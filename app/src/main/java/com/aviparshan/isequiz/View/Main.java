package com.aviparshan.isequiz.View;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aviparshan.isequiz.Controller.Quiz.QuizAdapter;
import com.aviparshan.isequiz.Controller.Quiz.QuizFetcher;
import com.aviparshan.isequiz.Models.Quiz;
import com.aviparshan.isequiz.R;
import com.aviparshan.isequiz.databinding.ActivityMainBinding;

import java.util.List;

public class Main extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private RecyclerView recyclerView;
//    private QuestionAdapter qa;
    private QuizAdapter qa;
    private List<Quiz> quizzes;
//    private List<QuizQuestion> quizQuestionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        setSupportActionBar(binding.toolbar);
//
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//
//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        setContentView(R.layout.recycler_view);
        recyclerView = findViewById(R.id.rvList);
        QuizFetcher quizFetcher = new QuizFetcher(this);


        QuizFetcher.OnQuizzesFetchedListener quizFetcherListener = new QuizFetcher.OnQuizzesFetchedListener() {

            @Override
            public void onQuizzesFetched(List<Quiz> quiz) {
                onFetchSuccess(quiz);
            }

            @Override
            public void onFetchError(Exception e) {
                // Handle the quiz fetch error here
                Toast.makeText(Main.this, "Error: "+ e.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        quizFetcher.fetchQuizzes(quizFetcherListener);
//        Toast.makeText(this, "Version is: " + Quiz.version, Toast.LENGTH_SHORT).show();

    }

//    got the list
    void onFetchSuccess(List<Quiz> q){
        quizzes = q;
        // Create an instance of the QuizAdapter class, passing in the quiz list
        qa = new QuizAdapter(quizzes);
        qa.setOnItemClickListener(((itemView, position) -> {
            Quiz quiz = quizzes.get(position);
            Toast.makeText(this, "Clicked: " + quiz.getSubject(), Toast.LENGTH_SHORT).show();
        }));
        qa.setOnItemLongClickListener(((itemView, position) -> {
            Quiz quiz = quizzes.get(position);
            Toast.makeText(this, "LongClick: " + quiz.getWeekNum(), Toast.LENGTH_SHORT).show();
        }));
        // Set the layout manager of the RecyclerView to a LinearLayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Main.this);
        recyclerView.setLayoutManager(layoutManager);

        // Set the adapter of the RecyclerView to the QuizAdapter instance
        recyclerView.setAdapter(qa);

        Toast.makeText(this, "V" + Quiz.version, Toast.LENGTH_SHORT).show();

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


//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}