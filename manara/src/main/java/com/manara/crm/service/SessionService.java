package com.manara.crm.service;

import com.manara.crm.entity.Activity;
import com.manara.crm.entity.Session;
import com.manara.crm.repos.ActivityRepository;
import com.manara.crm.repos.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
public class SessionService {
@Autowired
   SessionRepository sessionRepository;
@Autowired
    ActivityRepository activityRepository;
@Autowired
    NotificationService notificationService;

    public Session findById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session introuvable : " + id));
    }

    public List<Session> findAll() {
        return sessionRepository.findAll();
    }


   /* public List<Session> findByAnimateurId(Long animateurId) {
        return sessionRepository.findByAnimateurId(animateurId);
    }*/
   public List<Session> findByAnimateurId(Long animateurId) {
       return sessionRepository.findByActivityAnimateurId(animateurId);
   }



    public Session findCurrentToday(Long animateurId) {
        return sessionRepository.findCurrentSession(animateurId, LocalDateTime.now())
                .orElse(null);
    }
    public long countThisWeek(Long animateurId) {
        LocalDate today = LocalDate.now();
        LocalDate lundi = today.with(WeekFields.of(Locale.FRENCH).dayOfWeek(), 1);
        return sessionRepository.countThisWeek(
                animateurId,
                lundi.atStartOfDay(),
                lundi.plusDays(4).atTime(LocalTime.MAX)
        );
    }

    public List<Session> findUpcoming(Long animateurId, int limit) {
        return sessionRepository.findUpcomingByAnimateur(animateurId, LocalDateTime.now())
                .stream().limit(limit).toList();
    }

    public Session save(Session session) {

        if (session.getActivity() != null && session.getActivity().getId() != null) {


            Activity activity = activityRepository.findById(session.getActivity().getId())
                    .orElseThrow(() -> new RuntimeException("Activité introuvable"));

            session.setActivity(activity);
            session.setAnimateur(activity.getAnimateur());
        } else {
            throw new RuntimeException("Activité obligatoire");
        }

        return sessionRepository.save(session);
    }

    public void annuler(Long id) {
        Session session = findById(id);
        session.setStatus("CANCELLED");
        sessionRepository.save(session);
    }

    // Événements planning pour l'animateur
    public List<Map<String, Object>> getPlanningEvents(Long animateurId, String semaine) {
        List<Session> sessions = findByAnimateurId(animateurId);
        List<Map<String, Object>> events = new ArrayList<>();

        for (Session s : sessions) {
            if (s.getStatus().equals("CANCELLED")) continue;
            Map<String, Object> event = new HashMap<>();
            event.put("sessionId",    s.getId());
            event.put("activityName", s.getActivity().getName());
            event.put("location",     s.getLocation());
            event.put("inscrits",     s.getCapacity() - s.getAvailableSpots());
            // slot = heure de début au format HHh00
            event.put("slot", String.format("%02dh00", s.getStartDate().getHour()));
            // day = jour abrégé ex: "Lun 7"
            event.put("day", s.getStartDate().format(
                    java.time.format.DateTimeFormatter.ofPattern("EEE d", Locale.FRENCH)));
            events.add(event);
        }
        return events;
    }

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public List<Session> findAllWithActivity() {
        return sessionRepository.findAllWithActivity();
    }

    public Optional<Session> optionalFindById(Long id) {
        return sessionRepository.findById(id);
    }

    public List<Session> findByActivityId(Long activityId) {
        return sessionRepository.findByActivityId(activityId);
    }

    public List<Session> findByStartDateBetween(LocalDateTime from, LocalDateTime to) {
        return sessionRepository.findByStartDateBetween(from, to);
    }

    public List<Session> findUpcomingSessions() {
        return sessionRepository.findByStartDateAfterOrderByStartDateAsc(LocalDateTime.now());
    }



    public void deleteById(Long id) {
        sessionRepository.deleteById(id);
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupPastSessions() {
        cleanupPastSessionsNow();
    }

    @Transactional
    public int cleanupPastSessionsNow() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        List<Session> oldSessions = sessionRepository.findByStartDateBeforeOrderByStartDateDesc(startOfToday);
        if (oldSessions.isEmpty()) {
            return 0;
        }
        int deletedCount = oldSessions.size();
        sessionRepository.deleteAll(oldSessions);
        notificationService.envoyerAlerteSessionsExpirees(deletedCount);
        return deletedCount;
    }

}