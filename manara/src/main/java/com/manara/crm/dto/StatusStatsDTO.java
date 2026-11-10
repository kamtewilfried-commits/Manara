package com.manara.crm.dto;

public class StatusStatsDTO {
    private String status;
    private Long count;

    public StatusStatsDTO(String status, Long count) {
        this.status = status;
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public Long getCount() {
        return count;
    }
}