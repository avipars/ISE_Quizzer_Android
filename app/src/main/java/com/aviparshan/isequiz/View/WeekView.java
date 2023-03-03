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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aviparshan.isequiz.BuildConfig;
import com.aviparshan.isequiz.Controller.Questions.QuestionAdapter;
import com.aviparshan.isequiz.Controller.Questions.QuestionFetcher;
import com.aviparshan.isequiz.Models.Quiz;
import com.aviparshan.isequiz.Models.QuizQuestion;
import com.aviparshan.isequiz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/25/2023 on com.aviparshan.isequiz.View
 */
public class WeekView extends AppCompatActivity {
    private RecyclerView recyclerView;
    private QuestionAdapter adapter;
    private int weekNum;
    private String url, subject;
    private Quiz q;
    private List<QuizQuestion> quizQuestionList;
    private static final String TAG = WeekView.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

//        get the right week then fetch the questions (and answers) and cache them
//        get the bundle from the intent
        Bundle bundle = getIntent().getExtras();
        weekNum = bundle.getInt("quiz_week");
        url = bundle.getString("quiz_url");
        subject = bundle.getString("quiz_subject");
        q = new Quiz(weekNum, subject, url);
        setTitle(q.getWeek());
        quizQuestionList = new ArrayList<>(); //new empty list
        setUp(q);

    //    listeners for clicks
        adapter.setOnItemLongClickListener(new QuestionAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                //copy the answer to the clipboard
                String answer = quizQuestionList.get(position).getCorrectAnswer();
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
                adapter.toggleAnswer(quizQuestionList.indexOf(question));
                adapter.notifyItemChanged(quizQuestionList.indexOf(question));
            }
        });
    }

    private void setUp(Quiz q) {
        recyclerView = findViewById(R.id.rvList);
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

        // RequestQueue initialized
        //    get the quiz object then, fetch the questions from the url
         mRequestQueue = Volley.newRequestQueue(this);
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 16 * 1024 * 1024);
        mRequestQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        mRequestQueue.start();
        //tag the request

        //now fill the adapter, notify the adapter, and set the adapter to the recycler view
        mStringRequest = new StringRequest(Request.Method.GET, quiz.getUrl(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                quizQuestionList = QuestionFetcher.parser(response, quiz);
                // wait until the parsing is done
                if (QuestionFetcher.isIsFinishedParsing()) {
                    adapter.updateModel(quizQuestionList);
                } else {
                    //try again in a few
                    while (!QuestionFetcher.isIsFinishedParsing()) {
                        try {
                            Thread.sleep(500);
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
            if (BuildConfig.DEBUG)
                Log.e(TAG, "onErrorResponse: " + error.toString());
            else
                Toast.makeText(this, "Error with quiz request", Toast.LENGTH_SHORT).show();
        });
        mStringRequest.setTag(TAG);

        mRequestQueue.add(mStringRequest).setShouldCache(true);
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
        if (id == R.id.action_retry) {
            //invalidate the old RV if it is a different week
            if (adapter != null && quizQuestionList != null) {
                adapter.addItems(quizQuestionList);
            }
            return true;
        } else if (id == R.id.action_toggle_answers) {
            //toggle the answers
            boolean val = !item.isChecked();
            adapter.setAllAnswers(val);
            item.setChecked(val);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    //
    //public JSONObject getVolleyCacheEntryByUrl(Activity c,
    //                                           String relative_url) {
    //    // RequestQueue queue = Volley.newRequestQueue(c);
    //    String cachedResponse = new String(AppController
    //            .getInstance()
    //            .getRequestQueue()
    //            .getCache()
    //            .get(c.getResources().getString(R.string.base_url)
    //                    + relative_url).data);
    //
    //    try {
    //        JSONObject cacheObj = new JSONObject(cachedResponse);
    //        Log.e("CacheResult", cacheObj.toString());
    //        return cacheObj;
    //
    //    } catch (JSONException e) {
    //        e.printStackTrace();
    //        return null;
    //    }
    //
    //}
}
