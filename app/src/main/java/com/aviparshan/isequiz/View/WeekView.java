package com.aviparshan.isequiz.View;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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


//        get the bundle from the intent
        Bundle bundle = getIntent().getExtras();
        weekNum = bundle.getInt("quiz_week");
        url = bundle.getString("quiz_url");
        subject = bundle.getString("quiz_subject");
        q = new Quiz(weekNum,subject,url);
        setTitle(q.getWeek());
        setUp(q);
    }

    private void setUp(Quiz q){
        recyclerView = findViewById(R.id.rvList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(WeekView.this);
        recyclerView.setLayoutManager(layoutManager);
        questionFetcher = QuestionFetcher.getInstance(this, q);
        questionFetcher.fetchQuestions(questionListener);
    //    set the adapter
        adapter = new QuestionAdapter(quizQuestionList);
        recyclerView.setAdapter(adapter);
    }
QuestionFetcher.FetchQuestionListener questionListener = new QuestionFetcher.FetchQuestionListener() {
    @Override
    public void onFetchQuestionsSuccess(List<QuizQuestion> questions) {
        if(questions == null || questions.size() <= 0) {
            Toast.makeText(WeekView.this, "No questions found", Toast.LENGTH_SHORT).show();
            return;
        }
        if(quizQuestionList.size() <= 0){
        //    first time loading
            quizQuestionList = questions;
        }
        else{
            quizQuestionList.clear();
            quizQuestionList.addAll(questions);
        }
        adapter = new QuestionAdapter(quizQuestionList);
        quizQuestionList = questions;
        recyclerView.setAdapter(adapter);
        //click listener
        adapter.setOnItemClickListener(new QuestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(QuizQuestion question) {
                Toast.makeText(WeekView.this, "Item " + question.getQuestion() + " clicked", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onFetchQuestionsFailure() {
        Log.e("WeekView", "onFetchQuestionsFailure: " + "Failed to fetch questions");
        Toast.makeText(WeekView.this, "onFetchQuestionsFailure: ", Toast.LENGTH_SHORT).show();
    }



    };
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
            //invalidate the old RV if it is a different week
            if(adapter != null && quizQuestionList != null){
                quizQuestionList = questionFetcher.getQuizzes();
                adapter.updateModel(quizQuestionList);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
