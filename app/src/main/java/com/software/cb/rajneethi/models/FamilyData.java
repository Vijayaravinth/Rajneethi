package com.software.cb.rajneethi.models;

public class FamilyData {

   private String casteName;
   private long count;
    private boolean isSelected;

    private String percentage;

    private float calPer;

    public FamilyData(String casteName, long count, boolean isSelected) {
        this.casteName = casteName;
        this.count = count;
        this.isSelected = isSelected;
        this.percentage = "";
    }

    public FamilyData(String casteName, long count, boolean isSelected, String percentage) {
        this.casteName = casteName;
        this.count = count;
        this.isSelected = isSelected;
        this.percentage = percentage;
    }

    public FamilyData(String casteName, long count, boolean isSelected, String percentage, float calPer) {
        this.casteName = casteName;
        this.count = count;
        this.isSelected = isSelected;
        this.percentage = percentage;
        this.calPer = calPer;
    }

    public float getCalPer() {
        return calPer;
    }

    public String getPercentage() {
        return percentage;
    }

    public String getCasteName() {
        return casteName;
    }

    public long getCount() {
        return count;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
