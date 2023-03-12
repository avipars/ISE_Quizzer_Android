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
    private static final List<QuizQuestion> sQuestions = new ArrayList<>();
    //list of lists of questions
    private static final List<List<QuizQuestion>> quizOfQuizzes = new ArrayList<>();
    private static QuestionParser sQuizFetcher;
    private static boolean isFinishedParsing = false;
    //set the quiz
    private final Context mContext;
    public QuestionParser(Context con) {
        mContext = con.getApplicationContext();
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
            ++i;
        }
    }

//    /**
//     * Parse the response from the server and form into list of question objects
//     * @param response
//     * @param quiz one quiz with its questions
//     * @return
//     */
//    public static List<QuizQuestion> newParser(String response, Quiz quiz){
//        //response is the text from the server, quiz is the quiz object
//        List<String> list = new ArrayList<>(Arrays.asList(response.split("\n")));
//        List<QuizQuestion> questions = new ArrayList<>();
//        QuizQuestion question;
//        String trimmed;
//        String questionText = "";
//        int qType = 0;
//        int qNum = 0;
//
//        //remove the first line
//        list.remove(0);
//        //remove the last line
//        list.remove(list.size() - 1);
//        //get the first question with $
//        int firstQuestion = list.indexOf(cToS(Utils.QUESTION));
////        parse it
//        for (int i = firstQuestion; i < list.size(); i++) {
//            trimmed = list.get(i).trim();
//            if (trimmed.startsWith(cToS(Utils.QUESTION))) {
//                //if the question is not empty, add it to the list
//                if (!questionText.isEmpty()) {
//                    //split the answers into a list, and also denote the correct answer that has *
//
////                    question = new QuizQuestion(questionText, qType, qNum, quiz);
//                    questions.add(question);
//                    questionText = "";
//                }
//                //get the question number
//                qNum = Integer.parseInt(trimmed.substring(1));
//                //get the question type
//                qType = getqType(trimmed);
//            } else {
//                //add the question text
//                questionText += trimmed + "\n";
//            }
//        }
//        //add the last question
//        question = new QuizQuestion(questionText, qType, qNum, quiz);
//        questions.add(question);
//        return questions;
//    }


    /**
     * Parse the response from the server and form into list of question objects
     * @param response
     * @param quiz one quiz with its questions
     * @return
     */
    public static List<QuizQuestion> parser(String response, Quiz quiz) {

        List<QuizQuestion> sQuestions = new ArrayList<>(); // create an empty list of questions
        List<String> possibleAnswers; //create an empty list of possible answers

        String[] arr = response.split("");
        String joined = String.join("", arr);
        List<String> blocks = Arrays.asList(joined.split("\\$")); // split on $ (question)
        QuizQuestion qz;

        String trimmed = "", questionText = "", qAnswer = "";

        int index; //string index
        int qType, qNum = 0;
        int cAnsIndex;
//            range loop through each question (only handle T,F and MC for now)

//        skip first block (empty)
        String s;
        for (int i = 1; i < blocks.size() - 1; ++i) { //foreach block in blocks
            s = blocks.get(i);
            index = s.indexOf(cToS(Utils.OPEN)); //look for open answer
//                check if questionText contains any characters
            if (index != -1 && quiz.getWeekNum() != 12) {  //special case for open answer and skip week 12 due to the non-open answer having it
                questionText = s.substring(0, index).trim(); // get the question text
                trimmed = s.substring(index); // get the answer text
//                possibleAnswers is only a single element list
                possibleAnswers = new ArrayList<String>(
                        Arrays.asList(trimmed.substring(1).split("~"))); //still put in array
                qAnswer = possibleAnswers.get(0); //get the answer
                qType = Utils.OPEN_ANSWER;
                qz = new QuizQuestion(questionText, qType, quiz.getWeekNum(), qAnswer, 0, i, possibleAnswers);
                sQuestions.add(qz); //add to list
                //++qNum;
                continue; //skip to next question
            }
            //skip the first block (empty) or contains
            index = s.indexOf(Utils.ANSWER_S); //find answer symbol
            if (index <= -1) {  // make sure index is within bounds
                if(BuildConfig.DEBUG) {
                    Log.e(TAG, "parser index <=-1: " + s + " " + index);
                }
                continue;
            }
            questionText = s.substring(0, index).trim(); // get the question text
            if (questionText.isEmpty()) continue; //empty string, break (EOF)

//                now remove text until the first answer symbol
            trimmed = s.substring(index); // get the answer text
//split each answer on @ (answer) and remove the @ sign
            possibleAnswers = new ArrayList<>(Arrays.asList(trimmed.substring(1).trim().split(Utils.ANSWER_S)));
            // split on @ (answer) //
            possibleAnswers.replaceAll(String::trim); // trim each answer
//            printList(possibleAnswers);
            List<String> possibleAnsEdited = new ArrayList<>(); //clear the possibleEdit answers

            cAnsIndex = 0; //reset correct answer index for each question
            String ans2 = "";
            qType = Utils.UNKNOWN; //reset question type
            for(int j = 0; j < possibleAnswers.size(); ++j) {
                ans2 = possibleAnswers.get(j); //ans string
                possibleAnsEdited.add(ans2.trim().replace("*", ""));
                if(ans2.contains("*")){ //correct answer, hide the solution symbol
                    qAnswer = ans2.trim().replace("*", ""); //get rid of * and add to list
                    cAnsIndex = j; //set the correct answer index
//                    possibleAnsEdited.add(qAnswer);
                }
//                else {
//                    possibleAnsEdited.add(ans2);
//                }
            }

//            for (String ans : possibleAnswers) { //go through each answer and put in a list
////                get the array index of the correct answer
//                possibleAnsEdited.add(ans.trim().replace("*", ""));
//                if (ans.contains("*")) { //correct answer, hide the solution symbol
//                    qAnswer = ans.trim().replace("*", "");
//                    cAnsIndex = ansIndex; //set the correct answer index
//                } else {
//                    ++ansIndex; //increment the answer index to the next answer
////                    Log.d(TAG, "parser: " + ans + " " + ansIndex + " " + cAnsIndex);
//                }
//            }

            //get the question type based on # of entries
            qz = new QuizQuestion(questionText, Utils.getqType(possibleAnsEdited.size()), quiz.getWeekNum(), qAnswer, cAnsIndex, i, possibleAnsEdited);
            sQuestions.add(qz); //add to list
            //++qNum;
        }
        isFinishedParsing = true; //set the parsing to finished
        quiz.setList(sQuestions);
        return sQuestions;
    }
    public static boolean isIsFinishedParsing() {
        return isFinishedParsing;
    }

    public static void setIsFinishedParsing(boolean is) {
        isFinishedParsing = is;
    }

    public static void setAllQuizOfQuizzes(List<List<QuizQuestion>> quizzes) {
        quizOfQuizzes.addAll(quizzes);
    }

}