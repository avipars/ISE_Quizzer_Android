package com.aviparshan.isequiz.Models;


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

//    private enum Type{ TRUE_FALSE, MULTIPLE_CHOICE, OPEN_ANSWER, UNKNOWN}
    private int weekNum;
    private String correctAnswer;
    private int correctAnswerNumber;

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

    public QuizQuestion(String question, int qType, int weekNum, String correctAnswer, int correctAnswerNumber, int id, List<String> possibleAnswers) {
        this.question = question;
        this.qType = qType;
        this.weekNum = weekNum;
        this.correctAnswer = correctAnswer;
        this.correctAnswerNumber = correctAnswerNumber;
        this.id = id;
        this.possibleAnswers = possibleAnswers;
    }

    public QuizQuestion(String quest, String correctAns){
        this.question = quest;
        this.correctAnswer = correctAns;

    }

    public QuizQuestion(String q, String ca, int type){
        question = q;
        correctAnswer = ca;
        setqType(type);
    }

    public QuizQuestion(String q, String ca, int type, int week){
        question = q;
        correctAnswer = ca;
        setqType(type);
        setWeekNum(week);
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
        return correctAnswerNumber;
    }

    public void setCorrectAnswerNumber(int correctAnswerNumber) {
        this.correctAnswerNumber = correctAnswerNumber;
    }

    public int getId() {
        return id;
    }
}
