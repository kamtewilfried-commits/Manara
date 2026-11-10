package com.manara.crm.repos;

import com.manara.crm.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository  extends JpaRepository<PaymentTransaction, Long> {
    PaymentTransaction findByStripeSessionId(String stripeSessionId);
}
