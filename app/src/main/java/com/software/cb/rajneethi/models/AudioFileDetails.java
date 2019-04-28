package com.software.cb.rajneethi.models;

import java.io.Serializable;

/**
 * Created by w7 on 11/12/2017.
 */

public class AudioFileDetails implements Serializable {

    private String fileName, surveyId, surveyFlag, parentId, username, userId,boothname;
    private boolean isDownloaded, isPlaying, isDownloading, isPaused;

    private int isHeader;

    boolean isShowBottomLayout = true;

    public String getFileName() {
        return fileName;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public boolean isDownloaded() {

        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public AudioFileDetails(String fileName, boolean isDownloaded, boolean isPlaying, boolean isDownloading, boolean isPaused, String surveyId, String surveyFlag, String parentId, boolean isShowBottomLayout, String userId, int isHeader,String boothname) {

        this.fileName = fileName;
        this.isDownloaded = isDownloaded;
        this.isPlaying = isPlaying;
        this.isDownloading = isDownloading;
        this.isPaused = isPaused;
        this.surveyId = surveyId;
        this.surveyFlag = surveyFlag;
        this.parentId = parentId;
        this.isShowBottomLayout = isShowBottomLayout;
        this.userId = userId;
        this.isHeader = isHeader;
        this.boothname = boothname;
    }

    public String getBoothname() {
        return boothname;
    }

    public void setBoothname(String boothname) {
        this.boothname = boothname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getIsHeader() {
        return isHeader;
    }

    public void setIsHeader(int isHeader) {
        this.isHeader = isHeader;
    }

    public AudioFileDetails(String name, int header) {
        this.isHeader = header;
        this.userId = name;
    }

    public AudioFileDetails(String surveyId, boolean isShowBottomLayout, String surveyFlag) {
        this.surveyId = surveyId;
        this.isShowBottomLayout = isShowBottomLayout;
        this.surveyFlag = surveyFlag;
    }

    public boolean isShowBottomLayout() {
        return isShowBottomLayout;
    }

    public void setShowBottomLayout(boolean showBottomLayout) {
        isShowBottomLayout = showBottomLayout;
    }
}
