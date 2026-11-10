package com.manara.crm.controller;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PayementController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping("/checkout")
    public String checkoutPage(Model model) {
        model.addAttribute("productName", "Faire un Don");
        model.addAttribute("amount", 10.00);
        return "checkout";
    }

    @PostMapping("/create-checkout-session")
    public String createCheckoutSession() throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(baseUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(baseUrl + "/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("cad")
                                                .setUnitAmount(1000L) // 10.00 CAD in cents
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Premium Membership")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);

        return "redirect:" + session.getUrl();
    }

    @GetMapping("/success")
    public String successPage(String session_id, Model model) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        Session session = Session.retrieve(session_id);

        model.addAttribute("customerEmail", session.getCustomerDetails() != null
                ? session.getCustomerDetails().getEmail()
                : "N/A");

        model.addAttribute("amountTotal", session.getAmountTotal() != null
                ? session.getAmountTotal() / 100.0
                : 0);

        return "success";
    }

    @GetMapping("/cancel")
    public String cancelPage() {
        return "cancel";
    }
}