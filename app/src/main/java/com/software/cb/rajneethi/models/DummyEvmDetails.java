package com.software.cb.rajneethi.models;

public class DummyEvmDetails {

    private String name, image;


    private String serialNo, candidateName,  partyName;
    private int candidateImage,symbol;

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public DummyEvmDetails(String name, String image) {

        this.name = name;
        this.image = image;
    }


    public DummyEvmDetails(String serialNo, String candidateName, int candidateImage, String partyName, int symbol) {
        this.serialNo = serialNo;
        this.candidateName = candidateName;
        this.candidateImage = candidateImage;
        this.partyName = partyName;
        this.symbol = symbol;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public int getCandidateImage() {
        return candidateImage;
    }

    public void setCandidateImage(int candidateImage) {
        this.candidateImage = candidateImage;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }
}
