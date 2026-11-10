package com.manara.crm.dto;

import java.time.LocalDateTime;

public class RegistrationDTO {

    private Long id;

    private String status;
    private boolean waitlist;
    private boolean present;
    private LocalDateTime createdAt;

    private Long childId;
    private String childFirstName;
    private String childLastName;
    private String childFullName;
    private String childAllergies;

    private Long sessionId;
    private String sessionLocation;
    private LocalDateTime sessionStartDate;
    private LocalDateTime sessionEndDate;

    private Long parentId;
    private String parentFullName;
    private String parentEmail;
    private String parentPhone;

    private Long confirmedById;
    private String confirmedByFullName;

    public RegistrationDTO() {
    }

    public RegistrationDTO(Long id, String status, boolean waitlist, boolean present, LocalDateTime createdAt,
                           Long childId, String childFirstName, String childLastName, String childFullName,
                           Long sessionId, String sessionLocation, LocalDateTime sessionStartDate, LocalDateTime sessionEndDate,
                           Long parentId, String parentFullName, String parentEmail,
                           Long confirmedById, String confirmedByFullName) {
        this.id = id;
        this.status = status;
        this.waitlist = waitlist;
        this.present = present;
        this.createdAt = createdAt;
        this.childId = childId;
        this.childFirstName = childFirstName;
        this.childLastName = childLastName;
        this.childFullName = childFullName;
        this.sessionId = sessionId;
        this.sessionLocation = sessionLocation;
        this.sessionStartDate = sessionStartDate;
        this.sessionEndDate = sessionEndDate;
        this.parentId = parentId;
        this.parentFullName = parentFullName;
        this.parentEmail = parentEmail;
        this.confirmedById = confirmedById;
        this.confirmedByFullName = confirmedByFullName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isWaitlist() {
        return waitlist;
    }

    public void setWaitlist(boolean waitlist) {
        this.waitlist = waitlist;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    public String getChildFirstName() {
        return childFirstName;
    }

    public void setChildFirstName(String childFirstName) {
        this.childFirstName = childFirstName;
        updateChildFullName();
    }

    public String getChildLastName() {
        return childLastName;
    }

    public void setChildLastName(String childLastName) {
        this.childLastName = childLastName;
        updateChildFullName();
    }

    public String getChildFullName() {
        return childFullName;
    }

    public void setChildFullName(String childFullName) {
        this.childFullName = childFullName;
    }

    public String getChildAllergies() {
        return childAllergies;
    }

    public void setChildAllergies(String childAllergies) {
        this.childAllergies = childAllergies;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionLocation() {
        return sessionLocation;
    }

    public void setSessionLocation(String sessionLocation) {
        this.sessionLocation = sessionLocation;
    }

    public LocalDateTime getSessionStartDate() {
        return sessionStartDate;
    }

    public void setSessionStartDate(LocalDateTime sessionStartDate) {
        this.sessionStartDate = sessionStartDate;
    }

    public LocalDateTime getSessionEndDate() {
        return sessionEndDate;
    }

    public void setSessionEndDate(LocalDateTime sessionEndDate) {
        this.sessionEndDate = sessionEndDate;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentFullName() {
        return parentFullName;
    }

    public void setParentFullName(String parentFullName) {
        this.parentFullName = parentFullName;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public Long getConfirmedById() {
        return confirmedById;
    }

    public void setConfirmedById(Long confirmedById) {
        this.confirmedById = confirmedById;
    }

    public String getConfirmedByFullName() {
        return confirmedByFullName;
    }

    public void setConfirmedByFullName(String confirmedByFullName) {
        this.confirmedByFullName = confirmedByFullName;
    }

    private void updateChildFullName() {
        String first = childFirstName != null ? childFirstName : "";
        String last = childLastName != null ? childLastName : "";
        this.childFullName = (first + " " + last).trim();
    }
}