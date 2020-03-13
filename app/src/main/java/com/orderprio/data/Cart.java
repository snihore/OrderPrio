package com.orderprio.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Cart implements Serializable {

    private Set<String> cuisineNames;
    private HashMap<String, Integer> cuisineQuantities;
    private HashMap<String, Integer> cuisinePrices;
    private int totalItems;
    private String shopID;

    public Cart(String shopID) {

        this.shopID = shopID;
        cuisineNames = new HashSet<>();
        cuisineQuantities = new HashMap<>();
        cuisinePrices = new HashMap<>();
        totalItems = 0;

    }

    public void addCuisineName(String name){
       if(!cuisineNames.contains(name)){
           cuisineNames.add(name);
           totalItems += 1;
       }
    }


    public void addCuisineQuantity(String name, int quantity){
        if(cuisineQuantities.containsKey(name)){
            quantity += cuisineQuantities.get(name);

        }
        cuisineQuantities.put(name, quantity);
    }

    public void addCuisinePrice(String name, int price){
        if(cuisinePrices.containsKey(name)){
            price += cuisinePrices.get(name);
        }
        cuisinePrices.put(name, price);
    }

    public Set<String> getCuisineNames() {
        return cuisineNames;
    }

    public HashMap<String, Integer> getCuisineQuantities() {
        return cuisineQuantities;
    }

    public HashMap<String, Integer> getCuisinePrices() {
        return cuisinePrices;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public String getShopID() {
        return shopID;
    }
}
