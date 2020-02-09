package com.orderprio.data;

import java.util.ArrayList;
import java.util.List;

public class ShopMenuData {

    private String cuisineName;
    private String cuisinePrice;
    private boolean cuisineAvailability;

    public ShopMenuData(String cuisineName, String cuisinePrice, boolean cuisineAvailability) {
        this.cuisineName = cuisineName;
        this.cuisinePrice = cuisinePrice;
        this.cuisineAvailability = cuisineAvailability;
    }

    public String getCuisineName() {
        return cuisineName;
    }

    public void setCuisineName(String cuisineName) {
        this.cuisineName = cuisineName;
    }

    public String getCuisinePrice() {
        return cuisinePrice;
    }

    public void setCuisinePrice(String cuisinePrice) {
        this.cuisinePrice = cuisinePrice;
    }

    public boolean isCuisineAvailability() {
        return cuisineAvailability;
    }

    public void setCuisineAvailability(boolean cuisineAvailability) {
        this.cuisineAvailability = cuisineAvailability;
    }
}
