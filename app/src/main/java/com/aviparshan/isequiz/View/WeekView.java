package com.aviparshan.isequiz.View;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aviparshan.isequiz.Controller.Questions.QuestionAdapter;
import com.aviparshan.isequiz.Controller.Questions.QuestionFetcher;
import com.aviparshan.isequiz.Models.Quiz;
import com.aviparshan.isequiz.Models.QuizQuestion;
import com.aviparshan.isequiz.R;

import java.util.List;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/25/2023 on com.aviparshan.isequiz.View
 */
public class WeekView  extends AppCompatActivity  {
    private RecyclerView recyclerView;
    private QuestionAdapter adapter;
    private QuestionFetcher questionFetcher;
    private int weekNum;
    private String url, subject;
    private Quiz q;
    private List<QuizQuestion> quizQuestionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

//        get the right week then fetch the questions (and answers) and cache them
        recyclerView = findViewById(R.id.rvList);

//        get the bundle from the intent
        Bundle bundle = getIntent().getExtras();
        weekNum = bundle.getInt("quiz_week");
        url = bundle.getString("quiz_url");
        subject = bundle.getString("quiz_subject");
        q = new Quiz(weekNum,subject,url);
        setTitle(q.getWeek());

        questionFetcher = QuestionFetcher.getInstance(this, q);
        questionFetcher.getData();
        //when the data is ready, get the data

        quizQuestionList = questionFetcher.getQuizzes();
        adapter = new QuestionAdapter( quizQuestionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
//        pass in a quiz to the question fetcher

//        fetch the questions (and parse them, then load into the adapter)
//        questionFetcher.fetchQuestions(weekNum, new QuestionFetcher.OnQuestionsFetchedListener() {
//            @Override
//            public void onQuestionsFetched(List<QuizQuestion> quizQuestions) {
//                onFetchSuccess(quizQuestions);
//
//            }
//
//            @Override
//            public void onFetchError(Exception e) {
//                // Handle the quiz fetch error here
//
//            }
//        });

//        Toast.makeText(this, subject, Toast.LENGTH_SHORT).show();
//        change title
//        questionFetcher = new QuestionFetcher(this, q);
//        questionFetcher.fetchQuestions(weekNum, new QuestionFetcher.OnQuestionsFetchedListener() {
//            @Override
//            public void onQuestionsFetched(List<QuizQuestion> quizQuestions) {
//                onFetchSuccess(quizQuestions);
//            }
//
//            @Override
//            public void onFetchError(Exception e) {
//                // Handle the quiz fetch error here
//            }
//        });
//        adapter = new QuestionAdapter( quizQuestionList);

//        questionFetcher = new QuestionFetcher(this, q);
//        questionFetcher.fetchQuestions(weekNum, new QuestionFetcher.OnQuestionsFetchedListener() {
//            @Override
//            public void onQuestionsFetched(List<QuizQuestion> quizQuestions) {
//                onFetchSuccess(quizQuestions);
//            }
//
//            @Override
//            public void onFetchError(Exception e) {
//                // Handle the quiz fetch error here
//            }
//        });


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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
