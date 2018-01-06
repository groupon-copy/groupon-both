package com.example.marcus.grouponadministrator.Config;

/**
 * Created by Marcus on 6/29/2016.
 */
public class DatabaseVariable
{
    //public static final String HOST_DOMAIN = "http://192.168.0.222";
    public static final String HOST_DOMAIN = "http://192.168.86.250";
    public static final String PATH = "/android/groupon_slim_api_version";
    public static final String ROOT_PATH = HOST_DOMAIN + PATH;
    public static final String ROOT_PATH_VERSION = ROOT_PATH + "/v1";

    //Global returns
    public static final String ERROR = "error";
    public static final String MESSAGE = "message";


    //Users Operation
    public static final String _POST_EMAIL = "email";
    public static final String _POST_PASSWORD = "password";
    public static final String _POST_USERNAME = "username";
    public static final String _POST_AVATAR_IMAGE = "avatar_image";

    public static final String REGISTRATION_REGISTER_USER_URL = ROOT_PATH_VERSION + "/user_insert.php";
    public static final String REGISTRATION_LOGIN_USER_URL = ROOT_PATH_VERSION + "/user_login.php";
    public static final String REGISTRATION_RESEND_VERIFICATION_EMAIL_URL = ROOT_PATH_VERSION + "/user_resend_verification_email.php";
    public static final String UPLOAD_AVATAR_IMAGE_URL = ROOT_PATH_VERSION + "/user_upload_avatar_image.php";

    //Users returns
    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String API_KEY = "api_key";
    public static final String VERIFICATION_CODE = "verification_code";
    public static final String AVATAR_IMG_REL_URL = "avatar_img_rel_url";

    //DEAL OPERATIONS//
    public static final String _POST_TAG_LIST_STRING = "tag_list";
    public static final String GET_DEALS_BY_TAG_LIST_URL = ROOT_PATH_VERSION + "/get_deals_by_tag_list.php";
    public static final String _POST_DEALS_RETURN_STRING = "deals";

    public static final String GET_DEAL_BY_ID = ROOT_PATH_VERSION + "/get_deal_by_id.php";
    public static final String DEAL = "deal";

    //VENDOR OPERATIONS//
    public static final String GET_VENDOR_BY_ID_URL = ROOT_PATH_VERSION + "/get_vendor_by_id.php";
    public static final String VENDOR_RETURN_STRING = "vendor";

    //FIREBASE OPERATIONS//
    public static final String REGISTRATION_TOKEN = "registration_token";
    public static final String INSERT_FIREBASE_REGISTRATION_TOKENS_URL = ROOT_PATH_VERSION + "/insert_firebase_registration_token.php";

    //ROOT DIRECTORIES FOR IMAGES//
    //user avatar images
    public static final String USER_AVATAR_IMAGES_URL = ROOT_PATH + "/data/users/images/";
    //deal images
    public static final String DEAL_IMAGES_URL = ROOT_PATH + "/data/deals/images/";
    //vendor images
    public static final String VENDOR_IMAGES_URL = ROOT_PATH + "/data/vendors/images/";


    //DATABASE Column/Variable Names//
    //Deal Columns
    public static final String DEAL_ID = "id";
    public static final String DEAL_VENDOR_ID = "vendor_id";
    public static final String DEAL_BOLD_TEXT = "bold_text";
    public static final String DEAL_FINE_PRINT_TEXT = "fine_print_text";
    public static final String DEAL_HIGHLIGHT_TEXT = "highlight_text";
    public static final String DEAL_ORIGINAL_PRICE = "original_price";
    public static final String DEAL_CURRENT_PRICE = "current_price";
    public static final String DEAL_NUM_BOUGHT = "num_bought";
    public static final String DEAL_NUM_THUMBS_UP = "num_thumbs_up";
    public static final String DEAL_NUM_THUMBS_DOWN = "num_thumbs_down";
    public static final String DEAL_VALID_FROM = "valid_from";
    public static final String DEAL_VALID_UNTIL = "valid_until";
    public static final String DEAL_IS_LIMITED_AVAILABILITY = "is_limited_availability";
    public static final String DEAL_IS_LIMITED_TIME_OFFER = "is_limited_time_offer";
    public static final String DEAL_IMG_REL_URL = "img_rel_url";

    //Deal Tag Columns
    public static final String DEAL_TAG_DEAL_ID = "deal_id";
    public static final String DEAL_TAG_TAG = "tag";

    //Vendor Columns
    public static final String VENDOR_ID = "id";
    public static final String VENDOR_NAME = "vendor_name";
    public static final String VENDOR_ACCOUNT_NUM = "account_num";
    public static final String VENDOR_ADDR_1 = "addr_1";
    public static final String VENDOR_ADDR_2 = "addr_2";
    public static final String VENDOR_CITY = "city";
    public static final String VENDOR_ZIP = "zip";
    public static final String VENDOR_STATE = "state";
    public static final String VENDOR_COUNTRY_CODE = "country_code";
    public static final String VENDOR_PRICE_RANGE = "price_range";
    public static final String VENDOR_VENDOR_WEBSITE = "vendor_website";
    public static final String VENDOR_NUM_THUMBS_UP = "num_thumbs_up";
    public static final String VENDOR_NUM_THUMBS_DOWN = "num_thumbs_down";
    public static final String VENDOR_IMAGE_REL_URL = "image_rel_url";
    public static final String VENDOR_DESCRIPTION = "description";
    public static final String VENDOR_HOURS = "hours";
    public static final String VENDOR_PHONE_1 = "phone_1";
    public static final String VENDOR_EMAIL = "email";

    //Vendor Tag Columns
    public static final String VENDOR_TAG_VENDOR_ID = "vendor_id";
    public static final String VENDOR_TAG_TAG = "tag";
}
