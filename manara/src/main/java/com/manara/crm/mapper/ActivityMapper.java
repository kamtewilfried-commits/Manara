package com.manara.crm.mapper;

import com.manara.crm.dto.ActivityDTO;
import com.manara.crm.dto.SessionDTO;
import com.manara.crm.entity.Activity;
import com.manara.crm.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityMapper {

    public static ActivityDTO toDTO(Activity activity) {
        if (activity == null) {
            return null;
        }

        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setName(activity.getName());
        dto.setDescription(activity.getDescription());
        dto.setActive(activity.isActive());
        dto.setCategorie(activity.getCategorie());
        dto.setPhoto(activity.getPhoto());
        dto.setPrixHoraire(activity.getPrixHoraire());
        dto.setSessionCount(activity.getSessions() != null ? activity.getSessions().size() : 0);

        User animateur = activity.getAnimateur();
        if (animateur != null) {
            dto.setAnimateurId(animateur.getId());
            dto.setAnimateurFullName(buildFullName(animateur.getFirstName(), animateur.getLastName()));
            dto.setAnimateurEmail(animateur.getEmail());
        }
        if (activity.getSessions() != null) {
            dto.setSessions(
                    activity.getSessions().stream()
                            .map(session -> new SessionDTO(
                                    session.getId(),
                                    session.getStartDate(),
                                    session.getEndDate(),
                                    session.getCapacity(),
                                    session.getAvailableSpots(),
                                    session.getLocation(),
                                    session.getStatus(),

                                    // 🔗 Activity
                                    activity.getId(),
                                    activity.getName(),

                                    // 🔗 Animateur
                                    session.getAnimateur() != null ? session.getAnimateur().getId() : null,
                                    session.getAnimateur() != null
                                            ? session.getAnimateur().getFirstName() + " " + session.getAnimateur().getLastName()
                                            : null
                            ))
                            .toList()
            );
        }

        return dto;
    }

    public static List<ActivityDTO> toDTOList(List<Activity> activities) {
        if (activities == null) {
            return Collections.emptyList();
        }

        return activities.stream()
                .map(ActivityMapper::toDTO)
                .collect(Collectors.toList());
    }

    private static String buildFullName(String firstName, String lastName) {
        String first = firstName != null ? firstName : "";
        String last = lastName != null ? lastName : "";
        return (first + " " + last).trim();
    }
}