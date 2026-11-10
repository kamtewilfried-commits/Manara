package com.manara.crm.service;


import com.manara.crm.Twilio.EmailService;
import com.manara.crm.dto.NotificationForm;

import com.manara.crm.entity.Notification;
import com.manara.crm.entity.PaymentTransaction;
import com.manara.crm.entity.Registration;
import com.manara.crm.entity.User;
import com.manara.crm.repos.NotificationRepository;
import com.manara.crm.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private UserRepository         userRepository;
    @Autowired private EmailService           emailService;

    // ---- Lecture ----
    public List<Notification> findAll(Long userId) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId);
    }

    public List<Notification> findAllAdmin() {
        return notificationRepository.findAllOrderBySentAtDesc();
    }

    public List<Notification> findRecent(Long userId, int limit) {
        return notificationRepository.findRecentByUserId(userId, PageRequest.of(0, limit));
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // ---- Marquer comme lu ----
    public void markRead(Long id) {
        Notification notif = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification introuvable."));
        notif.setIsRead(true);
        notificationRepository.save(notif);
    }

    public void markAllRead(Long userId) {
        notificationRepository.markAllReadByUserId(userId);
    }

    // ---- Envois automatiques ----
    public void envoyerConfirmation(Registration reg) {
        User parent = reg.getChild().getParent();
        Notification notif = new Notification();
        notif.setUser(parent);
        notif.setSubject("Inscription confirmée — " + reg.getSession().getActivity().getName());
        notif.setMessage(
                reg.getChild().getFirstName()
                        + " est inscrit(e) à "
                        + reg.getSession().getActivity().getName()
                        + "."
        );
        notif.setType("CONFIRMATION");
        notif.setIsRead(false);
        notif.setEmailSent(false);
        sauvegarderEtEnvoyer(notif, parent.getEmail());
    }

    public void envoyerAnnulation(Registration reg) {
        User parent = reg.getChild().getParent();
        Notification notif = new Notification();
        notif.setUser(parent);
        notif.setSubject("Inscription annulée — " + reg.getSession().getActivity().getName());
        notif.setMessage(
                "L'inscription de "
                        + reg.getChild().getFirstName()
                        + " à "
                        + reg.getSession().getActivity().getName()
                        + " a été annulée."
        );
        notif.setType("CANCELLATION");
        notif.setIsRead(false);
        notif.setEmailSent(false);
        sauvegarderEtEnvoyer(notif, parent.getEmail());
    }

    public void envoyerRecu(PaymentTransaction transaction) {
        User parent = transaction.getParent();
        if (parent == null) return;

        java.util.List<Registration> registrations = transaction.getRegistrations();
        if (registrations == null || registrations.isEmpty()) return;

        StringBuilder msg = new StringBuilder("Paiement confirmé pour :\n");
        for (Registration reg : registrations) {
            msg.append("• ")
               .append(reg.getChild().getFirstName())
               .append(" — ")
               .append(reg.getSession().getActivity().getName())
               .append("\n");
        }
        msg.append(String.format("%nTotal : %.2f $CAD", transaction.getTotalAmount()));

        Notification notif = new Notification();
        notif.setUser(parent);
        notif.setSubject("Reçu de paiement");
        notif.setMessage(msg.toString());
        notif.setType("RECEIPT");
        notif.setIsRead(false);
        notif.setEmailSent(false);
        sauvegarderEtEnvoyer(notif, parent.getEmail());
    }



    // ---- Envoi admin vers parents ----
    public void envoyerAdmin(NotificationForm form) {
        List<User> destinataires;

        switch (form.getRecipientType()) {
            case "ONE" ->
                destinataires = userRepository.findById(form.getParentId())
                        .map(List::of)
                        .orElse(List.of());
            default ->
                destinataires = userRepository.findByRole("PARENT");
        }

        for (User parent : destinataires) {
            Notification notif = new Notification();
            notif.setUser(parent);
            notif.setSubject(form.getSubject());
            notif.setMessage(form.getMessage());
            notif.setType(form.getType());
            notif.setIsRead(false);
            notif.setEmailSent(false);
            sauvegarderEtEnvoyer(notif, parent.getEmail());
        }
    }

    public void envoyerAlerteSessionsExpirees(int deletedCount) {
        if (deletedCount <= 0) return;

        List<User> admins = userRepository.findByRole("ADMIN");
        for (User admin : admins) {
            Notification notif = new Notification();
            notif.setUser(admin);
            notif.setSubject("Sessions expirées supprimées");
            notif.setMessage(
                    deletedCount
                            + " session(s) expirée(s) ont été supprimée(s) automatiquement."
            );
            notif.setType("ADMIN_ALERT");
            notif.setIsRead(false);
            notif.setEmailSent(false);
            sauvegarderEtEnvoyer(notif, admin.getEmail());
        }
    }

    // ---- Helper : sauvegarde + tentative d'envoi email ----
    private void sauvegarderEtEnvoyer(Notification notif, String email) {
        notificationRepository.save(notif);
        try {
            emailService.sendSimple(email, notif.getSubject(), notif.getMessage());
            notif.setEmailSent(true);
            notificationRepository.save(notif);
        } catch (Exception e) {
            // email non envoyé — emailSent reste false
        }
    }

    }

