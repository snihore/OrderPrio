package com.orderprio.data;

public class ShopRegistrationData {

    private String shopName;
    private String address;
    private String email;
    private String mobile;
    private String district;
    private String state;
    private String zipCode;
    private String country;

    public ShopRegistrationData(String shopName, String address, String email, String mobile, String district, String state, String zipCode, String country) {
        this.shopName = shopName;
        this.address = address;
        this.email = email;
        this.mobile = mobile;
        this.district = district;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
