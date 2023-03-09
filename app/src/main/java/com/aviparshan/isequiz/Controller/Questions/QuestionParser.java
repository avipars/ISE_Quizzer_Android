package com.aviparshan.isequiz.Controller.Questions;


import static com.aviparshan.isequiz.Controller.Utils.cToS;

import android.content.Context;
import android.util.Log;

import com.aviparshan.isequiz.BuildConfig;
import com.aviparshan.isequiz.Controller.Utils;
import com.aviparshan.isequiz.Models.Quiz;
import com.aviparshan.isequiz.Models.QuizQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz.Controller
 */
public class QuestionParser {
    private static final String TAG = QuestionParser.class.getSimpleName();
    private static final String CACHE_KEY = "cached_data";
    private static final List<QuizQuestion> sQuestions = new ArrayList<>();
    //list of lists of questions
    private static final List<List<QuizQuestion>> quizOfQuizzes = new ArrayList<>();
    private static QuestionParser sQuizFetcher;
    private static boolean isFinishedParsing = false;
    //set the quiz

    public QuestionParser(Context con) {
        Context mContext = con.getApplicationContext();
    }

    public static QuestionParser getInstance(Context context) {
        if (sQuizFetcher == null) {
            sQuizFetcher = new QuestionParser(context);
        }
        return sQuizFetcher;
    }

    /**
     * Parse the response from the server and form into list of question objects
     *
     * @param list text from server (quiz_#.txt)
     *
     */

    private static void printList(List<String> list) {
        int i = 0;
        for (String s : list) {
            Log.e(TAG, "print: " + s + " " + i);
            i++;
        }
    }

    public static List<QuizQuestion> parser(String response, Quiz quiz) {
        List<QuizQuestion> sQuestions = new ArrayList<>(); // create an empty list of questions
        String trimmed, questionText, qAnswer = "";
        List<String> possibleAnswers;
        String[] arr = response.split("");
        String joined = String.join("", arr);
        List<String> blocks = Arrays.asList(joined.split("\\$")); // split on $ (question)

        int index; //string index
        int qType, qNum = 0;
        int ansIndex, cAnsIndex;
//            range loop through each question (only handle T,F and MC for now)
//        skip first block (empty)

        for (int i = 1; i < blocks.size() - 1; ++i) { //foreach block in blocks
            String s = blocks.get(i);
            index = s.indexOf(cToS(Utils.OPEN)); //look for open answer

            if (index != -1 && quiz.getWeekNum() != 12) {  //special case for open answer and skip week 12 due to the non-open answer having it
                questionText = s.substring(0, index).trim();
                trimmed = s.substring(index).trim(); // get the answer text
                possibleAnswers = new ArrayList<>(Arrays.asList(trimmed.substring(1).split(cToS(Utils.OPEN)))); //still put in array
                qAnswer = possibleAnswers.get(0).trim(); //get the answer
                sQuestions.add(new QuizQuestion(questionText, Utils.OPEN_ANSWER, quiz.getWeekNum(), qAnswer, 0, i, possibleAnswers)); //add to list
                continue; //skip to next question
            }
            //skip the first block (empty) or contains
            index = s.indexOf(cToS(Utils.ANSWER)); //first answer symbol
            if (index <= -1) {
                if (BuildConfig.DEBUG) {  // make sure index is within bounds
                    // handle the case where index is out of bounds
                    // for example, print an error message or set a default value for questionText
                    Log.e(TAG, "parser index <=-1: " + s + " " + index);
                }
            }

            questionText = s.substring(0, index).trim(); // get the question text
//                check if questionText contains any characters
            if (questionText.isEmpty()) continue; //empty string, break

//                now remove text until the first answer symbol
            trimmed = s.substring(index).trim(); // get the answer text
//split each answer on @ (answer)
            possibleAnswers = new ArrayList<>(Arrays.asList(trimmed.substring(1).split(cToS(Utils.ANSWER)))); // split on @ (answer)
            possibleAnswers.replaceAll(String::trim); // trim each answer
            List<String> possibleAnsEdited = new ArrayList<>();

            ansIndex = 0; //reset answer index
            cAnsIndex = 0; //reset correct answer index for each question

            for (String ans : possibleAnswers) { //go through each answer and put in a list
//                get the array index of the correct answer
                possibleAnsEdited.add(ans.trim().replace("*", ""));
                if (ans.contains("*")) { //correct answer, hide the solution symbol
                    qAnswer = ans.trim().replace("*", "");
                    cAnsIndex = ansIndex; //set the correct answer index
                } else {
                    ++ansIndex; //increment the answer index to the next answer
                }
            }

            sQuestions.add(new QuizQuestion(questionText, Utils.getqType(possibleAnsEdited), quiz.getWeekNum(), qAnswer, cAnsIndex, i, possibleAnsEdited)); //add to list
            ++qNum;
        }
        isFinishedParsing = true; //set the parsing to finished

        return sQuestions;
    }

    public static boolean isIsFinishedParsing() {
        return isFinishedParsing;
    }

    public static void setIsFinishedParsing(boolean is) {
        isFinishedParsing = is;
    }

    public List<QuizQuestion> getQuizzes() {
        return sQuestions;
    }

    public void setQuizzes(List<QuizQuestion> quizzes) {
        sQuestions.addAll(quizzes);
    }

    public List<List<QuizQuestion>> getQuizOfQuizzes() {
        return quizOfQuizzes;
    }

    public static void setAllQuizOfQuizzes(List<List<QuizQuestion>> quizzes) {
        quizOfQuizzes.addAll(quizzes);
    }

    public List<List<QuizQuestion>> getQuizOfQuizzes(int index) {
        return quizOfQuizzes;
    }

    public static void setAllQuizOfQuizzes(List<List<QuizQuestion>> quizzes, int index) {
        quizOfQuizzes.addAll(index, quizzes);
    }

    public static void setQuizOfQuizzes(List<QuizQuestion> quizzes, int index) {
        //quizOfQuizzes.add(quizzes);
        quizOfQuizzes.set(index, quizzes);
    }
}