package com.manara.crm.mapper;

import com.manara.crm.dto.NotificationDTO;
import com.manara.crm.entity.Notification;
import com.manara.crm.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationMapper {

    public static NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setSubject(notification.getSubject());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setIsRead(notification.getIsRead());
        dto.setEmailSent(notification.getEmailSent());
        dto.setSentAt(notification.getSentAt());

        User user = notification.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setUserFullName(buildFullName(user.getFirstName(), user.getLastName()));
            dto.setUserEmail(user.getEmail());
        }

        return dto;
    }

    public static List<NotificationDTO> toDTOList(List<Notification> notifications) {
        if (notifications == null) {
            return Collections.emptyList();
        }

        return notifications.stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    private static String buildFullName(String firstName, String lastName) {
        String first = firstName != null ? firstName : "";
        String last = lastName != null ? lastName : "";
        return (first + " " + last).trim();
    }
}