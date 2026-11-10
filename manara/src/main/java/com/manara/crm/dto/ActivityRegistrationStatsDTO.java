package com.manara.crm.dto;

public class ActivityRegistrationStatsDTO {
    private String activityName;
    private Long registrationsCount;

    public ActivityRegistrationStatsDTO(String activityName, Long registrationsCount) {
        this.activityName = activityName;
        this.registrationsCount = registrationsCount;
    }

    public String getActivityName() {
        return activityName;
    }

    public Long getRegistrationsCount() {
        return registrationsCount;
    }
}