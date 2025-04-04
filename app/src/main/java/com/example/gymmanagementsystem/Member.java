package com.example.gymmanagementsystem;

public class Member {
    // NEW: Firestore document ID field
    private String id;

    private String name;
    private String phone;
    private String joiningDate;
    private String subscriptionType;
    private String endDate;

    // Empty constructor needed for Firestore deserialization
    public Member() {
    }

    // Updated constructor with id (can be set later)
    public Member(String id, String name, String phone, String joiningDate, String subscriptionType, String endDate) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.joiningDate = joiningDate;
        this.subscriptionType = subscriptionType;
        this.endDate = endDate;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) { // NEW setter for id
        this.id = id;
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
