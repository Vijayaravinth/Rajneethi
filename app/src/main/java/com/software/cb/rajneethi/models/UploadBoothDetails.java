package com.software.cb.rajneethi.models;

public class UploadBoothDetails {

    private String boothNumber, boothName;
    private boolean isNewlyAdded, isTypeBooth;

    public UploadBoothDetails(String boothNumber, String boothName, boolean isNewlyAdded, boolean isTypeBooth) {
        this.boothNumber = boothNumber;
        this.boothName = boothName;
        this.isTypeBooth = isTypeBooth;
        this.isNewlyAdded = isNewlyAdded;
    }

    public String getBoothNumber() {
        return boothNumber;
    }

    public void setBoothNumber(String boothNumber) {
        this.boothNumber = boothNumber;
    }

    public String getBoothName() {
        return boothName;
    }

    public void setBoothName(String boothName) {
        this.boothName = boothName;
    }

    public boolean isNewlyAdded() {
        return isNewlyAdded;
    }

    public void setNewlyAdded(boolean newlyAdded) {
        isNewlyAdded = newlyAdded;
    }
}
