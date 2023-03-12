package com.aviparshan.isequiz.Models;


import androidx.annotation.NonNull;

import com.aviparshan.isequiz.Controller.Utils;

import java.io.Serializable;
import java.util.List;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz
 */
public class Quiz implements Serializable {

    public static double version = -1.0;
    private String subject, url, week;
    private int weekNum;
    private List<QuizQuestion> list;
    public Quiz(int num, String subject, String url) {
        this.subject = subject;
        this.url = url;
        setWeekNum(num);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(int num) {
        if (num >= 1 && num <= Utils.MaxWeeks) {
            this.weekNum = num;
            //take the week stirng from the xml
            this.week = String.format("Week %s", num);
        } else {
            //not valid week
            this.weekNum = -1;
            this.week = "Week -1";
        }
    }

    public List<QuizQuestion> getList() {
        return list;
    }

    public void setList(List<QuizQuestion> questions) {
        this.list = questions;
    }

    public String getWeek() {
        return week;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public int hashCode() {
        int result = getWeekNum();
        result = 31 * result + (getSubject() != null ? getSubject().hashCode() : 0);
        result = 31 * result + (getUrl() != null ? getUrl().hashCode() : 0);
        result = 31 * result + (getWeek() != null ? getWeek().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quiz)) return false;

        Quiz quiz = (Quiz) o;

        if (getWeekNum() != quiz.getWeekNum()) return false;
        if (getSubject() != null ? !getSubject().equals(quiz.getSubject()) : quiz.getSubject() != null)
            return false;
        if (getUrl() != null ? !getUrl().equals(quiz.getUrl()) : quiz.getUrl() != null)
            return false;
        return getWeek() != null ? getWeek().equals(quiz.getWeek()) : quiz.getWeek() == null;
    }

    @NonNull
    @Override
    public String toString() {
        return "Quiz" + "weekNum=" + weekNum + ", subject='" + subject + '\'' + ", url='" + url + '\'' + ", week='" + week + '\'' + '}';
    }
}
