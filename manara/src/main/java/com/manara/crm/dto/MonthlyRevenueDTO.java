package com.manara.crm.dto;

public class MonthlyRevenueDTO {
    private Integer year;
    private Integer month;
    private Double revenue;

    public MonthlyRevenueDTO(Integer year, Integer month, Double revenue) {
        this.year = year;
        this.month = month;
        this.revenue = revenue;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Double getRevenue() {
        return revenue;
    }
}