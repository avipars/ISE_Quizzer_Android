package com.aviparshan.isequiz.Controller;


import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.aviparshan.isequiz.Controller.Questions.QuestionParser;

/**
 * ISE Quiz
 * Created by Avi Parshan on 3/3/2023 on com.aviparshan.isequiz.Controller
 */
public class VolleySingleton {

    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private static final String TAG = VolleySingleton.class.getSimpleName();

    private VolleySingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
            DiskBasedCache cache = new DiskBasedCache(ctx.getCacheDir(), 16 * 1024 * 1024);
            requestQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        }
        return requestQueue;
    }

    //    add request to queue with tag
    public <T> void addToRequestQueue(com.android.volley.Request<T> req, String tag) {
        getRequestQueue().add(req).setTag(tag).setShouldCache(true);
    }

    //    add request to queue
    public <T> void addToRequestQueue(com.android.volley.Request<T> req) {
        getRequestQueue().add(req).setShouldCache(true);
    }

    //    cancel all requests
    public void cancelAllRequests() {
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(com.android.volley.Request<?> request) {
                return true;
            }
        });
    }


    //    cancel request based on tag
    public void cancelRequest(String tag) {
        requestQueue.cancelAll(tag);
    }

    public static boolean isIsFinishedParsing() {
        return QuestionParser.isIsFinishedParsing();
    }

    public static void setIsFinishedParsing(boolean is) {
        QuestionParser.setIsFinishedParsing(is);
    }

}
