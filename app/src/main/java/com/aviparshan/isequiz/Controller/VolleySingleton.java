package com.aviparshan.isequiz.Controller;


import static com.aviparshan.isequiz.Controller.Quiz.QuizUtils.cToS;
import static com.aviparshan.isequiz.Controller.Quiz.QuizUtils.getqType;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.aviparshan.isequiz.BuildConfig;
import com.aviparshan.isequiz.Controller.Quiz.QuizUtils;
import com.aviparshan.isequiz.Models.Quiz;
import com.aviparshan.isequiz.Models.QuizQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ISE Quiz
 * Created by Avi Parshan on 3/3/2023 on com.aviparshan.isequiz.Controller
 */
public class VolleySingleton {

    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private static final String TAG = VolleySingleton.class.getSimpleName();

    private static boolean isFinishedParsing = false;

    private VolleySingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
            DiskBasedCache cache = new DiskBasedCache(ctx.getCacheDir(), 16 * 1024 * 1024);
            requestQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));

        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(com.android.volley.Request<T> req) {
        getRequestQueue().add(req);
    }

    //    cancel all requests
    public void cancelAllRequests() {
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(com.android.volley.Request<?> request) {
                return true;
            }
        });
    }


    //    cancel request based on tag
    public void cancelRequest(String tag) {
        requestQueue.cancelAll(tag);
    }

    //    parser and fetcher
    public static List<QuizQuestion> parser(String response, Quiz quiz) {
        List<QuizQuestion> sQuestions = new ArrayList<>(); // create an empty list of questions
        QuizQuestion quizQ; // create a question object
        String trimmed, questionText, qAnswer = "";
        List<String> possibleAnswers;
        String[] arr = response.split("");
        String joined = String.join("", arr);
        List<String> blocks = Arrays.asList(joined.split("\\$")); // split on $ (question)

        int index; //string index
        int qType;
        int qNum = 0;
        int ansIndex, cAnsIndex;
//            range loop through each question (only handle T,F and MC for now)
//        skip first block (empty)

        for (int i = 1; i < blocks.size() - 1; ++i) { //foreach block in blocks
            String s = blocks.get(i);
            //open answer
            index = s.indexOf(cToS(QuizUtils.OPEN));
            if (index != -1 && quiz.getWeekNum() != 12) {  //special case for open answer and skip week 12 due to the non-open answer having it
                questionText = s.substring(0, index).trim();
                qType = QuizUtils.OPEN_ANSWER;
                trimmed = s.substring(index).trim(); // get the answer text
                possibleAnswers = new ArrayList<>(Arrays.asList(trimmed.substring(1).split(cToS(QuizUtils.OPEN)))); //still put in array
                qAnswer = possibleAnswers.get(0).trim();
                quizQ = new QuizQuestion(questionText, qType, quiz.getWeekNum(), qAnswer, 0, qNum, possibleAnswers);
                sQuestions.add(quizQ);
                continue; //skip to next question
            }
            //skip the first block (empty) or contains
            index = s.indexOf(cToS(QuizUtils.ANSWER)); //first answer symbol
            if (index <= -1) {
                if (BuildConfig.DEBUG) {  // make sure index is within bounds
                    // handle the case where index is out of bounds
                    // for example, print an error message or set a default value for questionText
                    Log.e(TAG, "parser index <=-1: " + s + " " + index);
                }
            }

            questionText = s.substring(0, index).trim(); // get the question text
//                check if questionText contains any characters
            if (questionText.isEmpty()) continue; //empty string, break

//                now remove text until the first answer symbol
            trimmed = s.substring(index).trim(); // get the answer text
//split each answer on @ (answer)
            possibleAnswers = new ArrayList<>(Arrays.asList(trimmed.substring(1).split(cToS(QuizUtils.ANSWER)))); // split on @ (answer)
            possibleAnswers.replaceAll(String::trim); // trim each answer
            List<String> possibleAnsEdited = new ArrayList<>();

            ansIndex = 0; //reset answer index
            cAnsIndex = 0; //reset correct answer index for each question

            for (String ans : possibleAnswers) { //go through each answer and put in a list
//                get the array index of the correct answer
                possibleAnsEdited.add(ans.trim().replace("*", ""));
                if (ans.contains("*")) { //correct answer, hide the solution symbol
                    qAnswer = ans.trim().replace("*", "");
                    cAnsIndex = ansIndex; //set the correct answer index
                } else {
                    ++ansIndex;
                }
            }

            qType = getqType(possibleAnsEdited);
            quizQ = new QuizQuestion(questionText, qType, quiz.getWeekNum(), qAnswer, cAnsIndex, qNum, possibleAnsEdited);
            sQuestions.add(quizQ);
            ++qNum;
        }
        isFinishedParsing = true;
        return sQuestions;
    }

    public static boolean isIsFinishedParsing() {
        return isFinishedParsing;
    }


}
