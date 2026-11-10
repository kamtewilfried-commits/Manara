package com.manara.crm.dto;

public class ActivityRevenueDTO {
    private String activityName;
    private Double revenue;

    public ActivityRevenueDTO(String activityName, Double revenue) {
        this.activityName = activityName;
        this.revenue = revenue;
    }

    public String getActivityName() {
        return activityName;
    }

    public Double getRevenue() {
        return revenue;
    }
}