package com.aviparshan.isequiz.Controller;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.util.Objects;

/**
 * ISE Quiz
 * Created by Avi Parshan on 3/3/2023 on com.aviparshan.isequiz.Controller
 */
public class VolleySingleton {

    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private static final String TAG = VolleySingleton.class.getSimpleName();
    private boolean isRunning = false;

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

    public void startRequestQueue() {
        requestQueue.start();
        isRunning = true;
    }

    public void stopRequestQueue() {
        requestQueue.stop();
        isRunning = false;
    }

    public void toggleRequestQueue() {
        if (isRunning) {
            stopRequestQueue();
        } else {
            startRequestQueue();
        }
    }

    public boolean isRequestQueueRunning() {
        return isRunning;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            isRunning = false;
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
            DiskBasedCache cache = new DiskBasedCache(ctx.getCacheDir(), 16 * 1024 * 1024); // 16MB cap
            requestQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
        //    set cache for days (default is 30)
            cache.initialize();

        }

        return requestQueue;
    }


    //    add request to queue with tag
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        getRequestQueue().add(req).setTag(tag).setShouldCache(true);
    }

    //    add request to queue
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req).setShouldCache(true);
    }

    //    cancel all requests
    public void cancelAllRequests() {
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }



    //    cancel request based on tag
    public void cancelRequest(String tag) {
        requestQueue.cancelAll(tag);
    }

    //function to see if cache is empty for a given url
    public boolean isCacheEmpty(String url) {
        return requestQueue.getCache().get(url) == null;
    }

    //get the cache entry for a given url
    public byte[] getCacheEntry(String url) {
        return Objects.requireNonNull(requestQueue.getCache().get(url)).data;
    }

    public String getCacheEntryAsString(String url) {
        return new String(getCacheEntry(url));
    }
    public static boolean isCacheEmpty(RequestQueue requestQueue, String url) {
        return requestQueue.getCache().get(url) == null;
    }

//    clear cache
    public void clearCache() {
        requestQueue.getCache().clear();
    }

//    remove cache item by url
    public void removeCacheItem(String url) {
        requestQueue.getCache().remove(url);
    }

//    invalidate cache item by url
    public void invalidateCacheItem(String url) {
        requestQueue.getCache().invalidate(url, true);
    }


}
