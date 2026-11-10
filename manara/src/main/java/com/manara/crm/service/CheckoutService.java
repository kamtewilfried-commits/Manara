package com.manara.crm.service;

import com.manara.crm.dto.CheckoutItemRequest;
import com.manara.crm.dto.CheckoutRequest;
import com.manara.crm.entity.*;
import com.manara.crm.repos.ChildRepository;
import com.manara.crm.repos.PaymentTransactionRepository;
import com.manara.crm.repos.RegistrationRepository;
import com.manara.crm.repos.SessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CheckoutService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private PaymentTransactionRepository transactionRepository;

    @Autowired
    private StripeService stripeService;

    @Transactional
    public String checkout(CheckoutRequest request, User parent) throws Exception {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Le panier est vide.");
        }

        double total = 0;
        List<Registration> registrations = new ArrayList<>();

        for (CheckoutItemRequest item : request.getItems()) {

            Session session = sessionRepository.findById(item.getSessionId())
                    .orElseThrow(() -> new RuntimeException("Session introuvable."));

            Child child = childRepository.findById(item.getChildId())
                    .orElseThrow(() -> new RuntimeException("Enfant introuvable."));

            if (session.getAvailableSpots() <= 0) {
                throw new RuntimeException("La session est complète.");
            }

            boolean alreadyRegistered = registrationRepository.existsByChildAndSession(child, session);

            if (alreadyRegistered) {
                throw new RuntimeException("Cet enfant est déjà inscrit à cette session.");
            }

            double prixSession = session.getPrixSession();
            total += prixSession;

            Registration registration = new Registration();
            registration.setChild(child);
            registration.setSession(session);
            registration.setParent(parent);
            registration.setStatus("PENDING_PAYMENT");
            registration.setWaitlist(false);
            registration.setPresent(false);

            registrations.add(registration);
        }

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setParent(parent);
        transaction.setTotalAmount(total);
        transaction.setStatus("PENDING");

        transactionRepository.save(transaction);

        for (Registration registration : registrations) {
            registration.setTransaction(transaction);
        }

        registrationRepository.saveAll(registrations);

        com.stripe.model.checkout.Session stripeSession =
                stripeService.createCheckoutSession(total, transaction.getId());

        transaction.setStripeSessionId(stripeSession.getId());
        transactionRepository.save(transaction);

        return stripeSession.getUrl();
    }
}