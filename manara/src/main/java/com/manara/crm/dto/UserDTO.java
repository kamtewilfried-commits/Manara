package com.manara.crm.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class UserDTO {

    private Long id;

    private String firstName;
    private String lastName;

    private String email;
    private String phone;

    private boolean verified;
    private boolean twoFactorEnabled;

    private String role;
    private Set<String> roles;

    private LocalDateTime createdAt;

    private String status;
    private boolean active;


    private String fullName;

    private String password;

    public UserDTO() {}

    public UserDTO(Long id, String firstName, String lastName, String email, String phone,
                   boolean verified, boolean twoFactorEnabled, String role, Set<String> roles,
                   LocalDateTime createdAt, String status, boolean active, String fullName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.verified = verified;
        this.twoFactorEnabled = twoFactorEnabled;
        this.role = role;
        this.roles = roles;
        this.createdAt = createdAt;
        this.status = status;
        this.active = active;
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateFullName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateFullName();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    // 🔥 construit automatiquement le nom complet
    private void updateFullName() {
        this.fullName = (firstName != null ? firstName : "") + " " +
                (lastName != null ? lastName : "");
    }
}