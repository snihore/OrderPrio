package com.orderprio.data;

public class QRCodeData {

    private String shopID;
    private String uniqueID;

    public QRCodeData(String shopID, String uniqueID) {
        this.shopID = shopID;
        this.uniqueID = uniqueID;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }
}
