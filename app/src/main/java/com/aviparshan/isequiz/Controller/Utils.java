package com.aviparshan.isequiz.Controller;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.aviparshan.isequiz.BuildConfig;
import com.aviparshan.isequiz.R;

import java.util.List;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz
 */
public class Utils {

    static final String TAG = Utils.class.getSimpleName();
    public static final int TRUE_FALSE = 0;
    public static final int MULTIPLE_CHOICE = 1;
    public static final int OPEN_ANSWER = 2;
    public static final int UNKNOWN = 3;

    public static final String QUIZZES_URL = "quizzes.json";

    public static final char OPEN = '~', ANSWER = '@', QUESTION = '$', SOLUTION = '*', WEEK_NUM = '#';
    public static final String OPEN_S = "~", ANSWER_S = "@", QUESTION_S = "$", SOLUTION_S = "*", WEEK_NUM_S = "#";

    //    array of answer types
    public static final int[] ANSWER_TYPES = {TRUE_FALSE, MULTIPLE_CHOICE, OPEN_ANSWER};
//    chars as strings

    public static String charToString(char c) {
        return String.valueOf(c);
    }

    public static String cToS(char c) {
        return charToString(c);
    }

    /**
     * get the question type given the possible answers list size
     */
    public static int getqType(int size) {
        if (size == 1) {
            return Utils.OPEN_ANSWER;
        } else if (size == 2) {
            return Utils.TRUE_FALSE;
        }
        else if(size > 2){
            return Utils.MULTIPLE_CHOICE;
        }
        else{
//            error
            throw new IllegalArgumentException("getqType: size is less than 1");
//            return Utils.UNKNOWN;
        }
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static boolean copyToClipboard(Context context, String text) {
        try {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(context.getString(R.string.app_name), text);
            clipboard.setPrimaryClip(clip);
        } catch (Exception e) {
            errorMessage(context, "copyToClipboardBug: " + e.getMessage(), R.string.failed_copy, TAG);
            return false;
        }
        return true;
    }

    public static void copyToClipboardWithMessage(Context context, String text, String success) {
        if (copyToClipboard(context, text)) {
            Toast.makeText(context, success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.failed_copy, Toast.LENGTH_SHORT).show();
        }
    }

    public static void errorMessage(Context context, String message, int prod_message, String TAG) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "ISE_ERROR: " + message);
        } else {
            Toast.makeText(context, prod_message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void errorMessage(Context context, String message, String prod_message, String TAG) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "ISE_ERROR: " + message);
        } else {
            Toast.makeText(context, prod_message, Toast.LENGTH_SHORT).show();
        }
    }
}