package com.manara.crm.dto;

import java.time.LocalDateTime;

public class NotificationDTO {

    private Long id;

    private String subject;
    private String message;
    private String type;

    private Boolean isRead;
    private Boolean emailSent;

    private LocalDateTime sentAt;

    // 🔗 User simplifié
    private Long userId;
    private String userFullName;
    private String userEmail;

    public NotificationDTO() {}

    public NotificationDTO(Long id, String subject, String message, String type,
                           Boolean isRead, Boolean emailSent, LocalDateTime sentAt,
                           Long userId, String userFullName, String userEmail) {
        this.id = id;
        this.subject = subject;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.emailSent = emailSent;
        this.sentAt = sentAt;
        this.userId = userId;
        this.userFullName = userFullName;
        this.userEmail = userEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getEmailSent() {
        return emailSent;
    }

    public void setEmailSent(Boolean emailSent) {
        this.emailSent = emailSent;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}