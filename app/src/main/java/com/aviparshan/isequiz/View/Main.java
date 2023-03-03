package com.aviparshan.isequiz.View;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aviparshan.isequiz.BuildConfig;
import com.aviparshan.isequiz.Controller.Quiz.QuizAdapter;
import com.aviparshan.isequiz.Controller.Quiz.QuizFetcher;
import com.aviparshan.isequiz.Models.Quiz;
import com.aviparshan.isequiz.R;

import java.util.List;

public class Main extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Quiz> quizzes;
    private static final String TAG = Main.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recycler_view);
        recyclerView = findViewById(R.id.rvList);
        QuizFetcher quizFetcher = QuizFetcher.getInstance(this);

        quizFetcher.fetchQuizzes(quizFetcherListener);
        // Set the layout manager of the RecyclerView to a LinearLayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Main.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        layoutManager.scrollToPosition(0);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

    }

    QuizFetcher.OnQuizzesFetchedListener quizFetcherListener = new QuizFetcher.OnQuizzesFetchedListener() {

        @Override
        public void onQuizzesFetched(List<Quiz> quiz) {
            onFetchSuccess(quiz);
        }

        @Override
        public void onFetchError(Exception e) {
            // Handle the quiz fetch error here
            if(BuildConfig.DEBUG){
                Log.e(TAG, "onFetchError", e);
            }
            else{
                Toast.makeText(Main.this, "Error Fetching Quizzes", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void passDataToNextActivity(Quiz quiz) {
        Intent intent = new Intent(this, WeekView.class);
        intent.putExtra("quiz", quiz);
        startActivity(intent);
        //StringRequest stringRequest = new StringRequest(Request.Method.GET, quiz.getUrl(), new Response.Listener<String>() {
        //    @Override
        //    public void onResponse(String response) {
        //        //send the list to the next activity
        //        //put a custom object in the intent
        //        QuestionFetcher.getInstance(Main.this, quiz);
        //
        //        quizQuestionList = QuestionFetcher.parser(response, quiz);
        //        if(!QuestionFetcher.isIsFinishedParsing()){
        //            while(!QuestionFetcher.isIsFinishedParsing()){
        //                try {
        //                    Thread.sleep(500);
        //                } catch (InterruptedException e) {
        //                    if(BuildConfig.DEBUG){
        //                        Log.e(TAG, "onResponseInteruptedEE: " + e.getMessage());
        //                    }
        //                    else{
        //                        Toast.makeText(Main.this, "Issue while parsing quiz", Toast.LENGTH_SHORT).show();
        //                    }
        //                }
        //            }
        //        }
        //        //intent.putExtra("quizQuestionList", (java.io.Serializable) quizQuestionList);
        //        startActivity(intent);
        //
        //    }
        //},  error ->{
        //        if (BuildConfig.DEBUG)
        //            Log.e(TAG, "onErrorResponse: " + error.toString());
        //        else
        //            Toast.makeText(this, "Error with quiz request", Toast.LENGTH_SHORT).show();
        //
        //});



        //{
        //    @Override
        //    public void onResponse(String response){
        //        quizQuestionList = VolleySingleton.parser(response, quiz);
        //        // wait until the parsing is done
        //        if (QuestionFetcher.isIsFinishedParsing()) {
        //            adapter.updateModel(quizQuestionList);
        //            startActivity(intent);
        //
        //        } else {
        //            //try again in a few
        //            while (!QuestionFetcher.isIsFinishedParsing()) {
        //                try {
        //                    Thread.sleep(500);
        //                } catch (InterruptedException e) {
        //                    if (BuildConfig.DEBUG) {
        //                        Log.e(TAG, "onResponseInteruptedEE: " + e.getMessage());
        //                    } else {
        //                        Toast.makeText(Main.this, "Issue while parsing quiz", Toast.LENGTH_SHORT).show();
        //                    }
        //                }
        //            }
        //            adapter.updateModel(quizQuestionList);
        //        }
        //    }
        //}
    //    StringRequest mStringRequest = new StringRequest(Request.Method.GET, quiz.getUrl(), new Response.Listener<String>() {
    //
    //        @Override
    //        public void onResponse(String response) {
    //            quizQuestionList = VolleySingleton.parser(response, quiz);
    //            // wait until the parsing is done
    //            if (QuestionFetcher.isIsFinishedParsing()) {
    //                adapter.updateModel(quizQuestionList);
    //            } else {
    //                //try again in a few
    //                while (!QuestionFetcher.isIsFinishedParsing()) {
    //                    try {
    //                        Thread.sleep(500);
    //                    } catch (InterruptedException e) {
    //                        if (BuildConfig.DEBUG) {
    //                            Log.e(TAG, "onResponseInteruptedEE: " + e.getMessage());
    //                        } else {
    //                            Toast.makeText(Main.this, "Issue while parsing quiz", Toast.LENGTH_SHORT).show();
    //                        }
    //
    //                    }
    //                }
    //                adapter.updateModel(quizQuestionList);
    //
    //            }
    //        }
    //    }, error -> {
    //        if (BuildConfig.DEBUG)
    //            Log.e(TAG, "onErrorResponse: " + error.toString());
    //        else
    //            Toast.makeText(this, "Error with quiz request", Toast.LENGTH_SHORT).show();
    //    });
    //    mStringRequest.setTag(TAG);
    //
    //    mRequestQueue.add(mStringRequest).setShouldCache(true);
    //
    //    prefetch the next quiz
    //   VolleySingleton.getInstance(this).addToRequestQueue(stringRequest, TAG);



    }

//    got the list
    void onFetchSuccess(List<Quiz> q){
        quizzes = q;
        // Create an instance of the QuizAdapter class, passing in the quiz list
        QuizAdapter qa = new QuizAdapter(quizzes);
        qa.setOnItemClickListener(((itemView, position) -> {
            Quiz quiz = quizzes.get(position);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
// Create a ClipData object to store the text
            ClipData clip = ClipData.newPlainText("label", quiz.getSubject());
// Copy the text to the clipboard
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "Copied: " + quiz.getSubject(), Toast.LENGTH_SHORT).show();
        }));
        qa.setOnItemLongClickListener(((itemView, position) -> {
            Quiz quiz = quizzes.get(position);
            passDataToNextActivity(quiz);
        }));


        // Set the adapter of the RecyclerView to the QuizAdapter instance
        recyclerView.setAdapter(qa);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Version: " + Quiz.version, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}