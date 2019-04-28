package com.software.cb.rajneethi.models;

import java.io.Serializable;

/**
 * Created by monika on 4/9/2017.
 */

public class VoterAttribute implements Serializable {

    public VoterAttribute(String voterCardNumber, String attributeName, String attributeValue, String date, boolean synced) {
        this.voterCardNumber = voterCardNumber;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.date = date;
        this.synced = synced;
    }

    private String voterCardNumber;
    private String attributeName;
    private String attributeValue;
    private String date;

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String surveyId;

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getAttributeValue_hardcode_qus() {
        return attributeValue_hardcode_qus;
    }

    public void setAttributeValue_hardcode_qus(String attributeValue_hardcode_qus) {
        this.attributeValue_hardcode_qus = attributeValue_hardcode_qus;
    }

    private String attributeValue_hardcode_qus;

    public VoterAttribute(String voterCardNumber, String attributeName, String attributeValue, String date, boolean synced, String surveyid) {
        this.voterCardNumber = voterCardNumber;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.date = date;
        this.synced = synced;
        this.surveyId = surveyid;
    }

  /*  public VoterAttribute(String voterCardNumber, String attributeName, String attributeValue, String date, boolean synced, String surveyid, String status) {
        this.voterCardNumber = voterCardNumber;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.date = date;
        this.synced = synced;
        this.surveyId = surveyid;
        this.status = status;
    }
*/

    public String getVoterCardNumber() {
        return voterCardNumber;
    }

    public void setVoterCardNumber(String voterCardNumber) {
        this.voterCardNumber = voterCardNumber;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public String getSurveyValues(String attributeName) {
        return this.attributeName;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    private String id;
    private boolean synced;
}
