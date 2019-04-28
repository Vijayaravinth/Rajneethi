package com.software.cb.rajneethi.supervisormigratoon.pojo;

public class PartyWorker {

    private String phoneNum;
    private String name;

    public PartyWorker() {
    }

    public PartyWorker(String phoneNum, String name) {
        this.phoneNum = phoneNum;
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
