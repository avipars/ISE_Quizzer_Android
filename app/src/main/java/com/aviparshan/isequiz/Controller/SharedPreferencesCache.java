package com.aviparshan.isequiz.Controller;


import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Cache;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * ISE Quiz
 * Created by Avi Parshan on 3/6/2023 on com.aviparshan.isequiz.Controller
 */
public class SharedPreferencesCache implements Cache {

   private SharedPreferences sharedPreferences;

   public SharedPreferencesCache(Context context) {
      this.sharedPreferences = context.getSharedPreferences("volley_cache", Context.MODE_PRIVATE);
   }

   @Override
   public Entry get(String key) {
      String json = sharedPreferences.getString(key, null);
      if (json != null) {
         try {
            JSONObject jsonObject = new JSONObject(json);
            Entry entry = new Entry();
            entry.data = jsonObject.getString("data").getBytes(StandardCharsets.UTF_8);
            entry.etag = jsonObject.getString("etag");
            entry.serverDate = jsonObject.getLong("serverDate");
            entry.ttl = jsonObject.getLong("ttl");
            return entry;
         } catch (JSONException e) {
            e.printStackTrace();
         }
      }
      return null;
   }

   @Override
   public void put(String key, Entry entry) {
      try {
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("data", new String(entry.data));
         jsonObject.put("etag", entry.etag);
         jsonObject.put("serverDate", entry.serverDate);
         jsonObject.put("ttl", entry.ttl);
         sharedPreferences.edit().putString(key, jsonObject.toString()).apply();
      } catch (JSONException e) {
         e.printStackTrace();
      }
   }

   @Override
   public void initialize() {
      // No initialization required
   }

   @Override
   public void invalidate(String key, boolean fullExpire) {
      sharedPreferences.edit().remove(key).apply();
   }

   @Override
   public void remove(String key) {
      sharedPreferences.edit().remove(key).apply();
   }

   @Override
   public void clear() {
      sharedPreferences.edit().clear().apply();
   }

}