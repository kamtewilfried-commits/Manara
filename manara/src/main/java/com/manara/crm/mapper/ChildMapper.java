package com.manara.crm.mapper;

import com.manara.crm.dto.ChildDTO;
import com.manara.crm.entity.Child;
import com.manara.crm.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ChildMapper {

    public static ChildDTO toDTO(Child child) {
        if (child == null) {
            return null;
        }

        ChildDTO dto = new ChildDTO();
        dto.setId(child.getId());
        dto.setFirstName(child.getFirstName());
        dto.setLastName(child.getLastName());
        dto.setBirthDate(child.getBirthDate());
        dto.setAllergies(child.getAllergies());
        dto.setMedicalNotes(child.getMedicalNotes());

        User parent = child.getParent();
        if (parent != null) {
            dto.setParentId(parent.getId());
            dto.setParentFullName(buildFullName(parent.getFirstName(), parent.getLastName()));
        }

        return dto;
    }

    public static List<ChildDTO> toDTOList(List<Child> children) {
        if (children == null) {
            return Collections.emptyList();
        }

        return children.stream()
                .map(ChildMapper::toDTO)
                .collect(Collectors.toList());
    }

    private static String buildFullName(String firstName, String lastName) {
        String first = firstName != null ? firstName : "";
        String last = lastName != null ? lastName : "";
        return (first + " " + last).trim();
    }
}