package com.example.nirmal.ilistensinch;

/**
 * Created by nirmal on 30/4/17.
 */

public class SinchUserData {
    private static String name;
    SinchUserData(){

    }
    SinchUserData(String mUserName){
        name = mUserName;
    }
    public static String getCanonicalClassName(){
        return "UserData";
    }
    public static String UserBaseName(){
        return "name";
    }
    public static String ReturnUserName(){
        return name;
    }
    public static void SetUserName(String a){
        name = a;
    }
}
