package com.aviparshan.isequiz.Models;


import androidx.annotation.NonNull;

import com.aviparshan.isequiz.Controller.Utils;

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
    private String correctAnswer;
    private int correctAnswerIndex; //index to the right element in the possible answers list
    private int id = 0;

    private boolean showAnswer = false;
    private List<String> possibleAnswers;


    public QuizQuestion(String question, int qType, int weekNum, String correctAnswer, int id, List<String> possibleAnswers) {
        this.question = question;
        setqType(qType);
        setWeekNum(weekNum);
        this.correctAnswer = correctAnswer;
        this.id = id;
        this.possibleAnswers = possibleAnswers;
    }

    public QuizQuestion(String question, int qType, int weekNum, String correctAnswer, int correctAnswerNumber, int id, List<String> possibleAnswers) {
        this.question = question;
        setqType(qType);
        setWeekNum(weekNum);
        this.correctAnswer = correctAnswer;
        this.correctAnswerIndex = correctAnswerNumber;
        this.id = id;
        this.possibleAnswers = possibleAnswers;
    }


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getWeekNum() {
        return weekNum;
    }
//   set the static week number

    public void setWeekNum(int weekNum) { //only have 12 weeks worth of questions
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

    public void setShowAnswer(boolean showAnswer) {
        this.showAnswer = showAnswer;
    }

    public boolean getShowAnswer() {
        return showAnswer;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getqType() {
        return qType;
    }

    public void setqType(int type) {
        if(type != Utils.OPEN_ANSWER && type != Utils.TRUE_FALSE && type != Utils.MULTIPLE_CHOICE){
            qType = Utils.UNKNOWN;
        }
        else {
            qType = type;
        }
    }

    public List<String> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(List<String> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
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
