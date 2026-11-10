package com.manara.crm.dto;

public class ActivityCategoryStatsDTO {
    private String categorie;
    private Long count;

    public ActivityCategoryStatsDTO(String categorie, Long count) {
        this.categorie = categorie;
        this.count = count;
    }

    public String getCategorie() {
        return categorie;
    }

    public Long getCount() {
        return count;
    }
}