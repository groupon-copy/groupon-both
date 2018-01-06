package com.example.marcus.grouponadministrator.Config;

// contains URL source for php services as well as root directory for images
public class AdminDatabaseVariable
{
    ///////////////////
    //Deal Operations//
    //////////////////

    //get tags by deal id
    //public static final String GET_DEAL_TAGS_BY_ID_URL = DatabaseVariable.ROOT_PATH_VERSION + "/php/get_deal_tags_by_id.php";
    //public static final String _POST_DEAL_TAGS = "deal_tags";

    //delete tags

    //public static final String DELETE_TAGS_WITH_DEAL_ID_URL = DatabaseVariable.ROOT_PATH_VERSION + "/php/delete_tags_with_deal_id.php";

    //get deal by id
    //public static final String GET_DEAL_BY_ID_URL = DatabaseVariable.ROOT_PATH_VERSION + "/php/get_deal_by_id.php";

    //get deals by tag list, tag list in csv form
    //public static final String _POST_TAG_LIST_STRING = "tag_list";
    //public static final String GET_DEALS_BY_TAG_LIST_URL = DatabaseVariable.ROOT_PATH_VERSION + "/php/get_deals_by_tag_list.php";
    //public static final String _POST_DEALS_RETURN_STRING = "deals";

    //insert a deal
    public static final String INSERT_DEAL_AND_TAGS_URL = DatabaseVariable.ROOT_PATH_VERSION + "/insert_deal_and_tags.php";
    //public static final String INSERT_DEAL_URL = DatabaseVariable.ROOT_PATH_VERSION + "/php/insert_deal.php";

    //update deal
    public static final String UPDATE_DEAL_AND_REFRESH_TAGS_BY_ID_URL = DatabaseVariable.ROOT_PATH_VERSION + "/update_deal_and_tags_by_id.php";
    //public static final String UPDATE_DEAL_URL = DatabaseVariable.ROOT_PATH_VERSION + "/php/update_deal_by_id.php";

    //delete deal
    public static final String DELETE_DEAL_URL = DatabaseVariable.ROOT_PATH_VERSION + "/delete_deal_by_id.php";

    //insert tags for specific deal id
    //public static final String _POST_DEAL_ID = "deal_id";
    public static final String _POST_TAG_LIST = "tag_list";
    //public static final String INSERT_DEAL_TAGS_BY_ID_URL = DatabaseVariable.FULL_PATH + "/php/insert_deal_tags_by_id.php";

    ///////////////////////
    //Firebase Operations//
    ///////////////////////
    public static final String FIREBASE_SEND_GROUPON_NOTIFICATION_URL = DatabaseVariable.ROOT_PATH_VERSION + "/firebase_send_groupon_notification.php";
}
