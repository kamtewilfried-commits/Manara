package com.manara.crm.Rest;

import com.manara.crm.entity.PaymentTransaction;
import com.manara.crm.entity.Registration;
import com.manara.crm.repos.PaymentTransactionRepository;
import com.manara.crm.repos.RegistrationRepository;
import com.manara.crm.service.NotificationService;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    @Autowired
    private PaymentTransactionRepository transactionRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private NotificationService notificationService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Webhook invalide");
        }

        if ("checkout.session.completed".equals(event.getType())) {

            com.stripe.model.checkout.Session stripeSession =
                    (com.stripe.model.checkout.Session) event.getDataObjectDeserializer()
                            .getObject()
                            .orElse(null);

            if (stripeSession == null) {
                return ResponseEntity.badRequest().body("Session Stripe invalide");
            }

            String stripeSessionId = stripeSession.getId();

            PaymentTransaction transaction =
                    transactionRepository.findByStripeSessionId(stripeSessionId);

            if (transaction == null) {
                return ResponseEntity.badRequest().body("Transaction introuvable");
            }

            transaction.setStatus("PAID");

            List<Registration> registrations = transaction.getRegistrations();

            for (Registration registration : registrations) {
                String previousStatus = registration.getStatus();
                registration.setStatus("CONFIRMED");
                if (!"CONFIRMED".equals(previousStatus)) {
                    notificationService.envoyerConfirmation(registration);
                }
            }

            registrationRepository.saveAll(registrations);
            transactionRepository.save(transaction);
            notificationService.envoyerRecu(transaction);
        }

        return ResponseEntity.ok("OK");
    }
}
