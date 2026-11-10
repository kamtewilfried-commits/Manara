package com.manara.crm.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(nullable = false)
    private String password;

    // email vérifié ou non
    private boolean verified = false;

    // 2FA SMS activé ou non
    private boolean twoFactorEnabled = true;

    @Column(nullable = false)
    private String role;

    @ManyToMany
    @JoinTable(
            name = "utilisateurs_roles",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"parent", "registrations"})

    private List<Child> children;

    private String status;

    private boolean active = true;

    private String emailVerificationToken;

    private LocalDateTime emailVerificationExpiry;

    public User() {
    }

    public User(Long id, String firstName, String lastName, String email, String phone, String password, boolean verified, boolean twoFactorEnabled, String role, Set<Role> roles, LocalDateTime createdAt, List<Child> children, String status, boolean active, String emailVerificationToken, LocalDateTime emailVerificationExpiry) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = normalizePhone(phone);
        this.password = password;
        this.verified = verified;
        this.twoFactorEnabled = twoFactorEnabled;
        this.role = role;
        this.roles = roles;
        this.createdAt = createdAt;
        this.children = children;
        this.status = status;
        this.active = active;
        this.emailVerificationToken = emailVerificationToken;
        this.emailVerificationExpiry = emailVerificationExpiry;
    }

    public User(String email, String phone, String password, String role) {
        this.email = email;
        this.phone = normalizePhone(phone);
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.active = true;
        this.verified = false;
        this.twoFactorEnabled = true;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public String getRole() {
        return role;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Child> getChildren() {
        return children;
    }

    public String getStatus() {
        return status;
    }

    public boolean isActive() {
        return active;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public LocalDateTime getEmailVerificationExpiry() {
        return emailVerificationExpiry;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = normalizePhone(phone);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public void setEmailVerificationExpiry(LocalDateTime emailVerificationExpiry) {
        this.emailVerificationExpiry = emailVerificationExpiry;
    }

    private String normalizePhone(String phone) {
        if (phone == null) return null;
        return phone.trim().replaceFirst("^\\+", "");
    }
}