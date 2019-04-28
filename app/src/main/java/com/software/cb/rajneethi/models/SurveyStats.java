package com.software.cb.rajneethi.models;

import java.io.Serializable;

/**
 * Created by w7 on 12/7/2017.
 */

public class SurveyStats implements Serializable {

    private String count;
    private String name;
    private boolean isExpanded;


    private String audioFileName, surveyId, surveyFlag, parentId, repondantName, mobile, userId,boothName;
    private int isHeader;

    public SurveyStats(String audioFileName, String surveyId, String surveyFlag, String parentId, String repondantName, String mobile, String userId, int isHeader,String boothName) {
        this.audioFileName = audioFileName;
        this.surveyId = surveyId;
        this.surveyFlag = surveyFlag;
        this.parentId = parentId;
        this.repondantName = repondantName;
        this.mobile = mobile;
        this.userId = userId;
        this.isHeader = isHeader;
        this.boothName = boothName;
    }

    public String getBoothName() {
        return boothName;
    }

    public void setBoothName(String boothName) {
        this.boothName = boothName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setIsHeader(int isHeader) {
        this.isHeader = isHeader;
    }

    public SurveyStats(String count, String name, boolean isExpanded) {
        this.count = count;
        this.name = name;
        this.isExpanded = isExpanded;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public SurveyStats(String name, String count) {
        this.name = name;
        this.count = count;
    }

    public SurveyStats(String userId, int isHeader) {
        this.userId = userId;
        this.isHeader = isHeader;
    }

    public String getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public String getAudioFileName() {
        return audioFileName;
    }

    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getSurveyFlag() {
        return surveyFlag;
    }

    public void setSurveyFlag(String surveyFlag) {
        this.surveyFlag = surveyFlag;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getRepondantName() {
        return repondantName;
    }

    public void setRepondantName(String repondantName) {
        this.repondantName = repondantName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserId() {
        return userId;
    }

    public int getIsHeader() {
        return isHeader;
    }
}
