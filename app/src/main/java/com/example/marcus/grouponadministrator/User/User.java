package com.example.marcus.grouponadministrator.User;

import android.content.Context;

import com.example.marcus.grouponadministrator.Config.DatabaseVariable;
import com.example.marcus.grouponadministrator.One.MainFeedActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by Marcus on 8/16/2016.
 */
public class User
{
    private static boolean logged_in = false;
    private static int user_id = -1;
    private static String user_password = null;
    private static String user_email = null;
    private static String user_username = null;
    private static String user_verification_code = null;
    private static String user_api_key = null;
    private static String user_avatar_img_full_url = null;

    public static boolean login(Context context, int id, String password, String email, String username, String verification_code, String avatar_img_rel_url, String api_key) {
        if (!logged_in) {
            user_id = id;
            user_email = email;
            user_password = password;
            user_username = username;
            user_verification_code = verification_code;
            user_api_key = api_key;

            if (avatar_img_rel_url != null) {
                //get full url path of avatar image
                user_avatar_img_full_url = DatabaseVariable.USER_AVATAR_IMAGES_URL + avatar_img_rel_url;

                // invalidate to load again.
                // also make sure all loads of this url has been loaded with
                // picasso.networkPolicy(NetworkPolicy.NO_CACHE) // no network cache, so new image will be uploaded
                Picasso.with(context).invalidate(User.getUser_avatar_img_full_url());
            } else {
                user_avatar_img_full_url = null;
            }

            logged_in = true;
            notifyOthers();
            return true;
        } else {
            return false;
        }
    }

    public static void logout() {
        clearLoginState();
    }

    private static void clearLoginState() {
        user_id = -1;
        user_email = null;
        user_password = null;
        user_username = null;
        user_verification_code = null;
        user_avatar_img_full_url = null;
        user_api_key = null;
        logged_in = false;
        notifyOthers();
    }

    public static void avatarImageChanged(Context context) {
        //invalidate cached image, in order to reload same image url
        Picasso.with(context).invalidate(User.getUser_avatar_img_full_url());
        notifyOthers();
    }

    private static void notifyOthers()
    {
        MainFeedActivity.userStateChanged = true;
    }


    public static boolean isLoggedIn()
    {
        return logged_in;
    }

    public static int getUser_id() { return user_id; }

    public static String getUser_api_key() { return user_api_key; }

    public static String getUser_password()
    {
        return user_password;
    }

    public static String getUser_email()
    {
        return user_email;
    }

    public static String getUser_username()
    {
        return user_username;
    }

    public static String getUser_verification_code() {return user_verification_code; }

    public static  String getUser_avatar_img_full_url() { return user_avatar_img_full_url; }
}
