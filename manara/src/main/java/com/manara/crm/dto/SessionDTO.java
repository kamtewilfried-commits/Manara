package com.manara.crm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class SessionDTO {

    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    private Integer capacity;
    private Integer availableSpots;

    private String location;
    private String status;

    // 🔗 Activity (simplifié)
    private Long activityId;
    private String activityName;

    // 🔗 Animateur (simplifié)
    private Long animateurId;
    private String animateurName;

    public SessionDTO() {}

    public SessionDTO(Long id, LocalDateTime startDate, LocalDateTime endDate,
                      Integer capacity, Integer availableSpots,
                      String location, String status,
                      Long activityId, String activityName,
                      Long animateurId, String animateurName) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.capacity = capacity;
        this.availableSpots = availableSpots;
        this.location = location;
        this.status = status;
        this.activityId = activityId;
        this.activityName = activityName;
        this.animateurId = animateurId;
        this.animateurName = animateurName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getAvailableSpots() {
        return availableSpots;
    }

    public void setAvailableSpots(Integer availableSpots) {
        this.availableSpots = availableSpots;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Long getAnimateurId() {
        return animateurId;
    }

    public void setAnimateurId(Long animateurId) {
        this.animateurId = animateurId;
    }

    public String getAnimateurName() {
        return animateurName;
    }

    public void setAnimateurName(String animateurName) {
        this.animateurName = animateurName;
    }
}