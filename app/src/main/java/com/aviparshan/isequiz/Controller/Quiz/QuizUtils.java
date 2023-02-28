package com.aviparshan.isequiz.Controller.Quiz;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * ISE Quiz
 * Created by Avi Parshan on 2/24/2023 on com.aviparshan.isequiz
 */
public class QuizUtils {

    public static final int TRUE_FALSE = 0;
    public static final int MULTIPLE_CHOICE = 1;
    public static final int OPEN_ANSWER = 2;
    public static final int UNKNOWN = 3;

    public static final char OPEN='~',ANSWER='@',QUESTION='$',SOLUTION='*',WEEK_NUM='#';

//    array of answer types
    public static final int[] ANSWER_TYPES = {TRUE_FALSE, MULTIPLE_CHOICE, OPEN_ANSWER};
//    chars as strings

    public static String charToString(char c){
        return String.valueOf(c);
    }

    public static String cToS(char c){
        return charToString(c);
    }

    public static byte[] readBytesFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }

    public static byte[] readBytesFromFile(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        inputStream.read(bytes);
        inputStream.close();
        return bytes;
    }

//    file to bytes
    public static byte[] convertUsingJavaNIO(File file)
    {
        byte[] fileBytes = null;
        try
        {
            fileBytes = Files.readAllBytes(file.toPath());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return fileBytes;
    }

    public static boolean isEqual(byte[] bytes1, byte[] bytes2) {
        if (bytes1 == null || bytes2 == null || bytes1.length != bytes2.length) {
            return false;
        }
        for (int i = 0; i < bytes1.length; i++) {
            if (bytes1[i] != bytes2[i]) {
                return false;
            }
        }
        return true;
    }

}