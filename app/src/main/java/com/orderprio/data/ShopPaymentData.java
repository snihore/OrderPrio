package com.orderprio.data;

public class ShopPaymentData {

    private String shopID;
    private String upiID;
    private String upiName;

    public ShopPaymentData() {
    }

    public ShopPaymentData(String shopID, String upiID, String upiName) {
        this.shopID = shopID;
        this.upiID = upiID;
        this.upiName = upiName;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getUpiID() {
        return upiID;
    }

    public void setUpiID(String upiID) {
        this.upiID = upiID;
    }

    public String getUpiName() {
        return upiName;
    }

    public void setUpiName(String upiName) {
        this.upiName = upiName;
    }
}
