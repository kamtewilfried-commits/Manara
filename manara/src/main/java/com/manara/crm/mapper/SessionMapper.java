package com.manara.crm.mapper;

import com.manara.crm.dto.SessionDTO;
import com.manara.crm.entity.Activity;
import com.manara.crm.entity.Session;
import com.manara.crm.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SessionMapper {

    public static SessionDTO toDTO(Session session) {
        if (session == null) {
            return null;
        }

        SessionDTO dto = new SessionDTO();
        dto.setId(session.getId());
        dto.setStartDate(session.getStartDate());
        dto.setEndDate(session.getEndDate());
        dto.setCapacity(session.getCapacity());
        dto.setAvailableSpots(calculateAvailableSpots(session));
        dto.setLocation(session.getLocation());
        dto.setStatus(session.getStatus());

        Activity activity = session.getActivity();
        if (activity != null) {
            dto.setActivityId(activity.getId());
            dto.setActivityName(activity.getName());
        }

        User animateur = session.getActivity().getAnimateur();
        if (animateur != null) {
            dto.setAnimateurId(animateur.getId());
            dto.setAnimateurName(buildFullName(animateur.getFirstName(), animateur.getLastName()));
        }

        return dto;
    }

    public static List<SessionDTO> toDTOList(List<Session> sessions) {
        if (sessions == null) {
            return Collections.emptyList();
        }

        return sessions.stream()
                .map(SessionMapper::toDTO)
                .collect(Collectors.toList());
    }

    private static Integer calculateAvailableSpots(Session session) {
        int capacity = session.getCapacity();
        int registrationCount = session.getRegistrations() != null ? session.getRegistrations().size() : 0;
        int available = capacity - registrationCount;
        return Math.max(available, 0);
    }

    private static String buildFullName(String firstName, String lastName) {
        String first = firstName != null ? firstName : "";
        String last = lastName != null ? lastName : "";
        return (first + " " + last).trim();
    }
}