package com.manara.crm.Rest;

import com.manara.crm.dto.ActivityDTO;
import com.manara.crm.entity.Activity;
import com.manara.crm.mapper.ActivityMapper;
import com.manara.crm.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
//@CrossOrigin(origins = "https://localhost:4200", allowCredentials = "true")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ActivityApiController {

    @Autowired
    private ActivityService activityService;

    @GetMapping
    public List<ActivityDTO> getAllActivities() {
        return ActivityMapper.toDTOList(activityService.findAll());
    }

    @GetMapping("/all")
    public List<ActivityDTO> getAfficherActivites() {
        return ActivityMapper.toDTOList(activityService.afficherActivites());
    }

    @GetMapping("/active")
    public List<ActivityDTO> getAllActiveActivities() {
        return ActivityMapper.toDTOList(activityService.findAllActive());
    }

    @GetMapping("/{id}")
    public ActivityDTO getActivityById(@PathVariable Long id) {
        return ActivityMapper.toDTO(activityService.findById(id));
    }

    @GetMapping(value="/animateur/{animateurId}", produces = "application/json")
    public List<ActivityDTO> getActivitiesByAnimateur(@PathVariable Long animateurId) {
        return ActivityMapper.toDTOList(activityService.findByAnimateurId(animateurId));
    }

    @GetMapping("/count/active")
    public long countActiveActivities() {
        return activityService.countActive();
    }

    @GetMapping("/count/animateur/{animateurId}")
    public long countByAnimateur(@PathVariable Long animateurId) {
        return activityService.countByAnimateurId(animateurId);
    }

    @PostMapping
    public ActivityDTO createActivity(@RequestBody Activity activity) {
        System.out.println("PRIX REÇU = " + activity.getPrixHoraire());

        Activity newActivity = new Activity();
        newActivity.setName(activity.getName());
        newActivity.setDescription(activity.getDescription());
        newActivity.setCategorie(activity.getCategorie());
        newActivity.setPrixHoraire(activity.getPrixHoraire());
        newActivity.setActive(activity.isActive());
        newActivity.setAnimateur(activity.getAnimateur());

        Activity savedActivity = activityService.save(newActivity);
        return ActivityMapper.toDTO(savedActivity);
    }

    @PutMapping("/{id}")
    public ActivityDTO updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        Activity existingActivity = activityService.findById(id);

        existingActivity.setName(activity.getName());
        existingActivity.setDescription(activity.getDescription());
        existingActivity.setActive(activity.isActive());
        existingActivity.setAnimateur(activity.getAnimateur());
        existingActivity.setCategorie(activity.getCategorie());
        existingActivity.setPrixHoraire(activity.getPrixHoraire());

        Activity updatedActivity = activityService.save(existingActivity);
        return ActivityMapper.toDTO(updatedActivity);
    }

    @PatchMapping("/{id}/toggle-active")
    public void toggleActivity(@PathVariable Long id) {
        activityService.toggleActive(id);
    }

    @GetMapping("/planning/week-label")
    public String getWeekLabel(@RequestParam(required = false) String semaine) {
        return activityService.getWeekLabel(semaine);
    }

    @GetMapping("/planning/week-days")
    public List<String> getWeekDays(@RequestParam(required = false) String semaine) {
        return activityService.getWeekDays(semaine);
    }

    @GetMapping("/planning/time-slots")
    public List<String> getTimeSlots() {
        return activityService.getTimeSlots();
    }

    @GetMapping("/planning/previous-week")
    public String getPreviousWeek(@RequestParam(required = false) String semaine) {
        return activityService.getPreviousWeek(semaine);
    }

    @GetMapping("/planning/next-week")
    public String getNextWeek(@RequestParam(required = false) String semaine) {
        return activityService.getNextWeek(semaine);
    }

    @GetMapping("/active-with-sessions")
    public List<ActivityDTO> getAllActiveActivitiesWithSessions() {
        return ActivityMapper.toDTOList(activityService.findAllActiveWithSessions());
    }
    @DeleteMapping("/{id}")
    public void deleteActivity(@PathVariable Long id) {
        activityService.delete(id);
    }
    @PostMapping("/{id}/photo")
    public ActivityDTO uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        Activity activity = activityService.uploadPhoto(id, file);
        return ActivityMapper.toDTO(activity);
    }
}