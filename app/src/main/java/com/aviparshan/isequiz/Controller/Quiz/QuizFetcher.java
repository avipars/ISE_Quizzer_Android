package com.aviparshan.isequiz.Controller.Quiz;


/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz
 */

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.aviparshan.isequiz.Models.Quiz;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class QuizFetcher {
    private static final String TAG = QuizFetcher.class.getSimpleName();
    private static QuizFetcher sQuizFetcher;

    private static final String QUIZZES_URL = "quizzes.json";
    private static final String QUIZZES_CACHE_DIR = "quizzes_cache";
    private static final String QUIZ_FILE_PREFIX = "q_";

    private final Context mContext;
    private List<Quiz> mQuizzes;

    public QuizFetcher(Context context) {
        mContext = context.getApplicationContext();
        mQuizzes = new ArrayList<>();
    }

    public static QuizFetcher getInstance(Context context) {
        if (sQuizFetcher == null) {
            sQuizFetcher = new QuizFetcher(context);
        }
        return sQuizFetcher;
    }


    public void fetchQuizzes(OnQuizzesFetchedListener listener) {
        new FetchQuizzesTask(listener).execute();
    }

//    public void checkForUpdates(OnQuizzesFetchedListener listener) {
//        new CheckForUpdatesTask(listener).execute();
//    }

    private void saveQuizToFile(Quiz quiz, File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(quiz.getData());
        outputStream.close();
    }

    private class FetchQuizzesTask extends AsyncTask<Void, Void, List<Quiz>> {
        private final OnQuizzesFetchedListener mListener;

        public FetchQuizzesTask(OnQuizzesFetchedListener listener) {
            mListener = listener;
        }

        @Override
        protected void onPostExecute(List<Quiz> quizzes) {
            mQuizzes = quizzes;
            if (mListener != null) {
                mListener.onQuizzesFetched(mQuizzes);
            }
        }

        @Override
        protected List<Quiz> doInBackground(Void... params) {
            List<Quiz> quizzes = new ArrayList<>();

            try {
                // Get quizzes JSON from the URL
                AssetManager assetManager = mContext.getAssets();
                InputStream is = assetManager.open(QUIZZES_URL);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                is.close();

                // Parse quizzes JSON and download quiz files
                JSONObject json = new JSONObject(builder.toString());
                Quiz.version = json.getDouble("version");
//                Toast.makeText(mContext, "V" + json.getDouble("version"), Toast.LENGTH_SHORT).show();
                JSONArray quizzesJson = json.getJSONArray("quizzes");

                for (int i = 0; i < quizzesJson.length(); ++i) {
                    JSONObject quizJson = quizzesJson.getJSONObject(i);
                    int quizNumber = quizJson.getInt("number");
                    String quizSubject = quizJson.getString("subject");
                    String quizUrl = quizJson.getString("path");

                    Quiz quiz = new Quiz(quizNumber, quizSubject, quizUrl);
                    quizzes.add(quiz);
                    
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching quizzes", e);
            }

            return quizzes;
        }
    }

//    private class CheckForUpdatesTask extends AsyncTask<Void, Void, List<Quiz>> {
//        private OnQuizzesFetchedListener mListener;
//
//        public CheckForUpdatesTask(OnQuizzesFetchedListener listener) {
//            mListener = listener;
//        }
//
//        @Override
//        protected List<Quiz> doInBackground(Void... params) {
//            List<Quiz> quizzes = new ArrayList<>();
//            try {
//                // Get quizzes JSON from the URL
//                URL url = new URL(QUIZZES_URL);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.connect();
//                InputStream inputStream = connection.getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                StringBuilder builder = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    builder.append(line);
//                }
//                inputStream.close();
//                connection.disconnect();
//
//                // Parse quizzes JSON and download updated quiz files
//                JSONObject json = new JSONObject(builder.toString());
//                JSONArray quizzesJson = json.getJSONArray("quizzes");
//                for (int i = 0; i < quizzesJson.length(); ++i) {
//                    JSONObject quizJson = quizzesJson.getJSONObject(i);
//                    int quizNumber = quizJson.getInt("number");
//                    String quizSubject = quizJson.getString("subject");
//                    String quizUrl = quizJson.getString("path");
//                    URL quizFileUrl = new URL(quizUrl);
//                    HttpURLConnection quizConnection = (HttpURLConnection) quizFileUrl.openConnection();
//                    quizConnection.connect();
//                    InputStream quizInputStream = quizConnection.getInputStream();
//                    byte[] quizData = new byte[quizConnection.getContentLength()];
//                    quizInputStream.read(quizData);
//                    quizInputStream.close();
//                    quizConnection.disconnect();
//                    Quiz quiz = new Quiz(quizNumber, quizSubject, quizData);
//                    quiz.setUrl(quizUrl);
//                    quizzes.add(quiz);
//                    // Check if quiz file has been updated
//                    File quizFile = new File(mContext.getCacheDir(), QUIZZES_CACHE_DIR + File.separator + QUIZ_FILE_PREFIX + quizNumber + ".txt");
//                    if (quizFile.exists()) {
//                        byte[] cachedQuizData = QuizUtils.readBytesFromFile(quizFile);
//                        if (!QuizUtils.isEqual(cachedQuizData, quizData)) {
//                            saveQuizToFile(quiz, quizFile);
//                        }
//                    } else {
//                        saveQuizToFile(quiz, quizFile);
//                    }
//                }
//            } catch (IOException | JSONException e) {
//                Log.e(TAG, "Error checking for updates", e);
//                Toast.makeText(mContext, "Error checking for updates: " + e.toString(), Toast.LENGTH_SHORT).show();
//            }
//
//            return quizzes;
//        }
//
//        @Override
//        protected void onPostExecute(List<Quiz> quizzes) {
//            mQuizzes = quizzes;
//            if (mListener != null) {
//                mListener.onQuizzesFetched(mQuizzes);
//            }
//        }
//    }

    public interface OnQuizzesFetchedListener {
        void onQuizzesFetched(List<Quiz> quiz);

        void onFetchError(Exception error);

    }


//    private List<String> parseQuizUrls(String response) {
//        List<String> urls = new ArrayList<>();
//        // Parse urls from response
//        return urls;
//    }
//
//    private List<QuizQuestion> parseQuestions(String response) {
//        List<QuizQuestion> questions = new ArrayList<>();
//        // Parse questions from response
//        return questions;
//    }

//    public void fetchQuizUrls(String url, final FetchQuizUrlsCallback callback) {
//        StringRequest stringRequest = new StringRequest(url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        List<String> urls = parseQuizUrls(response);
//                        mQuizUrls.addAll(urls);
//                        callback.onQuizUrlsFetched(urls);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, "Error fetching quiz urls: ", error);
//                        callback.onFetchError(error);
//                    }
//                });
//
//        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
//    }
}

