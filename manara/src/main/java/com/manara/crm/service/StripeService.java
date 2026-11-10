package com.manara.crm.service;

import com.stripe.Stripe;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public com.stripe.model.checkout.Session createCheckoutSession(double amount, Long transactionId) throws Exception {

        Stripe.apiKey = stripeSecretKey;

        long amountInCents = Math.round(amount * 100);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4200/payment-success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:4200/cart")
                .putMetadata("transactionId", transactionId.toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("cad")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Inscription aux activités")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        return com.stripe.model.checkout.Session.create(params);
    }
}