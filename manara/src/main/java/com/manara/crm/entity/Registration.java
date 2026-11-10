package com.manara.crm.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(
        name = "registrations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"child_id", "session_id"})
        }
)
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private User parent;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private PaymentTransaction transaction;

    private String status; // PENDING_PAYMENT, CONFIRMED, CANCELLED

    private boolean waitlist = false;

    private boolean present = false;

    @ManyToOne
    @JoinColumn(name = "confirmed_by")
    private User confirmedBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Registration() {}

    public double getPrix() {
        if (session == null) {
            return 0;
        }
        return session.getPrixSession();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public PaymentTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(PaymentTransaction transaction) {
        this.transaction = transaction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isWaitlist() {
        return waitlist;
    }

    public void setWaitlist(boolean waitlist) {
        this.waitlist = waitlist;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public User getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(User confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}