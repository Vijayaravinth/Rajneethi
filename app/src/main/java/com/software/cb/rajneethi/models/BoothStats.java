package com.software.cb.rajneethi.models;

/**
 * Created by w7 on 12/7/2017.
 */

public class BoothStats {

    public String getPid() {
        return pid;
    }

    public String getBoothName() {
        return boothName;
    }

    public String getSurveyType() {
        return surveyType;
    }

    public String getUserType() {
        return userType;
    }

    public BoothStats(String pid, String boothName, String surveyType, String userType) {

        this.pid = pid;
        this.boothName = boothName;
        this.surveyType = surveyType;
        this.userType = userType;
    }

    private String pid, boothName, surveyType, userType;
}
