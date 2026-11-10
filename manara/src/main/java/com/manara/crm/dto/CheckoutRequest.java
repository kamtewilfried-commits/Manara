package com.manara.crm.dto;

import java.util.List;

public class CheckoutRequest {

    private List<CheckoutItemRequest> items;

    public List<CheckoutItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CheckoutItemRequest> items) {
        this.items = items;
    }
}