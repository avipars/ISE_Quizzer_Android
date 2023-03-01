package com.aviparshan.isequiz.Controller.Questions;


import static com.aviparshan.isequiz.Controller.Quiz.QuizUtils.cToS;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aviparshan.isequiz.Controller.Quiz.QuizUtils;
import com.aviparshan.isequiz.Models.Quiz;
import com.aviparshan.isequiz.Models.QuizQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz.Controller
 */
public class QuestionFetcher {
    private static final String TAG = QuestionFetcher.class.getSimpleName();
    private static final String CACHE_KEY = "cached_data";
    public boolean done = false;
    private static List<QuizQuestion> sQuestions;
    private final Context mContext;
    private Quiz quiz;
    private static QuestionFetcher sQuizFetcher;
    //    get the quiz object then, fetch the questions from the url
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    public QuestionFetcher(Context con, Quiz q) {
        this.mContext = con.getApplicationContext();
        this.quiz = q;
        sQuestions = new ArrayList<>();
    }

    public static QuestionFetcher getInstance(Context context, Quiz q) {
        if (sQuizFetcher == null) {
            sQuizFetcher = new QuestionFetcher(context, q);
        }
        //check if the quiz is the same as the one in the cache
//        if(sQuizFetcher.quiz != q){
//            sQuizFetcher.quiz = q; //update the quiz
//            sQuestions.clear(); //clear the existing questions
//        //    fetch the questions from the new quiz
//
//        }

        return sQuizFetcher;
    }

    public List<QuizQuestion> getQuizzes() {
        return sQuestions;
    }

    //private String getTextFromUrl(String link) {
    //
    //    ArrayList<String> al = new ArrayList<>();
    //
    //    try {
    //        URL url = new URL(link);
    //        URLConnection conn = url.openConnection();
    //        conn.setDoOutput(true);
    //        conn.connect();
    //
    //        InputStream is = conn.getInputStream();
    //        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
    //
    //        try (BufferedReader br = new BufferedReader(isr)) {
    //            String line;
    //            while ((line = br.readLine()) != null) {
    //                al.add(line);
    //            }
    //        }
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //    }
    //    return al.get(0).toString();
    //}


//    public JSONObject getVolleyCacheEntryByUrl(Activity c,
//                                               String relative_url) {
//        // RequestQueue queue = Volley.newRequestQueue(c);
//        String cachedResponse = new String(AppController
//                .getInstance()
//                .getRequestQueue()
//                .getCache()
//                .get(c.getResources().getString(R.string.base_url)
//                        + relative_url).data);
//
//        try {
//            JSONObject cacheObj = new JSONObject(cachedResponse);
//            Log.e("CacheResult", cacheObj.toString());
//            return cacheObj;
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }


    /**
     * Parse the response from the server and form into list of question objects
     * @param list text from server (quiz_#.txt)
     * @return List of QuizQuestion objects
     */

    private void printList(List<String> list){
        int i = 0;
        for(String s : list){

            Log.e(TAG, "print: " + s + " " + i);
            i++;
        }
    }
    public List<QuizQuestion> parser(String response) {
        List<QuizQuestion> sQuestions = new ArrayList<>(); // create an empty list of questions
        AsyncTask.execute(() -> {
            QuizQuestion q; // create a question object
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

            for (int i = 1; i < blocks.size()-1; ++i) { //foreach block in blocks
                String s = blocks.get(i);
                //open answer
                index = s.indexOf(cToS(QuizUtils.OPEN));
                if(index != -1 && quiz.getWeekNum() != 12){  //special case for open answer and skip week 12 due to the non-open answer having it
                    questionText = s.substring(0, index).trim();
                    qType = QuizUtils.OPEN_ANSWER;
                    trimmed = s.substring(index).trim(); // get the answer text
                    possibleAnswers = new ArrayList<>(Arrays.asList(trimmed.substring(1).split(cToS(QuizUtils.OPEN)))); //still put in array
                    qAnswer = possibleAnswers.get(0).trim();
                    q = new QuizQuestion(questionText, qType, quiz.getWeekNum(), qAnswer, 0, qNum, possibleAnswers);
                    sQuestions.add(q);
                    continue; //skip to next question
                }
                //skip the first block (empty) or contains

                index = s.indexOf(cToS(QuizUtils.ANSWER)); //first answer symbol
                //do that in java
                // questionText = new String(s.Take(index).ToArray()).Trim();
                if(index <= -1 ) {
                    Log.e(TAG, "parser: " + s + " " + index);
                    printList(blocks);
                    //continue; //no answer symbol, break
                }
                if (index >= 0) { // make sure index is within bounds
                    questionText = s.substring(0, index).trim(); // get the question text
                } else {
                    // handle the case where index is out of bounds
                    // for example, print an error message or set a default value for questionText
                    //Toast.makeText(mContext, "AVIHAHA", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "AVIHAHA: " + s + " " + index);
                    printList(blocks);
                    break;
                }

                //questionText = s.substring(0, index).trim(); // get the question text
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
                q = new QuizQuestion(questionText, qType, quiz.getWeekNum(), qAnswer, cAnsIndex, qNum, possibleAnsEdited);
                sQuestions.add(q);
                ++qNum;
            }
        });
        done = true;
        return sQuestions;

    }

    /**
     * get the question type given the possible answers list size
     *
     * @param possibleAnsEdited
     * @return
     */
    private int getqType(List<String> possibleAnsEdited) {
        int qType;
        if (possibleAnsEdited.size() == 1) {
            qType = QuizUtils.OPEN_ANSWER;
        } else if (possibleAnsEdited.size() == 2) {
            qType = QuizUtils.TRUE_FALSE;
        } else if (possibleAnsEdited.size() > 2) {
            qType = QuizUtils.MULTIPLE_CHOICE;
        } else {
            qType = QuizUtils.UNKNOWN;
        }
        return qType;
    }

    public void getData() {
        // RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(mContext);

        // String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, quiz.getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                sQuestions = parser(response);
//                now parse the response
//                Log.d(TAG, "Response" + response.toString());
                Toast.makeText(mContext, "R0-100" + response.toString().substring(0, 100), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Response0-200" + response.toString().substring(0, 200));

//                done = true;
//now fill the adapter, notify the adapter, and set the adapter to the recycler view


                //Toast.makeText(mContext, "Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen
            }
        }, error -> {
            Log.i(TAG, "Error :" + error.toString());

            Toast.makeText(mContext, "Error :" + error.toString(), Toast.LENGTH_SHORT).show();
            done = true;
        });

        mRequestQueue.add(mStringRequest);
    //    .setShouldCache(true)
    }


    public interface FetchQuestionListener {
        void onFetchQuestionsSuccess(List<QuizQuestion> questions);

        void onFetchQuestionsFailure();
    }

    public void fetchQuestions(FetchQuestionListener listener) {
        new QuestionFetcher.FetchQuestionsTask(listener).execute();
    }

    /*
    * add the quizWEEk
    * */

    private class FetchQuestionsTask extends AsyncTask<Void, Void, List<QuizQuestion>>{
        private final FetchQuestionListener listener;

        public FetchQuestionsTask(FetchQuestionListener listener) {
            this.listener = listener;
        }

        @Override
        protected List<QuizQuestion> doInBackground(Void... voids) {
        //    use the parse method i built
            getData();
            //wait for the data to be fetched (when done = true)
            while(!done){
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return sQuestions;

        }

        @Override
        protected void onPostExecute(List<QuizQuestion> questions) {
            super.onPostExecute(questions);
            if (questions != null && listener != null) {
                listener.onFetchQuestionsSuccess(questions);
            } else if(listener != null){
                //question null
                listener.onFetchQuestionsFailure();
                Log.e(TAG, "Listener is null");
            }
            sQuestions = questions;
        //   send the data to the adapter

        }
    }
}
