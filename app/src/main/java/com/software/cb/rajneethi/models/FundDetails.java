package com.software.cb.rajneethi.models;

public class FundDetails {

    private String title,startDate,endDate, cost, expenditure, status, agency;
    private boolean isSelected;

    private String beneficiaryTitle,type,beneficiaryName,beneficiaryDate,voter ,id;

    public FundDetails( String id,String beneficiaryTitle, String type, String beneficiaryName, String beneficiaryDate, String voter,boolean isSelected) {
        this.isSelected = isSelected;
        this.beneficiaryTitle = beneficiaryTitle;
        this.type = type;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryDate = beneficiaryDate;
        this.voter = voter;
        this.id =id;
    }

    public FundDetails(String id ,String title, String startDate, String endDate, String cost, String expenditure, String status, String agency, boolean isSelected) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
        this.expenditure = expenditure;
        this.status = status;
        this.agency = agency;
        this.isSelected = isSelected;
        this.id = id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(String expenditure) {
        this.expenditure = expenditure;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getBeneficiaryTitle() {
        return beneficiaryTitle;
    }

    public void setBeneficiaryTitle(String beneficiaryTitle) {
        this.beneficiaryTitle = beneficiaryTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficiaryDate() {
        return beneficiaryDate;
    }

    public void setBeneficiaryDate(String beneficiaryDate) {
        this.beneficiaryDate = beneficiaryDate;
    }

    public String getVoter() {
        return voter;
    }

    public void setVoter(String voter) {
        this.voter = voter;
    }
}
