package com.manara.crm.mapper;

import com.manara.crm.dto.RegistrationDTO;
import com.manara.crm.entity.Child;
import com.manara.crm.entity.Registration;
import com.manara.crm.entity.Session;
import com.manara.crm.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RegistrationMapper {

    public static RegistrationDTO toDTO(Registration registration) {
        if (registration == null) {
            return null;
        }

        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(registration.getId());
        dto.setStatus(registration.getStatus());
        dto.setWaitlist(registration.isWaitlist());
        dto.setPresent(registration.isPresent());
        dto.setCreatedAt(registration.getCreatedAt());

        Child child = registration.getChild();
        if (child != null) {
            dto.setChildId(child.getId());
            dto.setChildFirstName(child.getFirstName());
            dto.setChildLastName(child.getLastName());
            dto.setChildAllergies(child.getAllergies());
        }

        Session session = registration.getSession();
        if (session != null) {
            dto.setSessionId(session.getId());
            dto.setSessionLocation(session.getLocation());
            dto.setSessionStartDate(session.getStartDate());
            dto.setSessionEndDate(session.getEndDate());
        }

        User parent = registration.getParent();
        if (parent != null) {
            dto.setParentId(parent.getId());
            dto.setParentFullName(buildFullName(parent.getFirstName(), parent.getLastName()));
            dto.setParentEmail(parent.getEmail());
            dto.setParentPhone(parent.getPhone());
        }

        User confirmedBy = registration.getConfirmedBy();
        if (confirmedBy != null) {
            dto.setConfirmedById(confirmedBy.getId());
            dto.setConfirmedByFullName(buildFullName(confirmedBy.getFirstName(), confirmedBy.getLastName()));
        }

        return dto;
    }

    public static List<RegistrationDTO> toDTOList(List<Registration> registrations) {
        if (registrations == null) {
            return Collections.emptyList();
        }

        return registrations.stream()
                .map(RegistrationMapper::toDTO)
                .collect(Collectors.toList());
    }

    private static String buildFullName(String firstName, String lastName) {
        String first = firstName != null ? firstName : "";
        String last = lastName != null ? lastName : "";
        return (first + " " + last).trim();
    }
}