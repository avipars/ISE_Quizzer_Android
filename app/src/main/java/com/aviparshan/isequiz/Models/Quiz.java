package com.aviparshan.isequiz.Models;


import java.io.Serializable;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz
 */
public class Quiz implements Serializable {

    int weekNum;
    String subject;
    String url;


    public static double version = -2.0;

    String week;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Quiz(int num, String subject, String url) {
        this.weekNum = num;
        this.subject = subject;
        this.url = url;
        this.week = String.format(String.format("Week %s", num));
    }


    public int getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(int num) {
        this.weekNum = num;
        this.week = String.format(String.format("Week %s", num));

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

    @Override
    public int hashCode() {
        int result = getWeekNum();
        result = 31 * result + (getSubject() != null ? getSubject().hashCode() : 0);
        result = 31 * result + (getUrl() != null ? getUrl().hashCode() : 0);
        result = 31 * result + (getWeek() != null ? getWeek().hashCode() : 0);
        return result;
    }
}
