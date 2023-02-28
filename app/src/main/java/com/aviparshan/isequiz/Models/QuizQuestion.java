package com.aviparshan.isequiz.Models;


import androidx.annotation.NonNull;

import com.aviparshan.isequiz.Controller.Quiz.QuizUtils;

import java.io.Serializable;
import java.util.List;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz
 */
public class QuizQuestion implements Serializable {

    private String question;

    private int qType; //fromQuizUtils
    private int weekNum;
//    static variable for week number
    private static int weekNumber = 0;
    private String correctAnswer;
    private int correctAnswerIndex; //index to the right element in the possible answers list
    private int id = 0;

    private List<String> possibleAnswers;
    public void setId(int id) {
        this.id = id;
    }

    public int getqType() {
        return qType;
    }

    public void setqType(int type) {
        if(type <= 2 && type >= 0 ){
            qType = type;
        }
        else{
            qType = QuizUtils.UNKNOWN;
        }
    }

    public List<String> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(List<String> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    public QuizQuestion(String question, int qType, int weekNum, String correctAnswer, int id, List<String> possibleAnswers) {
        this.question = question;
        this.qType = qType;
        this.weekNum = weekNum;
        this.correctAnswer = correctAnswer;
        this.id = id;
        this.possibleAnswers = possibleAnswers;
    }

    public QuizQuestion(String question, int qType, int weekNum, String correctAnswer, int correctAnswerNumber, int id, List<String> possibleAnswers) {
        this.question = question;
        this.qType = qType;
        this.weekNum = weekNum;
        this.correctAnswer = correctAnswer;
        this.correctAnswerIndex = correctAnswerNumber;
        this.id = id;
        this.possibleAnswers = possibleAnswers;
    }

//    OPEN ANSWER
    public QuizQuestion(String quest, String correctAns){
        this.question = quest;
        this.correctAnswer = correctAns;
        setqType(QuizUtils.OPEN_ANSWER);

    }

//    TRUE FALSE
    public QuizQuestion(String q, String ca, int type){
        question = q;
        correctAnswer = ca;
        setqType(type);
    }


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

//   set the static week number
    public static void setWeekNumber(int weekNumber) {
        if(weekNumber >= 0 && weekNumber <= 12)
            QuizQuestion.weekNumber = weekNumber;
        else
            QuizQuestion.weekNumber = -1;
    }
//    static method to get the week number
    public static int getWeekNumber() {
        return weekNumber;
    }

    public int getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(int weekNum) {
        if(weekNum >= 0 && weekNum <= 12){
            this.weekNum = weekNum;
        }
        else{
            this.weekNum = -1;
        }
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getCorrectAnswerNumber() {
        return correctAnswerIndex;
    }

    public void setCorrectAnswerNumber(int correctAnswerNumber) {
        this.correctAnswerIndex = correctAnswerNumber;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String toString(){
        return "Question: " + question + " Week: " + weekNum + " Correct Answer: " + correctAnswer + " Correct Answer Number: " + correctAnswerIndex + " ID: " + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuizQuestion)) return false;

        QuizQuestion that = (QuizQuestion) o;

        if (getqType() != that.getqType()) return false;
        if (getWeekNum() != that.getWeekNum()) return false;
        if (getCorrectAnswerNumber() != that.getCorrectAnswerNumber()) return false;
        if (getId() != that.getId()) return false;
        if (!getQuestion().equals(that.getQuestion())) return false;
        if (!getCorrectAnswer().equals(that.getCorrectAnswer())) return false;
        return getPossibleAnswers() != null ? getPossibleAnswers().equals(that.getPossibleAnswers()) : that.getPossibleAnswers() == null;
    }

    @Override
    public int hashCode() {
        int result = getQuestion().hashCode();
        result = 31 * result + getqType();
        result = 31 * result + getWeekNum();
        result = 31 * result + getCorrectAnswer().hashCode();
        result = 31 * result + getCorrectAnswerNumber();
        result = 31 * result + getId();
        result = 31 * result + (getPossibleAnswers() != null ? getPossibleAnswers().hashCode() : 0);
        return result;
    }
}
