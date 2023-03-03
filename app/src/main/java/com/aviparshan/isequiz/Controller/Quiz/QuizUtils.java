package com.aviparshan.isequiz.Controller.Quiz;


/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz
 */
public class QuizUtils {

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

}