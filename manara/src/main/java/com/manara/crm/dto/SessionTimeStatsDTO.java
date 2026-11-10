package com.manara.crm.dto;

public class SessionTimeStatsDTO {
    private Integer year;
    private Integer month;
    private Long count;

    public SessionTimeStatsDTO(Integer year, Integer month, Long count) {
        this.year = year;
        this.month = month;
        this.count = count;
    }

    public Integer getYear() { return year; }
    public Integer getMonth() { return month; }
    public Long getCount() { return count; }
}