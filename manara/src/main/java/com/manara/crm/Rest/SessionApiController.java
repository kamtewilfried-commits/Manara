package com.manara.crm.Rest;

import com.manara.crm.dto.SessionDTO;
import com.manara.crm.entity.Activity;
import com.manara.crm.entity.Session;
import com.manara.crm.entity.User;
import com.manara.crm.mapper.SessionMapper;
import com.manara.crm.repos.ActivityRepository;
import com.manara.crm.repos.UserRepository;
import com.manara.crm.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
//@CrossOrigin(origins = "https://localhost:4200", allowCredentials = "true")
public class SessionApiController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private ActivityRepository activityRepository;
    @GetMapping("/{id}")
    public SessionDTO getSessionById(@PathVariable Long id) {
        return SessionMapper.toDTO(sessionService.findById(id));
    }

    @GetMapping
    public List<SessionDTO> getAllSessions() {
        return SessionMapper.toDTOList(sessionService.findAll());
    }

    @GetMapping("/with-activity")
    public List<SessionDTO> getAllWithActivity() {
        return SessionMapper.toDTOList(sessionService.findAllWithActivity());
    }

    @GetMapping("/optional/{id}")
    public SessionDTO getOptionalSessionById(@PathVariable Long id) {
        Optional<Session> session = sessionService.optionalFindById(id);
        return session.map(SessionMapper::toDTO).orElse(null);
    }

    @GetMapping(value="/animateur/{animateurId}", produces = "application/json")
    public List<SessionDTO> getSessionsByAnimateur(@PathVariable Long animateurId) {
        return SessionMapper.toDTOList(sessionService.findByAnimateurId(animateurId));
    }

    @GetMapping("/animateur/{animateurId}/current")
    public SessionDTO getCurrentToday(@PathVariable Long animateurId) {
        return SessionMapper.toDTO(sessionService.findCurrentToday(animateurId));
    }

    @GetMapping("/animateur/{animateurId}/count-this-week")
    public long countThisWeek(@PathVariable Long animateurId) {
        return sessionService.countThisWeek(animateurId);
    }

    @GetMapping("/animateur/{animateurId}/upcoming")
    public List<SessionDTO> getUpcomingByAnimateur(
            @PathVariable Long animateurId,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return SessionMapper.toDTOList(sessionService.findUpcoming(animateurId, limit));
    }

    @GetMapping("/activity/{activityId}")
    public List<SessionDTO> getSessionsByActivity(@PathVariable Long activityId) {
        return SessionMapper.toDTOList(sessionService.findByActivityId(activityId));
    }

    @GetMapping("/between")
    public List<SessionDTO> getSessionsBetween(
            @RequestParam String from,
            @RequestParam String to
    ) {
        return SessionMapper.toDTOList(
                sessionService.findByStartDateBetween(
                        LocalDateTime.parse(from),
                        LocalDateTime.parse(to)
                )
        );
    }

    @GetMapping("/upcoming")
    public List<SessionDTO> getUpcomingSessions() {
        return SessionMapper.toDTOList(sessionService.findUpcomingSessions());
    }

    @PostMapping
    public SessionDTO createSession(@RequestBody SessionDTO dto) {
        Session session = new Session();

        session.setStartDate(dto.getStartDate());
        session.setEndDate(dto.getEndDate());
        session.setLocation(dto.getLocation());
        session.setCapacity(dto.getCapacity());
        session.setStatus(dto.getStatus());

        if (dto.getActivityId() != null) {
            Activity activity = activityRepository.findById(dto.getActivityId())
                    .orElseThrow(() -> new RuntimeException("Activité introuvable"));
            session.setActivity(activity);
        }

        return SessionMapper.toDTO(sessionService.save(session));
    }
    @PostMapping("/animateur/{animateurId}")
    public SessionDTO createSessionForAnimateur(
            @PathVariable Long animateurId,
            @RequestBody SessionDTO dto
    ) {

        if (dto.getActivityId() == null) {
            throw new RuntimeException("ActivityId manquant");
        }

        Activity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new RuntimeException("Activité introuvable"));

        Session session = new Session();
        session.setStartDate(dto.getStartDate());
        session.setEndDate(dto.getEndDate());
        session.setLocation(dto.getLocation());
        session.setCapacity(dto.getCapacity());
        session.setStatus(dto.getStatus());

        // 🔥 IMPORTANT
        session.setActivity(activity);

        // 🔥 UTILISE animateurId (FIX PRINCIPAL)
        User animateur = userRepository.findById(animateurId)
                .orElseThrow(() -> new RuntimeException("Animateur introuvable"));

        session.setAnimateur(animateur);

        return SessionMapper.toDTO(sessionService.save(session));
    }
    @PutMapping("/{id}")
    public SessionDTO updateSession(@PathVariable Long id, @RequestBody SessionDTO dto) {
        Session existingSession = sessionService.findById(id);

        existingSession.setStartDate(dto.getStartDate());
        existingSession.setEndDate(dto.getEndDate());
        existingSession.setLocation(dto.getLocation());
        existingSession.setCapacity(dto.getCapacity());
        existingSession.setStatus(dto.getStatus());

        if (dto.getActivityId() != null) {
            Activity activity = activityRepository.findById(dto.getActivityId())
                    .orElseThrow(() -> new RuntimeException("Activité introuvable"));
            existingSession.setActivity(activity);
        }

        return SessionMapper.toDTO(sessionService.save(existingSession));
    }
    @PatchMapping("/{id}/cancel")
    public void cancelSession(@PathVariable Long id) {
        sessionService.annuler(id);
    }

    @DeleteMapping("/{id}")
    public void deleteSession(@PathVariable Long id) {
        sessionService.deleteById(id);
    }

    @PostMapping("/admin/run-cleanup-now")
    public Map<String, Object> runCleanupNow() {
        int deletedCount = sessionService.cleanupPastSessionsNow();
        return Map.of(
                "message", "Cleanup des sessions expirées exécuté.",
                "deletedSessions", deletedCount
        );
    }

    @GetMapping("/planning-events/{animateurId}")
    public List<Map<String, Object>> getPlanningEvents(
            @PathVariable Long animateurId,
            @RequestParam(required = false) String semaine
    ) {
        return sessionService.getPlanningEvents(animateurId, semaine);
    }
}