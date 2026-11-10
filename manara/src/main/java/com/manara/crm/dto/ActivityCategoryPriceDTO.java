package com.manara.crm.dto;

public class ActivityCategoryPriceDTO {
    private String categorie;
    private Double averagePrice;

    public ActivityCategoryPriceDTO(String categorie, Double averagePrice) {
        this.categorie = categorie;
        this.averagePrice = averagePrice;
    }

    public String getCategorie() {
        return categorie;
    }

    public Double getAveragePrice() {
        return averagePrice;
    }
}