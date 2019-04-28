package com.software.cb.rajneethi.models;

/**
 * Created by monika on 3/25/2017.
 */

public class VoterDetails {

    private String id;
    private String constituencyId;
    private String hierarchyId;
    private String hierarchyHash;
    private String voterCardNumber;
    private String nameEnglish;
    private String nameRegional;

    public boolean isVoted() {
        return isVoted;
    }

    private String gender;
    private String age;
    private String relatedEnglish;
    private String relatedRegional;
    private String relationship;
    private String addressEnglish;
    private String addressRegional;
    private String houseNo;
    private String serialNo;
    private String wardNo;
    private String wardName;
    private String boothNo;
    private String boothName;
    private String AC1;
    private int SOS;
    private String mobile;
    private String newViter;
    private String VNV;
    private boolean isVoted;

    public String getWardNo() {
        return wardNo;
    }

    public void setWardNo(String wardNo) {
        this.wardNo = wardNo;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getBoothNo() {
        return boothNo;
    }

    public void setBoothNo(String boothNo) {
        this.boothNo = boothNo;
    }

    public String getBoothName() {
        return boothName;
    }

    public void setBoothName(String boothName) {
        this.boothName = boothName;
    }

    public String getAC1() {
        return AC1;
    }

    public void setAC1(String AC1) {
        this.AC1 = AC1;
    }

    public String getAC2() {
        return AC2;
    }

    public void setAC2(String AC2) {
        this.AC2 = AC2;
    }

    public String getIs_updated() {
        return is_updated;
    }

    private String AC2;
    private String is_updated;

    public String getId() {
        return id;
    }

    public void setSOS(int SOS) {
        this.SOS = SOS;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNewViter() {
        return newViter;
    }

    public void setNewViter(String newViter) {
        this.newViter = newViter;
    }

    public String getVNV() {
        return VNV;
    }

    public void setVNV(String VNV) {
        this.VNV = VNV;
    }

    public void setVoted(boolean voted) {
        isVoted = voted;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConstituencyId() {
        return constituencyId;
    }

    public void setConstituencyId(String constituencyId) {
        this.constituencyId = constituencyId;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getHierarchyHash() {
        return hierarchyHash;
    }

    public void setHierarchyHash(String hierarchyHash) {
        this.hierarchyHash = hierarchyHash;
    }

    public String getVoterCardNumber() {
        return voterCardNumber;
    }

    public void setVoterCardNumber(String voterCardNumber) {
        this.voterCardNumber = voterCardNumber;
    }

    public String getNameEnglish() {
        return nameEnglish;
    }

    public void setNameEnglish(String nameEnglish) {
        this.nameEnglish = nameEnglish;
    }

    public String getNameRegional() {
        return nameRegional;
    }

    public void setNameRegional(String nameRegional) {
        this.nameRegional = nameRegional;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getRelatedEnglish() {
        return relatedEnglish;
    }

    public void setRelatedEnglish(String relatedEnglish) {
        this.relatedEnglish = relatedEnglish;
    }

    public String getRelatedRegional() {
        return relatedRegional;
    }

    public void setRelatedRegional(String relatedRegional) {
        this.relatedRegional = relatedRegional;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getAddressEnglish() {
        return addressEnglish;
    }

    public void setAddressEnglish(String addressEnglish) {
        this.addressEnglish = addressEnglish;
    }

    public String getAddressRegional() {
        return addressRegional;
    }

    public void setAddressRegional(String addressRegional) {
        this.addressRegional = addressRegional;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String is_updated() {
        return is_updated;
    }

    public void setIs_updated(String is_updated) {
        this.is_updated = is_updated;
    }


    public VoterDetails(String id, String nameEnglish, String age, String voterCardNumber, String serialNo, String addressEnglish, String relatedEnglish, String gender) {
        this.id = id;
        this.nameEnglish = nameEnglish;
        this.age = age;
        this.voterCardNumber = voterCardNumber;
        this.serialNo = serialNo;
        this.addressEnglish = addressEnglish;
        this.relatedEnglish = relatedEnglish;
        this.gender = gender;

    }

    public VoterDetails(String isupdated,String name,String voterCardNumber,String serialNo,String relationName,String gender,String boothName,String address,String mobile,String wardName,String houseNo){
        this.houseNo = houseNo;
        this.nameEnglish = name;
        this.voterCardNumber = voterCardNumber;
        this.serialNo = serialNo;
        this.relatedEnglish = relationName;
        this.gender = gender;
        this.boothName = boothName;
        this.addressEnglish = address;
        this.mobile = mobile;
        this.wardName = wardName;
        this.is_updated=  isupdated;
    }

    public VoterDetails(String isupdated,String name,String voterCardNumber,String serialNo,String relationName,String gender,String boothName,String address,String mobile,String wardName,String houseNo, boolean isVoted, String VNV){
        this.houseNo = houseNo;
        this.nameEnglish = name;
        this.voterCardNumber = voterCardNumber;
        this.serialNo = serialNo;
        this.relatedEnglish = relationName;
        this.gender = gender;
        this.boothName = boothName;
        this.addressEnglish = address;
        this.mobile = mobile;
        this.wardName = wardName;
        this.VNV = VNV;
        this.is_updated=  isupdated;
        this.isVoted = isVoted;
    }

    public int getSOS() {
        return SOS;
    }

    private boolean isFamilyHead = false, isFamilyMember = false;

    public VoterDetails(String constituencyId, String hierarchyId, String hierarchyHash, String voterCardNumber, String nameEnglish, String nameRegional, String gender, String age, String relatedEnglish, String relatedRegional, String relationship, String addressEnglish, String addressRegional, String houseNo, String serialNo, String boothNo, String boothName, String wardNo, String wardName, String AC1, String AC2, String is_updated, int sos, String mobile, String newVoter, String VNV) {
        this.constituencyId = constituencyId;
        this.hierarchyId = hierarchyId;
        this.hierarchyHash = hierarchyHash;
        this.voterCardNumber = voterCardNumber;
        this.nameEnglish = nameEnglish;
        this.nameRegional = nameRegional;
        this.gender = gender;
        this.age = age;
        this.relatedEnglish = relatedEnglish;
        this.relatedRegional = relatedRegional;
        this.relationship = relationship;
        this.addressEnglish = addressEnglish;
        this.addressRegional = addressRegional;
        this.houseNo = houseNo;
        this.serialNo = serialNo;
        this.is_updated = is_updated;
        this.boothName = boothName;
        this.boothNo = boothNo;
        this.wardName = wardName;
        this.wardNo = wardNo;
        this.AC1 = AC1;
        this.AC2 = AC2;
        this.SOS = sos;
        this.mobile = mobile;
        this.newViter = newVoter;
        this.VNV = VNV;
        this.isFamilyHead = false;

        //tesr
    }

    public boolean isFamilyHead() {
        return isFamilyHead;
    }

    public void setFamilyHead(boolean familyHead) {
        isFamilyHead = familyHead;
    }

    public VoterDetails(String constituencyId, String hierarchyId, String hierarchyHash, String voterCardNumber, String nameEnglish, String nameRegional, String gender, String age, String relatedEnglish, String relatedRegional, String relationship, String addressEnglish, String addressRegional, String houseNo, String serialNo, String boothNo, String boothName, String wardNo, String wardName, String AC1, String AC2, String is_updated, int sos, String mobile , String newVoter, String VNV, boolean isVoted) {
        this.constituencyId = constituencyId;
        this.hierarchyId = hierarchyId;
        this.hierarchyHash = hierarchyHash;
        this.voterCardNumber = voterCardNumber;
        this.nameEnglish = nameEnglish;
        this.nameRegional = nameRegional;
        this.gender = gender;
        this.age = age;
        this.relatedEnglish = relatedEnglish;
        this.relatedRegional = relatedRegional;
        this.relationship = relationship;
        this.addressEnglish = addressEnglish;
        this.addressRegional = addressRegional;
        this.houseNo = houseNo;
        this.serialNo = serialNo;
        this.is_updated = is_updated;
        this.boothName = boothName;
        this.boothNo = boothNo;
        this.wardName = wardName;
        this.wardNo = wardNo;
        this.AC1 = AC1;
        this.AC2 = AC2;
        this.SOS = sos;
        this.mobile = mobile;
        this.newViter = newVoter;
        this.VNV = VNV;
        this.isVoted = isVoted;

    }


}
