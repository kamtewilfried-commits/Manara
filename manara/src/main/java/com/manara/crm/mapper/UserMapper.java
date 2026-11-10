package com.manara.crm.mapper;

import com.manara.crm.dto.UserDTO;
import com.manara.crm.entity.Role;
import com.manara.crm.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setVerified(user.isVerified());
        dto.setTwoFactorEnabled(user.isTwoFactorEnabled());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setStatus(user.getStatus());
        dto.setActive(user.isActive());

        return dto;
    }

    public static List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }

        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }


}