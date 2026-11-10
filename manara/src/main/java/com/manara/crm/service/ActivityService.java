package com.manara.crm.service;

import com.manara.crm.entity.Activity;
import com.manara.crm.entity.User;
import com.manara.crm.repos.ActivityRepository;
import com.manara.crm.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
@Service
public class ActivityService {
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    private UserRepository userRepository;


    public Activity findById(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activité introuvable : " + id));
    }

    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    public List<Activity> findAllActive() {
        return activityRepository.findByActiveTrue();
    }

    public List<Activity> findAllActiveWithSessions() {
        return activityRepository.findActiveWithSessions();
    }

    public List<Activity> findByAnimateurId(Long animateurId) {
        return activityRepository.findByAnimateurId(animateurId);
    }

    public long countActive() {
        return activityRepository.countByActiveTrue();
    }

    public long countByAnimateurId(Long animateurId) {
        return activityRepository.findByAnimateurId(animateurId).size();
    }

    public Activity save(Activity activity) {

        if (activity.getAnimateur() != null && activity.getAnimateur().getId() != null) {

            User animateur = userRepository.findById(activity.getAnimateur().getId())
                    .orElseThrow(() -> new RuntimeException("Animateur introuvable"));

            if (!"ANIMATEUR".equalsIgnoreCase(animateur.getRole())) {
                throw new RuntimeException("L'utilisateur choisi n'a pas le rôle ANIMATEUR");
            }

            activity.setAnimateur(animateur);
        }
        return activityRepository.save(activity);
    }

    public void toggleActive(Long id) {
        Activity activity = findById(id);
        activity.setActive(!activity.isActive());
        activityRepository.save(activity);
    }

    // ---- Utilitaires planning ----
    public String getWeekLabel(String semaine) {
        LocalDate lundi = getLundi(semaine);
        LocalDate vendredi = lundi.plusDays(4);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d MMM", Locale.FRENCH);
        return "Semaine du " + lundi.format(fmt) + " au " + vendredi.format(fmt) + " " + lundi.getYear();
    }

    public List<String> getWeekDays(String semaine) {
        LocalDate lundi = getLundi(semaine);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE d", Locale.FRENCH);
        return List.of(
                lundi.format(fmt),
                lundi.plusDays(1).format(fmt),
                lundi.plusDays(2).format(fmt),
                lundi.plusDays(3).format(fmt),
                lundi.plusDays(4).format(fmt)
        );
    }

    public List<String> getTimeSlots() {
        return List.of("09h00", "10h00", "11h00", "13h00", "14h00", "15h00", "15h30", "16h00", "17h00");
    }

    public String getPreviousWeek(String semaine) {
        return getLundi(semaine).minusWeeks(1).toString();
    }

    public String getNextWeek(String semaine) {
        return getLundi(semaine).plusWeeks(1).toString();
    }

    private LocalDate getLundi(String semaine) {
        if (semaine == null || semaine.isBlank()) {
            LocalDate today = LocalDate.now();
            return today.with(WeekFields.of(Locale.FRENCH).dayOfWeek(), 1);
        }
        LocalDate date = LocalDate.parse(semaine);
        return date.with(WeekFields.of(Locale.FRENCH).dayOfWeek(), 1);
    }

    public List<Activity> afficherActivites(){
        return ( List<Activity> ) activityRepository.findAll();
    }

    public void delete(Long id) {
        Activity activity = findById(id);
        activityRepository.delete(activity);
    }
    public Activity uploadPhoto(Long id, MultipartFile file) {
        try {
            Activity activity = findById(id);

            if (file.isEmpty()) {
                throw new RuntimeException("Fichier vide");
            }

            String uploadDir = "uploads/activities/";
            Files.createDirectories(Paths.get(uploadDir));

            String originalName = file.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID() + extension;
            Path filePath = Paths.get(uploadDir + fileName);

            Files.write(filePath, file.getBytes());

            activity.setPhoto("/uploads/activities/" + fileName);

            return activityRepository.save(activity);

        } catch (Exception e) {
            throw new RuntimeException("Erreur upload photo : " + e.getMessage());
        }
    }
}
