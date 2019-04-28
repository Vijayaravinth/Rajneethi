package com.software.cb.rajneethi.models;

public class GrievanceDetails {
    private String id,title,name,voter,date,type,subType,remark,proiroty,slaTime,status,rewards;
    private boolean iisSelected ;

    public GrievanceDetails(String id,String title, String name, String voter, String date, String type, String subType, String remark, String proiroty, String slaTime, String status, String rewards, boolean isSelected) {
        this.title = title;
        this.name = name;
        this.voter = voter;
        this.date = date;
        this.type = type;
        this.subType = subType;
        this.remark = remark;
        this.proiroty = proiroty;
        this.slaTime = slaTime;
        this.status = status;
        this.rewards = rewards;
        this.iisSelected = isSelected;
        this.id =id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIisSelected() {
        return iisSelected;
    }

    public void setIisSelected(boolean iisSelected) {
        this.iisSelected = iisSelected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVoter() {
        return voter;
    }

    public void setVoter(String voter) {
        this.voter = voter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProiroty() {
        return proiroty;
    }

    public void setProiroty(String proiroty) {
        this.proiroty = proiroty;
    }

    public String getSlaTime() {
        return slaTime;
    }

    public void setSlaTime(String slaTime) {
        this.slaTime = slaTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }
}
