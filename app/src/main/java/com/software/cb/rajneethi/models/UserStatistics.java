package com.software.cb.rajneethi.models;

public class UserStatistics {

    private String boothName,verified, rejected,unverified, total;

    public UserStatistics(String boothName, String verified, String rejected, String unverified, String total) {
        this.boothName = boothName;
        this.verified = verified;
        this.rejected = rejected;
        this.unverified = unverified;
        this.total = total;
    }

    public String getTotal() {
        return total;
    }

    public String getBoothName() {
        return boothName;
    }

    public String getVerified() {
        return verified;
    }

    public String getRejected() {
        return rejected;
    }

    public String getUnverified() {
        return unverified;
    }
}
