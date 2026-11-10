package com.manara.crm.entity;
import com.manara.crm.entity.Registration;
import com.manara.crm.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User parent;

    private double totalAmount;

    private String status; // PENDING, PAID, FAILED, CANCELLED

    private String stripeSessionId;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<Registration> registrations = new ArrayList<>();

    public PaymentTransaction() {}

    public Long getId() {
        return id;
    }

    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStripeSessionId() {
        return stripeSessionId;
    }

    public void setStripeSessionId(String stripeSessionId) {
        this.stripeSessionId = stripeSessionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }
}