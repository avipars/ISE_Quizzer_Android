package com.aviparshan.isequiz.Models;


/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz
 */
public class Quiz {

    int weekNum;
    String subject;
    String url;
    byte[] data;

    public static double version = -2.0;

    String week;
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] ar) {
        this.data = ar;
    }

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

    public Quiz(int num, String subject, byte[] data) {
        this.weekNum = num;
        this.subject = subject;
        this.data = data;
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

}
