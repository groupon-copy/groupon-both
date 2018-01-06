package com.example.marcus.grouponadministrator.Config;

/**
 * Created by Marcus on 8/27/2016.
 */
public class StatusCode {
    public static final String STATUS_CODE = "status_code";

    //STATUS CODES//
    public static final int ENTER_MISSING_VALUES = 15;

    public static final int USER_CREATED_SUCCESSFULLY = 0;
    public static final int USER_CREATE_FAILED = 1;
    public static final int USER_EMAIL_ALREADY_EXISTS = 2;
    public static final int USER_USERNAME_ALREADY_EXISTS = 3;
    public static final int USER_CREATED_EMAIL_SENT = 13;
    public static final int USER_CREATED_EMAIL_NOT_SENT = 14;
    public static final int USER_CREATED_AND_UPDATE_FAILED = 27;

    public static final int USER_IS_NOW_VERIFIED = 5;
    public static final int VERIFICATION_CODE_DOES_NOT_EXIST = 7;
    public static final int USER_VERIFY_FAILED = 8;
    public static final int USER_NEEDS_TO_BE_VERIFIED = 16;
    public static final int USER_ALREADY_VERIFIED = 9;

    public static final int LOGIN_SUCCESS = 12;
    public static final int PASSWORD_INCORRECT = 10;
    public static final int EMAIL_DOES_NOT_EXIST = 11;

    public static final int EMAIL_SENT = 1;
    public static final int EMAIL_NOT_SENT = 18;

    public static final int FAILED_AUTHENTICATION = 19;
    public static final int ERROR_UPLOADING_IMAGE = 20;

    public static final int IMG_UPLOADED_UPDATE_SUCCESS = 21;
    public static final int IMG_UPLOADED_UPDATE_FAILED = 22;

    public static final int ERROR_EXECUTING_QUERY = 23;
    public static final int SUCCESS_EXECUTING_QUERY = 24;

    public static final int USER_EMAIL_ALREADY_EXISTS_AND_ACTIVATED = 25;
    public static final int USER_EMAIL_ALREADY_EXISTS_AND_NOT_ACTIVATED = 26;

    public static final int BAD_DATE_TIME_FORMAT = 27;
}
