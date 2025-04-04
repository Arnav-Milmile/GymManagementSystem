package com.example.gymmanagementsystem;

public class Member {
    private String name;
    private String phone;
    private String joiningDate;
    private String subscriptionType;
    private String endDate;

    public Member(String name, String phone, String joiningDate, String subscriptionType, String endDate) {
        this.name = name;
        this.phone = phone;
        this.joiningDate = joiningDate;
        this.subscriptionType = subscriptionType;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public String getEndDate() {
        return endDate;
    }
}
