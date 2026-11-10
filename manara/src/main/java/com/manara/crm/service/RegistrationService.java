package com.manara.crm.service;

import com.manara.crm.entity.Child;
import com.manara.crm.entity.Registration;
import com.manara.crm.entity.Session;
import com.manara.crm.entity.User;
import com.manara.crm.repos.ChildRepository;
import com.manara.crm.repos.RegistrationRepository;
import com.manara.crm.repos.SessionRepository;
import com.manara.crm.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class RegistrationService {

    @Autowired private RegistrationRepository registrationRepository;
    @Autowired private SessionRepository      sessionRepository;
    @Autowired private ChildRepository        childRepository;
    @Autowired private UserRepository         userRepository;
    @Autowired private NotificationService    notificationService;

    public Registration findById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable : " + id));
    }

    public List<Registration> findAll() {
        return registrationRepository.findAll();
    }

    public List<Registration> findByParentId(Long parentId) {
        return registrationRepository.findByParentId(parentId);
    }

    public List<Registration> findBySessionId(Long sessionId) {
        return registrationRepository.findBySessionId(sessionId);
    }

    public List<Registration> findRecent(int limit) {
        return registrationRepository.findRecent(PageRequest.of(0, limit));
    }
    /*public List<Registration> findByAnimateurId(Long animateurId) {
        return registrationRepository.findBySessionActivityAnimateurId(animateurId);
    }*/
    public List<Registration> findByAnimateurId(Long animateurId) {
        return registrationRepository.findByAnimateurId(animateurId);
    }
    public long countActives(Long parentId) {
        return registrationRepository.countActivesByParentId(parentId);
    }

    public long countPending(Long parentId) {
        return registrationRepository.countPendingByParentId(parentId);
    }

    public long countAll() {
        return registrationRepository.countAllActive();
    }

    public long countPendingAll() {
        return registrationRepository.countAllPending();
    }

    public long countByAnimateurId(Long animateurId) {
        return registrationRepository.countChildrenByAnimateurId(animateurId);
    }

    public List<Registration> findUpcoming(Long parentId) {
        return registrationRepository.findUpcomingByParentId(parentId, LocalDateTime.now());
    }
    public void confirmerByAnimateur(Long id, Long animateurId) {
        Registration registration = findById(id);

        User animateur = userRepository.findById(animateurId)
                .orElseThrow(() -> new RuntimeException("Animateur introuvable"));

        registration.setStatus("CONFIRMED");
        registration.setConfirmedBy(animateur);

        registrationRepository.save(registration);
    }
    // ---- Inscrire un enfant à une session ----
    @Transactional
    public Registration inscrire(Long childId, Long sessionId) {
        if (registrationRepository.existsByChildIdAndSessionId(childId, sessionId)) {
            throw new RuntimeException("Cet enfant est déjà inscrit à cette session.");
        }

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session introuvable."));
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Enfant introuvable."));

        Registration reg = new Registration();
        reg.setChild(child);
        reg.setSession(session);

        boolean enListeAttente = session.getAvailableSpots() <= 0;
        reg.setWaitlist(enListeAttente);
        reg.setStatus(enListeAttente ? "PENDING" : "PENDING");

        return registrationRepository.save(reg);
    }

    // ---- Confirmer (admin) ----
    @Transactional
    public void confirmer(Long id, Long adminId) {
        Registration reg = findById(id);
        reg.setStatus("CONFIRMED");
        User admin = userRepository.findById(adminId).orElse(null);
        reg.setConfirmedBy(admin);
        registrationRepository.save(reg);
        notificationService.envoyerConfirmation(reg);
    }

    // ---- Annuler ----
    @Transactional
    public void annuler(Long id) {
        Registration reg = findById(id);
        reg.setStatus("CANCELLED");
        registrationRepository.save(reg);
        // Promouvoir le premier en liste d'attente si place libérée
        if (!reg.isWaitlist()) {
            promouvoirPremierEnAttente(reg.getSession().getId());
        }
        notificationService.envoyerAnnulation(reg);
    }
    @Transactional
    public void cancel(Long id) {
        Registration reg = findById(id);
        reg.setStatus("CANCELLED");
        registrationRepository.save(reg);

    }

    // ---- Promouvoir de la liste d'attente ----
    @Transactional
    public void promouvoir(Long id, Long adminId) {
        Registration reg = findById(id);
        if (!reg.isWaitlist()) {
            throw new RuntimeException("Cette inscription n'est pas en liste d'attente.");
        }
        reg.setWaitlist(false);
        reg.setStatus("CONFIRMED");
        User admin = userRepository.findById(adminId).orElse(null);
        reg.setConfirmedBy(admin);
        registrationRepository.save(reg);
        notificationService.envoyerConfirmation(reg);
    }

    private void promouvoirPremierEnAttente(Long sessionId) {
        List<Registration> attente = registrationRepository.findWaitlistBySessionId(sessionId);
        if (!attente.isEmpty()) {
            Registration premier = attente.get(0);
            premier.setWaitlist(false);
            premier.setStatus("PENDING");
            registrationRepository.save(premier);
        }
    }

    // ---- Présences ----
    @Transactional
    public void savePresences(Long sessionId, List<Long> childPresents) {
        List<Registration> regs = registrationRepository.findBySessionId(sessionId);
        for (Registration reg : regs) {
            boolean present = childPresents != null
                    && childPresents.contains(reg.getChild().getId());
            reg.setPresent(present);
            registrationRepository.save(reg);
        }
    }

    // ---- Événements planning parent ----
    public List<Map<String, Object>> getPlanningEvents(Long parentId, String semaine) {
        List<Registration> regs = findUpcoming(parentId);
        List<Map<String, Object>> events = new ArrayList<>();
        for (Registration r : regs) {
            if (r.getStatus().equals("CANCELLED")) continue;
            Map<String, Object> event = new HashMap<>();
            event.put("activityName", r.getSession().getActivity().getName());
            event.put("childName",    r.getChild().getFirstName());
            event.put("location",     r.getSession().getLocation());
            event.put("status",       r.getStatus());
            event.put("slot", String.format("%02dh00", r.getSession().getStartDate().getHour()));
            event.put("day", r.getSession().getStartDate().format(
                    java.time.format.DateTimeFormatter.ofPattern("EEE d", Locale.FRENCH)));
            events.add(event);
        }
        return events;
    }
}