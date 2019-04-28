package com.software.cb.rajneethi.models;

/**
 * Created by DELL on 13-04-2018.
 */

public class voterArray {

   private  String distric_code,ac_no,part_no,state_code,epic_no,mobile_no,party_affiliation;

    public voterArray(String distric_code, String ac_no, String part_no, String state_code, String epic_no, String mobile_no, String party_affiliation) {
        this.distric_code = distric_code;
        this.ac_no = ac_no;
        this.part_no = part_no;
        this.state_code = state_code;
        this.epic_no = epic_no;
        this.mobile_no = mobile_no;
        this.party_affiliation = party_affiliation;
    }

    public String getDistric_code() {
        return distric_code;
    }

    public void setDistric_code(String distric_code) {
        this.distric_code = distric_code;
    }

    public String getAc_no() {
        return ac_no;
    }

    public void setAc_no(String ac_no) {
        this.ac_no = ac_no;
    }

    public String getPart_no() {
        return part_no;
    }

    public void setPart_no(String part_no) {
        this.part_no = part_no;
    }

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public String getEpic_no() {
        return epic_no;
    }

    public void setEpic_no(String epic_no) {
        this.epic_no = epic_no;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getParty_affiliation() {
        return party_affiliation;
    }

    public void setParty_affiliation(String party_affiliation) {
        this.party_affiliation = party_affiliation;
    }
}
