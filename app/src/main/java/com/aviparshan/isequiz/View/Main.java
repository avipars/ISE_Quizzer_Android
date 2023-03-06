package com.aviparshan.isequiz.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.StringRequest;
import com.aviparshan.isequiz.Controller.Questions.QuestionParser;
import com.aviparshan.isequiz.Controller.Quiz.QuizAdapter;
import com.aviparshan.isequiz.Controller.Quiz.QuizFetcher;
import com.aviparshan.isequiz.Controller.Utils;
import com.aviparshan.isequiz.Controller.VolleySingleton;
import com.aviparshan.isequiz.Models.Quiz;
import com.aviparshan.isequiz.Models.QuizQuestion;
import com.aviparshan.isequiz.R;

import java.util.List;

public class Main extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Quiz> quizzes;
    List<QuizQuestion> quizQuestions;
    List<List<QuizQuestion>> quizQuestionsList;
    private static final String TAG = Main.class.getSimpleName();
    QuizFetcher quizFetcher;
    QuestionParser qp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recycler_view);
        recyclerView = findViewById(R.id.rvList);
        quizFetcher = QuizFetcher.getInstance(this);

        quizFetcher.fetchQuizzes(quizFetcherListener);
        // Set the layout manager of the RecyclerView to a LinearLayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Main.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        layoutManager.scrollToPosition(0);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        if(!Utils.isConnectedToInternet(this)) //warn the user if they are not connected to the internet
        {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show(); //send a toast to the user
        }
        else{
        //    ch
            setUpListOfQuizzes();
        }

    }


    QuizFetcher.OnQuizzesFetchedListener quizFetcherListener = new QuizFetcher.OnQuizzesFetchedListener() {

        @Override
        public void onQuizzesFetched(List<Quiz> quiz) {
            onFetchSuccess(quiz);
        }

        @Override
        public void onFetchError(Exception e) {
            // Handle the quiz fetch error here
            Utils.errorMessage(Main.this, e.toString(), R.string.error_fetch, TAG);
        }
    };

    public void passDataToNextActivity(Quiz quiz) {
        Intent intent = new Intent(this, WeekView.class);
        intent.putExtra("quiz", quiz);
        //submit web request to get the quiz
        //if connected to internet and cache is empty, prefetch the data
        Context ctx = this.getApplicationContext();
        if(Utils.isConnectedToInternet(ctx) && VolleySingleton.getInstance(ctx).isCacheEmpty(quiz.getUrl())){
            WeekView.prefetcher(quiz, ctx);
        }

        if(!Utils.isConnectedToInternet(ctx)) //warn the user if they are not connected to the internet
        {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show(); //send a toast to the user
        }
        startActivity(intent);
    }


    private void setUpListOfQuizzes() {
        //check if the cache is empty
        //if it is, then send a request to the server to get the data
        //if it is not, then get the data from the cache

        //TODO finish method
        // Create an instance of the QuizAdapter class, passing in the quiz list
        qp = new QuestionParser(this);
        //get request for each of the quizzes and then put into QP variable
        Quiz q;

        for(int i = 0; i < quizzes.size(); ++i){
            q = quizzes.get(i); //get quiz at week i

        //    send a request to server to pull that week's questions and put it in cache and in the array of arrays
            VolleySingleton volleySingleton = VolleySingleton.getInstance(getApplicationContext());

            Quiz finalQ = q;
            int finalI = i;
            StringRequest request = new StringRequest(q.getUrl(), response -> {

                quizQuestionsList.add(finalI,QuestionParser.parser(response, finalQ));; //save the result to the right quiz (and cache)
                //cache it


            }, error -> {
                // Handle the quiz fetch error here
                Utils.errorMessage(Main.this, error.toString(), R.string.error_fetch, TAG);
            });
            volleySingleton.addToRequestQueue(request);
        }
        QuestionParser.setAllQuizOfQuizzes(quizQuestionsList); //set the list of lists to the static variable in QuestionParser

    }

//    got the list
    void onFetchSuccess(List<Quiz> q){
        quizzes = q; //set the quizzes variable to the list of quizzes

        // Create an instance of the QuizAdapter class, passing in the quiz list
        QuizAdapter qa = new QuizAdapter(quizzes);
        qa.setOnItemClickListener(((itemView, position) -> {
            Quiz quiz = quizzes.get(position);
            Utils.copyToClipboardWithMessage(this, quiz.getSubject(), String.format(getResources().getString(R.string.smart_copy), quiz.getSubject()));

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
            Toast.makeText(this, String.format(getResources().getString(R.string.smart_version),Quiz.version), Toast.LENGTH_SHORT).show();
            return true;
        } else if(id == R.id.action_cache){
            //clear cache for each of the quizzes
            VolleySingleton.getInstance(this).clearCache();
            setUpListOfQuizzes();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}