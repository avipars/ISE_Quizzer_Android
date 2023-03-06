package com.aviparshan.isequiz.Controller.Quiz;


/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz
 */

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.aviparshan.isequiz.Controller.Utils;
import com.aviparshan.isequiz.Models.Quiz;
import com.aviparshan.isequiz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class QuizFetcher {
    private static final String TAG = QuizFetcher.class.getSimpleName();
    private static QuizFetcher sQuizFetcher;
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

    public List<Quiz> getQuizzes() {
        return mQuizzes;
    }

    public void fetchQuizzes(OnQuizzesFetchedListener listener) {
        new FetchQuizzesTask(listener).execute();
    }

    public interface OnQuizzesFetchedListener {
        void onQuizzesFetched(List<Quiz> quiz);

        void onFetchError(Exception error);

    }

    private class FetchQuizzesTask extends AsyncTask<Void, Void, List<Quiz>> {
        private final OnQuizzesFetchedListener mListener;

        public FetchQuizzesTask(OnQuizzesFetchedListener listener) {
            mListener = listener;
        }

        @Override
        protected List<Quiz> doInBackground(Void... params) {
            List<Quiz> quizzes = new ArrayList<>();

            try {
                // Get quizzes JSON from the URL
                AssetManager assetManager = mContext.getAssets();
                InputStream is;
                try{
                    is = assetManager.open(Utils.QUIZZES_URL);

                }catch (IOException e){
                    Utils.errorMessage(mContext, "Error fetching quizzes.json", R.string.error_fetch,TAG);

                    return null;
                }

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
                JSONArray quizzesJson = json.getJSONArray("quizzes");

                for (int i = 0; i < quizzesJson.length(); ++i) {
                    JSONObject quizJson = quizzesJson.getJSONObject(i);
                    int quizNumber = quizJson.getInt("number");
                    String quizSubject = quizJson.getString("subject");
                    String quizUrl = quizJson.getString("path");

                    Quiz quiz = new Quiz(quizNumber, quizSubject, quizUrl);
                    quizzes.add(quiz);
                }

                //    now save the quizzes to the database

            } catch (IOException | JSONException e) {
                Utils.errorMessage(mContext, e.toString(), R.string.error_fetch,TAG);
                if (mListener != null) {
                    mListener.onFetchError(e);
                }
            }

            return quizzes;
        }

        @Override
        protected void onPostExecute(List<Quiz> quizzes) {
            mQuizzes = quizzes;
            if (mListener != null && mQuizzes != null) {
                mListener.onQuizzesFetched(mQuizzes);
            } else {
                Utils.errorMessage(mContext, "postExecute", R.string.error_fetch,TAG);
            }

        }
    }

}