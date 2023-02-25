package com.aviparshan.isequiz.Controller.Questions;


import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aviparshan.isequiz.Models.Quiz;
import com.aviparshan.isequiz.Models.QuizQuestion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public static QuestionFetcher getInstance(Context context,Quiz q) {
        if (sQuizFetcher == null) {
            sQuizFetcher = new QuestionFetcher(context, q);
        }
        return sQuizFetcher;
    }
    public List<QuizQuestion> getQuizzes() {
        return sQuestions;
    }

    private String getTextFromUrl(String link){

        ArrayList<String> al=new ArrayList<>();

        try{
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
        }catch (IOException e){
            e.printStackTrace();
        }
        return al.get(0).toString();
    }

    public static List<String> LoadTxt(String filename) {
        String path = GetFilePath(filename, Arrays.asList(".txt"));
        if (path == null) {
            return new ArrayList<String>();
        }
        try {
            return Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
    public List<QuizQuestion> parser(String response){
//        go through txt file and separate each questions
//        convert response to array
        List<String> text = new ArrayList<>();
            Files.readAllLines(response)
            String[] lines = response.split("\\r?\\n"); // split on new line
        String joined = String.join("", text.toArray(new String[text.size()]));
        String[] blocks = joined.split("\\$");
        for(String block: blocks){

        }

    }
    public void getData() {
        // RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(mContext);

        // String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, quiz.getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                now parse the response

                Log.d(TAG,"Response" + response.toString());
                Toast.makeText(mContext, "Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Error :" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Error :" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
//        return parser(res[0]);

    }
    public void fetchData(Context mContext, Quiz q){
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = q.getUrl();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("Response is: ", response.substring(0, 500));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("That didn't work!", error.toString());
            }
        });
        queue.add(stringRequest);
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

    private static List<QuizQuestion> readQuestionsFromUrl(String url) {
        List<QuizQuestion> questions = new ArrayList<>();
        try {
            InputStream inputStream = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    QuizQuestion question = new QuizQuestion(parts[0], parts[1]);
                    questions.add(question);
                }
            }
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading quiz questions from URL", e);
        }
        return questions;
    }

    public interface FetchQuizQuestionsListener {
        void onFetchQuizQuestionsSuccess(List<QuizQuestion> questions);
        void onFetchQuizQuestionsFailure();
    }


}
