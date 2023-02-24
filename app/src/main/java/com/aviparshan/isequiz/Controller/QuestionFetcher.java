package com.aviparshan.isequiz.Controller;


import android.content.Context;
import android.util.Log;

import com.aviparshan.isequiz.Models.QuizQuestion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz.Controller
 */
class QuestionFetcher {
    private static final String TAG = QuestionFetcher.class.getSimpleName();

    private static final String QUIZZES_URL = "https://example.com/quizzes.json";
    private static final String QUIZ_BASE_URL = "https://example.com/quizzes/";
    private static List<QuizQuestion> sQuestions = new ArrayList<>();

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
