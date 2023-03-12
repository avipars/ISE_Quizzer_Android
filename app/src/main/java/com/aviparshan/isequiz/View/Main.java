package com.aviparshan.isequiz.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.aviparshan.isequiz.BuildConfig;
import com.aviparshan.isequiz.Controller.Questions.QuestionParser;
import com.aviparshan.isequiz.Controller.Quiz.QuizAdapter;
import com.aviparshan.isequiz.Controller.Quiz.QuizFetcher;
import com.aviparshan.isequiz.Controller.Utils;
import com.aviparshan.isequiz.Controller.VolleySingleton;
import com.aviparshan.isequiz.Models.Quiz;
import com.aviparshan.isequiz.Models.QuizQuestion;
import com.aviparshan.isequiz.R;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {

    private static final String TAG = Main.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Quiz> quizzes;

    private QuizFetcher quizFetcher;

    //avi's test stuff
    private QuestionParser qp;
    private List<QuizQuestion> quizQuestions;
    private List<List<QuizQuestion>> quizQuestionsList;
    VolleySingleton volleySingleton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recycler_view);

        if(BuildConfig.DEBUG)
        {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                    .detectLeakedClosableObjects()
                    .build());
        }

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

        if (!Utils.isConnectedToInternet(this)) //warn the user if they are not connected to the internet
        {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show(); //send a toast to the user
        }

//        else {
//            //    ch
//            //setUpListOfQuizzes();
//        }

        volleySingleton = VolleySingleton.getInstance(getApplicationContext());


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
        //Context ctx = this.getApplicationContext();
        volleySingleton = VolleySingleton.getInstance(getApplicationContext());
        if (Utils.isConnectedToInternet(this) && volleySingleton.isCacheEmpty(quiz.getUrl())) {
            WeekView.prefetcher(quiz, getApplicationContext());
            //setUpListOfQuizzes();
        }

        if (!Utils.isConnectedToInternet(this)) //warn the user if they are not connected to the internet
        {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show(); //send a toast to the user
        }
        startActivity(intent);
    }


    private void setUpListOfQuizzes() {
        //check if the cache is empty
        //if it is, then send a request to the server to get the data
        //if it is not, then get the data from the cache

        qp = QuestionParser.getInstance(this); // Create an instance of the QuizAdapter class

        //get request for each of the quizzes and then put into QP variable
        quizQuestionsList = new ArrayList<>();
        Quiz q;
        for (int i = 0; i < quizzes.size(); ++i) { //for each quiz
            q = quizzes.get(i); //get quiz at week i
            //    send a request to server to pull that week's questions and put it in cache and in the array of arrays


            //decide to send the request or not (fetch from cache)
            if (volleySingleton.isCacheEmpty(q.getUrl())) { //if the cache is not empty, then get the data from the cache
                // Cache data not exist.
                if (!volleySingleton.isRequestQueueRunning()) {
                    volleySingleton.startRequestQueue(); // Get a RequestQueue
                }
                volleySingleton.addToRequestQueue(request(q, volleySingleton,i), "QUIZ_FETCH" + i); //add the request to the queue

            } else {
                String data = volleySingleton.getCacheEntryAsString(q.getUrl()); //get the data from the cache
                quizQuestionsList.add(i, QuestionParser.parser(data, q)); //get the data from the cache - now put in list (and cache)

            }
        }
        QuestionParser.setAllQuizOfQuizzes(quizQuestionsList); //set the list of lists to the static variable in QuestionParser

    }

    StringRequest request(Quiz q, VolleySingleton vs, int index) {
        return new StringRequest(Request.Method.GET, q.getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Handle the quiz fetch success here
                quizQuestionsList.add(index,QuestionParser.parser(response, q)); //save the result to the right quiz (and cache)
                //quizQuestionsList.add(finalI,QuestionParser.parser(response, q));; //save the result to the right quiz (and cache)
                //cache it
                vs.stopRequestQueue();
            }
        }, error -> {
            // Handle the quiz fetch error here
            Utils.errorMessage(this, error.toString(), R.string.error_fetch, TAG);
        });
    }

    //    got the list
    void onFetchSuccess(List<Quiz> q) {
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

    void startSearch(String query) {
//        using the text listener to change text
        MyOnQueryTextListener myOnQueryTextListener = new MyOnQueryTextListener();
        myOnQueryTextListener.onQueryTextChange(query);

    }
//    search for all questions in all quizzes
    void startSearch(SearchView searchView){
        searchView.setQueryHint(getString(R.string.search_quiz));
        searchView.setOnQueryTextListener(new MyOnQueryTextListener());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) item.getActionView();
        startSearch(searchView);
        item.setActionView(searchView);

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
            Toast.makeText(this, String.format(getResources().getString(R.string.smart_version), Quiz.version), Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_cache) {
            //clear cache for each of the quizzes
            VolleySingleton.getInstance(this).clearCache();
//            setUpListOfQuizzes();
            return true;
        }
        else if(id == R.id.actionSearch){
//            search for all questions in all quizzes
//            search through all quizzes and all questions

            return true;
        }
//        else if(id == R.id.actionSearchQuiz){
////            search for all questions in all quizzes
////            get the search query
//            String query = item.getTitle().toString();
//            startSearch(query);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (volleySingleton.getRequestQueue() != null) {
            VolleySingleton.getInstance(this.getApplicationContext()).cancelRequest(TAG);
        }
    }

    private class MyOnQueryTextListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            //filter the list of quizzes
            List<Quiz> filteredList = new ArrayList<>();
            for (Quiz quiz : quizzes) {
                if (quiz.getSubject().toLowerCase().contains(newText.toLowerCase()) || quiz.getWeek().toLowerCase().contains(newText.toLowerCase())) {
                    filteredList.add(quiz);
                }
            }
            //update the adapter
            QuizAdapter qa = new QuizAdapter(filteredList);
            qa.setOnItemClickListener(((itemView, position) -> {
                Quiz quiz = quizzes.get(position);
                Utils.copyToClipboardWithMessage(Main.this, quiz.getSubject(), String.format(getResources().getString(R.string.smart_copy), quiz.getSubject()));

            }));
            qa.setOnItemLongClickListener(((itemView, position) -> {
                Quiz quiz = quizzes.get(position);
                passDataToNextActivity(quiz);
            }));
            recyclerView.setAdapter(qa);
            return true;
        }
    }
}