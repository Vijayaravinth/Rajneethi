package com.software.cb.rajneethi.models;

/**
 * Created by w7 on 11/28/2017.
 */

public class SurveyDetails {

    private String pwName, surveyId, latitude, longitude, audioFileName, surveyDate, userId, userType, projectId, boothNmae, surveyType, name, mobile;

    public String getSurveyId() {
        return surveyId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserType() {
        return userType;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getBoothNmae() {
        return boothNmae;
    }

    public String getSurveyType() {
        return surveyType;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAudioFileName() {
        return audioFileName;
    }

    public String getSurveyDate() {
        return surveyDate;
    }

    public SurveyDetails(String surveyId, String latitude, String longitude, String audioFileName, String surveyDate, String userId, String userType, String projectId, String boothName, String surveyType) {

        this.surveyId = surveyId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.audioFileName = audioFileName;
        this.surveyDate = surveyDate;
        this.userId = userId;
        this.projectId = projectId;
        this.userType = userType;
        this.boothNmae = boothName;
        this.surveyType = surveyType;
    }

    public SurveyDetails(String surveyId, String latitude, String longitude, String audioFileName, String surveyDate, String userId, String userType, String projectId, String boothName, String surveyType, String name, String mobile, String pwName) {

        this.surveyId = surveyId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.audioFileName = audioFileName;
        this.surveyDate = surveyDate;
        this.userId = userId;
        this.projectId = projectId;
        this.userType = userType;
        this.boothNmae = boothName;
        this.surveyType = surveyType;
        this.name = name;
        this.mobile = mobile;
        this.pwName = pwName;
    }

    public String getPwName() {
        return pwName;
    }

    public void setPwName(String pwName) {
        this.pwName = pwName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
