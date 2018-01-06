package com.example.marcus.grouponadministrator.User;

/**
 * Created by Marcus on 8/16/2016.
 */
public class IsValid
{
    public static boolean isUsernameValid(String username)
    {
        return username.length() > 1;
    }

    public static boolean isEmailValid(String email)
    {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password)
    {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
