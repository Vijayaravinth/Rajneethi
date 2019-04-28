package com.software.cb.rajneethi.api;

import com.software.cb.rajneethi.BuildConfig;

/**
 * Created by Monica on 09-03-2017.
 */

public class API {


    private static final String BASE_API = BuildConfig.BASE_URL;

    private static final String NEWBASEURL = BuildConfig.BASE_URL1;


    private static final String WEB_BASE_URL = BuildConfig.BASE_URL2;
    public static final String GET_PROJECT = NEWBASEURL + "project/";


    public static final String LOGIN = BASE_API + "login";
    public static final String ADDUSER = BASE_API + "registration";
    public static final String BOOTH = NEWBASEURL + "shared?PRCID=GETBOOTH&userid=";
    public static final String CONSTITUENCYBOOTHS = NEWBASEURL + "shared?PRCID=GETCONSTITUENCYBOOTHS&projectId=";

    public static final String CONSTITUENCY_STATS = NEWBASEURL + "shared?PRCID=CONSTITUTENCYSTATS&constituencyId=";

    public static final String BOOTH_INFO = NEWBASEURL + "shared?PRCID=BOOTHMATRIX&";

    public static final String DELETE_USER = NEWBASEURL + "shared?PRCID=DELETEUSER&id=";

    public static final String VALIDATE_SURVEY = NEWBASEURL + "shared?PRCID=VALIDATESURVEY";
    public static final String GET_PAYLOADDATA = NEWBASEURL + "shared?PRCID=GETPAYLOADDATA";

    public static final String ALLOCATEPROJECT = NEWBASEURL + "shared?PRCID=ALLOCATEPROJECT&userId=";

    public static final String UPLOAD_SURVEY = NEWBASEURL + "shared?PRCID=SURVEYSTATICPARAMS";
    public static final String GET_AUDIO_FILES = NEWBASEURL + "shared?PRCID=GETAUDIOFILENAMES&projectID=";

    public static final String VOTREIDAPI = NEWBASEURL + "shared?PRCID=CREATEVOTER&name=";
    public static final String GETROLEURL = "http://ec2-52-66-83-57.ap-south-1.compute.amazonaws.com/api/v1/shared?PRCID=";

    public static final String GET_SURVEY_MAP = NEWBASEURL + "shared?PRCID=GETSURVEYMAP&pid=";
    public static final String GET_CONSTITUENCY = NEWBASEURL + "constituency";
    public static final String ALLOCATE_BOOTH = NEWBASEURL + "shared?PRCID=INSERTBOOTH&userId=";
    public static final String DEALLOCATE_BOOTH = NEWBASEURL + "shared?PRCID=DELETEBOOTH&userId=";


    public static final String GET_QUESTIONS = BASE_API + "constituency/offlinedata/";
    public static final String GET_SURVEY_DATA = BASE_API + "shared?PRCID=GETSURVEYDETAILS1&cid=";
    public static final String UPLOAD_DATA = BASE_API + "voter/bulkupload/";
    public static final String GET_CASTE_EQUATION = NEWBASEURL + "constituencymap/";
    public static final String GET_ELECTION_HISTORY = NEWBASEURL + "shared?PRCID=SURVEYDETAILS&Question=";
    public static final String GET_QUESTION_LIST = "http://ec2-52-66-83-57.ap-south-1.compute.amazonaws.com/api/v1/shared?PRCID=GETQUESTION&constituencyId=" + "$scope.consId";
    public static final String GET_DATA = "http://ec2-52-66-83-57.ap-south-1.compute.amazonaws.com/api/v1/shared?PRCID=SURVEYDETAILS&Question=" + "name " + "&ProID=" + "projectId";

    public static final String GET_BOOTH_WISE_STATISTICS = NEWBASEURL + "shared?PRCID=GET_BOOTH_WISE_STATISTICS&projectId=";
    public static final String GET_BOOTH_WISE_DATA = NEWBASEURL + "shared?PRCID=GET_BOOTH_WISE_DATA&projectId=";

    public static final String UPDATE_VOTE = NEWBASEURL + "updatevote";

    public static final String GET_CONSTITUENCY_SUMMARY = WEB_BASE_URL + "survey-reports?";
    public static final String GET_GOTV_STATISTICS = WEB_BASE_URL + "gotv-result?";
    public static final String PAST_ELECTION_HISTORY = WEB_BASE_URL + "get-past-election-history?";
    public static final String GET_EVENTS = WEB_BASE_URL + "get-events-slots?";
    public static final String DELETE_EVENTS = WEB_BASE_URL + "delete-events-details?";
    public static final String GET_BENEFICIARY = WEB_BASE_URL + "beneficiary-data-list?";
    public static final String GET_FUND_DETAILS = WEB_BASE_URL + "fund-data-list?";
    public static final String DELETE_BENEFICIARY_DETAILS = WEB_BASE_URL + "delete-beneficiary-details?";
    public static final String DELETE_FUND_DEAILS = WEB_BASE_URL + "delete-fund-details?";
    public static final String ADD_NEW_BENEFICIARY = WEB_BASE_URL + "create-new-beneficiary-details";
    public static final String ADD_FUND_DETAILS = WEB_BASE_URL + "create-new-fund-details";
    public static final String GET_GRIEVANCE_DETAILS = WEB_BASE_URL + "grievance-data-list?";
    public static final String DELETE_GRIEVANCE = WEB_BASE_URL + "delete-grievance-details?";
    public static final String UPDATE_FUND_DETAILS = WEB_BASE_URL + "edit-fund-details";
    public static final String UPDATE_BENEFICIARY_DETAILS = WEB_BASE_URL + "edit-beneficiary-details";
    public static final String ADD_NEW_GRIEVANCE_DETAILS = WEB_BASE_URL + "create-new-grievance-details";
    public static final String UPDATE_GRIEVANCE_DETAILS = WEB_BASE_URL + "edit-grievance-details";
    public static final String ADD_NEW_EVENTS = WEB_BASE_URL + "create-new-calendar-event";
    public static final String UPDATE_EVENTS = WEB_BASE_URL + "edit-calendar-event";

    public static final String GET_USER_BOOTHS = NEWBASEURL+"shared?PRCID=GET_USERS_BOOTHS&id=";
    public static final String GET_USER_STATISTICS = NEWBASEURL+"shared?PRCID=GET_STATISTICS_USER&name=";

    public static final String SMS_API = BuildConfig.SMSAPI+"send_sms.php";

    public static final String LOGIN_STATUS = BuildConfig.SMSAPI +"login_status.php";

}
