package com.aviparshan.isequiz.Controller;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz
 */
public class Utils {

    public static final int TRUE_FALSE = 0;
    public static final int MULTIPLE_CHOICE = 1;
    public static final int OPEN_ANSWER = 2;
    public static final int UNKNOWN = 3;

    public static final char OPEN='~',ANSWER='@',QUESTION='$',SOLUTION='*',WEEK_NUM='#';
    public static final String OPEN_S="~",ANSWER_S="@",QUESTION_S="$",SOLUTION_S="*",WEEK_NUM_S="#";

//    array of answer types
    public static final int[] ANSWER_TYPES = {TRUE_FALSE, MULTIPLE_CHOICE, OPEN_ANSWER};
//    chars as strings

    public static String charToString(char c){
        return String.valueOf(c);
    }

    public static String cToS(char c){
        return charToString(c);
    }

    /**
     * get the question type given the possible answers list size
     */
    public static int getqType(List<String> possibleAnsEdited) {
        int qType;
        if (possibleAnsEdited.size() == 1) {
            qType = Utils.OPEN_ANSWER;
        } else if (possibleAnsEdited.size() == 2) {
            qType = Utils.TRUE_FALSE;
        } else if (possibleAnsEdited.size() > 2) {
            qType = Utils.MULTIPLE_CHOICE;
        } else {
            qType = Utils.UNKNOWN;
        }
        return qType;
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}