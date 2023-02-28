package com.aviparshan.isequiz.Controller.Questions;


import static com.aviparshan.isequiz.Controller.Quiz.QuizUtils.cToS;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aviparshan.isequiz.Controller.Quiz.QuizUtils;
import com.aviparshan.isequiz.Models.Quiz;
import com.aviparshan.isequiz.Models.QuizQuestion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
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

    private static List<QuizQuestion> sQuestions = new ArrayList<>();
    private final Context mContext;
    private final Quiz quiz;
    private static QuestionFetcher sQuizFetcher;
    //    get the quiz object then, fetch the questions from the url
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    public QuestionFetcher(Context con, Quiz q) {
        this.mContext = con;
        this.quiz = q;
        sQuestions = new ArrayList<>();

    }

    public static QuestionFetcher getInstance(Context context, Quiz q) {
        if (sQuizFetcher == null) {
            sQuizFetcher = new QuestionFetcher(context, q);
        }
        return sQuizFetcher;
    }

    public List<QuizQuestion> getQuizzes() {
        return sQuestions;
    }

    private String getTextFromUrl(String link) {

        ArrayList<String> al = new ArrayList<>();

        try {
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);

            try (BufferedReader br = new BufferedReader(isr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    al.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return al.get(0).toString();
    }

    //public static List<String> LoadTxt(String filename) {
    //    String path = GetFilePath(filename, Arrays.asList(".txt"));
    //    if (path == null) {
    //        return new ArrayList<String>();
    //    }
    //    try {
    //        return Files.readAllLines(Paths.get(path));
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //        return new ArrayList<String>();
    //    }
    //}

    /**
     * Parse the response from the server and form into list of question objects
     * @param response text from server (quiz_#.txt)
     * @return List of QuizQuestion objects
     */
    public List<QuizQuestion> parser(String response) {

        String[] questions = response.split("\\$"); // split on $ (question)
        List<String> text = new ArrayList<>(Arrays.asList(questions)); // convert to list
        List<QuizQuestion> sQuestions = new ArrayList<>(); // create a list of questions
        QuizQuestion q; // create a question object
        String trimmed, questionText, qAnswer = "";
        List<String> possibleAnswers;
        int index; //string index
        int qType;
        int qNum = 0;
//            range loop through each question (only handle T,F and MC for now)
        for (String s : text) { //foreach block in blocks

            index = s.indexOf(QuizUtils.OPEN_ANSWER);
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
            //if (s.contains(QuizUtils.OPEN_ANSWER)) {
            //    questionText = s.substring(0, s.indexOf(cToS(QuizUtils.OPEN_ANSWER))).trim();
            //    q = new QuizQuestion(questionText, QuizUtils.OPEN_ANSWER, quiz.getWeekNum(), qAnswer, 0, qNum, new ArrayList<>());
            //    sQuestions.add(q);
            //    ++qNum;
            //    continue; //skip to next question
            //}

            index = s.indexOf(QuizUtils.ANSWER); //first answer symbol
            //do that in java
            // questionText = new String(s.Take(index).ToArray()).Trim();
            Log.e(TAG, "parser: " + s + " " + index);
            if(index <= 0) break; //no answer symbol, break

            questionText = s.substring(0, index).trim(); // get the question text
//                check if questionText contains any characters
            if (questionText.isEmpty()) break; //empty string, break

//                now remove text until the first answer symbol
            trimmed = s.substring(index).trim(); // get the answer text
//split each answer on @ (answer)
            possibleAnswers = new ArrayList<>(Arrays.asList(trimmed.substring(1).split(cToS(QuizUtils.ANSWER)))); // split on @ (answer)
            possibleAnswers.replaceAll(String::trim); // trim each answer
            List<String> possibleAnsEdited = new ArrayList<>();

            int ansIndex = 0, cAnsIndex = 0;

            for (String ans : possibleAnswers) { //go through each answer and put in a list
//                get the array index of the correct answer
                possibleAnsEdited.add(ans.trim().replace(cToS(QuizUtils.SOLUTION), ""));
                if (ans.contains(cToS(QuizUtils.SOLUTION))) { //correct answer, hide the solution symbol
                    qAnswer = ans.trim().replace(cToS(QuizUtils.SOLUTION), "");
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
//                now parse the response
                Log.d(TAG, "Response" + response.toString());

                sQuestions = parser(response);
                //Toast.makeText(mContext, "Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Error :" + error.toString());

                Toast.makeText(mContext, "Error :" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        mRequestQueue.add(mStringRequest);
//        return parser(res[0]);

    }



//    public static void fetchQuizQuestions(Context context, final FetchQuizQuestionsListener listener) {
//        String quizzesJson = readQuizzesJsonFromAsset(context);
//        if (quizzesJson == null) {
//            listener.onFetchQuizQuestionsFailure();
//            return;
//        }
//
//        try {
//            JSONObject quizzes = new JSONObject(quizzesJson);
//            JSONArray quizArray = quizzes.getJSONArray("quizzes");
//
//            for (int i = 0; i < quizArray.length(); i++) {
//                JSONObject quiz = quizArray.getJSONObject(i);
//                String quizName = quiz.getString("name");
//                String quizUrl = QUIZ_BASE_URL + quiz.getString("url");
//
//                List<QuizQuestion> questions = readQuestionsFromUrl(quizUrl);
//                for (QuizQuestion question : questions) {
//
//                    question.setSubject(quizName);
//                }
//                sQuestions.addAll(questions);
//            }
//
//            listener.onFetchQuizQuestionsSuccess(sQuestions);
//        } catch (JSONException e) {
//            Log.e(TAG, "Error parsing quizzes JSON", e);
//            listener.onFetchQuizQuestionsFailure();
//        }
//    }

    private static String readQuizzesJsonFromAsset(Context context) {
        String json = null;
        try {
            InputStream inputStream = context.getAssets().open("quizzes.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Log.e(TAG, "Error reading quizzes JSON from asset", e);
        }
        return json;
    }

//    private static List<QuizQuestion> readQuestionsFromUrl(String url) {
//        List<QuizQuestion> questions = new ArrayList<>();
//        try {
//            InputStream inputStream = new URL(url).openStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.split("\\|");
//                if (parts.length == 2) {
//                    QuizQuestion question = new QuizQuestion(parts[0], parts[1]);
//                    questions.add(question);
//                }
//            }
//            reader.close();
//            inputStream.close();
//        } catch (IOException e) {
//            Log.e(TAG, "Error reading quiz questions from URL", e);
//        }
//        return questions;
//    }

    public interface FetchQuizQuestionsListener {
        void onFetchQuizQuestionsSuccess(List<QuizQuestion> questions);

        void onFetchQuizQuestionsFailure();
    }


}
