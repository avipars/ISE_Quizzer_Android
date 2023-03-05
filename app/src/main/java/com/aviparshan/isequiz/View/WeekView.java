package com.aviparshan.isequiz.View;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.aviparshan.isequiz.BuildConfig;
import com.aviparshan.isequiz.Controller.Questions.QuestionAdapter;
import com.aviparshan.isequiz.Controller.Questions.QuestionParser;
import com.aviparshan.isequiz.Controller.VolleySingleton;
import com.aviparshan.isequiz.Models.Quiz;
import com.aviparshan.isequiz.Models.QuizQuestion;
import com.aviparshan.isequiz.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/25/2023 on com.aviparshan.isequiz.View
 */
public class WeekView extends AppCompatActivity {
    private static final String TAG = WeekView.class.getSimpleName();
    private static RequestQueue queue; //for volley
    private QuestionAdapter adapter;
    private List<QuizQuestion> quizQuestionList = new ArrayList<>(); //new empty list
    private RequestQueue mRequestQueue;
    private Quiz q;

    VolleySingleton volleySingleton;
    //static method version of getData for pre-fetching
    public static void prefetcher(Quiz quiz, Context context) {
        //now fill the adapter, notify the adapter, and set the adapter to the recycler view
        // wait until the parsing is done
        //try again in a few
        VolleySingleton volleySingleton = VolleySingleton.getInstance(context.getApplicationContext());
        queue = volleySingleton.getRequestQueue();

        StringRequest mStringRequest = new StringRequest(Request.Method.GET, quiz.getUrl(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                QuestionParser.parser(response, quiz);
                // wait until the parsing is done
                if (!QuestionParser.isIsFinishedParsing()) {
                    //try again in a few
                    while (!QuestionParser.isIsFinishedParsing()) {
                        try {
                            Thread.sleep(500); //wait half a second for a response
                        } catch (InterruptedException e) {
                            if (BuildConfig.DEBUG) {
                                Log.e(TAG, "onResponseInteruptedEE: " + e.getMessage());
                            }
                        }
                    }
                } else {
                    //queue.stop();
                    volleySingleton.stopRequestQueue();
                }
            }
        }, error -> {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "onErrorResponse: " + error.toString());
            }
        });

        //check if the request is already in cache
        //if it is, then don't make the request
        // Get a RequestQueue
        if(!volleySingleton.isRequestQueueRunning()) {
            volleySingleton.startRequestQueue();
        }
        //call the volley request
        VolleySingleton.getInstance(context).addToRequestQueue(mStringRequest, TAG);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

//        get the bundle from the intent
        q = (Quiz) getIntent().getSerializableExtra("quiz"); //        get the right week then fetch the questions (and answers) and cache them
        setTitle(q.getWeek());
        setUp(q);

        //    listeners for clicks
        adapter.setOnItemLongClickListener(new QuestionAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                //copy the answer to the clipboard
                String answer = adapter.getQuizQuestions().get(position).getCorrectAnswer();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("answer", answer);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(WeekView.this, R.string.copied, Toast.LENGTH_SHORT).show();
            }

        });

        adapter.setOnItemClickListener(new QuestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(QuizQuestion question) {
                //show the answer
                adapter.toggleAnswer(adapter.getQuizQuestions().indexOf(question));
                adapter.notifyItemChanged(adapter.getQuizQuestions().indexOf(question));
            }
        });
    }

    private void setUp(Quiz q) {
        RecyclerView recyclerView = findViewById(R.id.rvList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(WeekView.this);
        recyclerView.setLayoutManager(layoutManager);
        //    set the adapter
        adapter = new QuestionAdapter(quizQuestionList);
        recyclerView.setAdapter(adapter);
        getData(q);

        layoutManager.scrollToPosition(0);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
    }

    public void getData(Quiz quiz) {

        //now fill the adapter, notify the adapter, and set the adapter to the recycler view
        // wait until the parsing is done
        //try again in a few
        VolleySingleton volleySingleton = VolleySingleton.getInstance(this.getApplicationContext());
        mRequestQueue = volleySingleton.getRequestQueue();
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, quiz.getUrl(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                quizQuestionList = QuestionParser.parser(response, quiz);
                // wait until the parsing is done
                if (QuestionParser.isIsFinishedParsing()) {
                    adapter.updateModel(quizQuestionList);
                    volleySingleton.stopRequestQueue();
                } else {
                    //try again in a few
                    while (!QuestionParser.isIsFinishedParsing()) {
                        try {
                            Thread.sleep(500); //wait half a second for a response
                        } catch (InterruptedException e) {
                            if (BuildConfig.DEBUG) {
                                Log.e(TAG, "onResponseInteruptedEE: " + e.getMessage());
                            } else {
                                Toast.makeText(WeekView.this, "Issue while parsing quiz", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    adapter.updateModel(quizQuestionList);
                }
            }
        }, error -> {
            if (BuildConfig.DEBUG) Log.e(TAG, "onErrorResponse: " + error.toString());
            else Toast.makeText(this, R.string.error_req, Toast.LENGTH_SHORT).show();
        });

        if (!volleySingleton.isCacheEmpty(q.getUrl())) {
            // Cache data available.
            String data = new String(Objects.requireNonNull(mRequestQueue.getCache().get(quiz.getUrl())).data);
            quizQuestionList = QuestionParser.parser(data, quiz);
            adapter.updateModel(quizQuestionList);
            //if(BuildConfig.DEBUG){
            //    Log.d(TAG, "getData: " +quiz.getWeekNum()  +  " cache is not null");
            //}
        } else {

            // Cache data not exist.
            // Get a RequestQueue
            if(!volleySingleton.isRequestQueueRunning()) {
                volleySingleton.startRequestQueue();
            }
            volleySingleton.addToRequestQueue(mStringRequest, TAG);
            //call the volley request

            //    try to see if it is in cache
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Week: " + quiz.getWeekNum() + " cache miss/empty");
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_questions, menu);
        MenuItem item = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(adapter != null)
                {
                    adapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(adapter != null)
                {
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });
        item.setActionView(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_retry) {
            //invalidate the old RV if it is a different week
            if (adapter != null && quizQuestionList != null) {
            //    invalidate cache
            //    mRequestQueue.getCache().invalidate(q.getUrl(), true);

            //    clear cache entry
                volleySingleton.removeCacheItem(q.getUrl());
            //    get the data again
                getData(q);
                //adapter.addItems(quizQuestionList);

            }
            return true;
        } else if (id == R.id.action_toggle_answers) {
            //toggle the answers
            boolean val = !item.isChecked();
            adapter.setAllAnswers(val);
            item.setChecked(val);
            return true;

        } else if (id == R.id.actionSearch) {
            //search
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestQueue != null) {

            VolleySingleton.getInstance(this.getApplicationContext()).cancelRequest(TAG);
        }
    }
}
