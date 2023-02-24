package com.aviparshan.isequiz.Models;


import java.net.URL;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz
 */
public class Quiz {

    int weekNum;
    String subject;
    URL path;
    String url;
    byte[] data;

    public byte[] getData() {
        return data;
    }

    public void getData(byte[] ar) {
        this.data = ar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Quiz(int weekNum, String subject, String url) {
        this.weekNum = weekNum;
        this.subject = subject;
        this.url = url;
    }

    public Quiz(int weekNum, String subject, byte[] data) {
        this.weekNum = weekNum;
        this.subject = subject;
        this.data = data;
    }

    public Quiz(int weekNum, String subject, URL path) {
        this.weekNum = weekNum;
        this.subject = subject;
        this.path = path;
    }

    public int getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(int weekNum) {
        this.weekNum = weekNum;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public URL getPath() {
        return path;
    }

    public void setPath(URL path) {
        this.path = path;
    }
}
