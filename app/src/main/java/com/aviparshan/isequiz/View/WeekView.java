package com.aviparshan.isequiz.View;


import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.aviparshan.isequiz.Controller.Utils;
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
    private QuestionAdapter adapter;
    private List<QuizQuestion> quizQuestionList = new ArrayList<>(); //new empty list
    private RequestQueue mRequestQueue;
    private Quiz q;

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
        // get the bundle from the intent
        q = (Quiz) getIntent().getSerializableExtra("quiz"); //  get the right week then fetch the questions (and answers) and cache them
        setTitle(q.getWeek());
        setUp(q);

        //    listeners for clicks
        adapter.setOnItemLongClickListener(new QuestionAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                //copy the answer to the clipboard
                String answer = adapter.getQuizQuestions().get(position).getCorrectAnswer();
                Utils.copyToClipboardWithMessage(WeekView.this, answer, String.format(getResources().getString(R.string.smart_copy), answer));
            }

        });

        adapter.setOnItemClickListener(new QuestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                //show the answer
                QuizQuestion question = adapter.getQuizQuestions().get(position);
                Toast.makeText(WeekView.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                adapter.toggleAnswer(position, question);
//                notify via diffUtil
//                adapter.updateModel(adapter.getQuizQuestions());
//                adapter.notifyItemChanged(adapter.getQuizQuestions().indexOf(question));
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
        volleySingleton = VolleySingleton.getInstance(this.getApplicationContext());
        mRequestQueue = volleySingleton.getRequestQueue();
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, quiz.getUrl(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //save as byte array then into asset

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
                            Utils.errorMessage(WeekView.this, e.getMessage(),R.string.issue_parsing, TAG);
                        }
                    }
                    adapter.updateModel(quizQuestionList);
                }
            }
        }, error -> {
            Utils.errorMessage(WeekView.this, error.toString(),R.string.error_req, TAG);

        });

        if (!volleySingleton.isCacheEmpty(quiz.getUrl())) {
            // Cache data available.
            String data = new String(Objects.requireNonNull(mRequestQueue.getCache().get(quiz.getUrl())).data);
            quizQuestionList = QuestionParser.parser(data, quiz);
            adapter.updateModel(quizQuestionList);

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

    //static method version of getData for pre-fetching
    public static void prefetcher(Quiz quiz, Context context) {
        //now fill the adapter, notify the adapter, and set the adapter to the recycler view
        // wait until the parsing is done
        //try again in a few
        VolleySingleton volleySingleton = VolleySingleton.getInstance(context.getApplicationContext());

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
        volleySingleton.addToRequestQueue(mStringRequest, TAG);


    }

    void startSearch(MenuItem item){
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(getString(R.string.search));
//        SearchManager searchManager=(SearchManager)getSystemService(Context.SEARCH_SERVICE);
//
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(adapter != null)
                {
                    adapter.filter(newText);
                    //save the query INTO suggestions
//
                }
                return true;
            }
        });
        item.setActionView(searchView);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_questions, menu);
        MenuItem item = menu.findItem(R.id.actionSearch);
        startSearch(item);

        //set up the checkbox for toggling answers
        MenuItem toggleAnswers = menu.findItem(R.id.action_toggle_answers);
        toggleAnswers.setChecked(Utils.getToggleState(this));
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
            //    clear cache entry
                volleySingleton.removeCacheItem(q.getUrl());
            //    get the data again
                getData(q);
            }
            return true;
        } else if (id == R.id.action_toggle_answers) {
            //toggle the answers and save the state of the toggle
            boolean toggle = !item.isChecked();
            adapter.setAllAnswers(toggle);
            item.setChecked(toggle); //toggle the check
            Utils.setToggleState(this, item.isChecked());

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
        if (volleySingleton.getRequestQueue() != null) {
            VolleySingleton.getInstance(this.getApplicationContext()).cancelRequest(TAG);
        }
    }
}
