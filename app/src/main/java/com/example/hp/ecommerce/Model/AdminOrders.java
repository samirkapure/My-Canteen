package com.example.hp.ecommerce.Model;

public class AdminOrders
{
    private String name, address, phone,date,state,time,totalPrice;

    public AdminOrders() {    }

    public AdminOrders(String name, String address, String phone, String date, String state, String time, String totalPrice) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.date = date;
        this.state = state;
        this.time = time;
        this.totalPrice = totalPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
