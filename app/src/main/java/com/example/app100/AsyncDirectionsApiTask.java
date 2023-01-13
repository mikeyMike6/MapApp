package com.example.app100;

import static android.content.ContentValues.TAG;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AsyncDirectionsApiTask extends AsyncTask<Void, Void, String> {
    private WeakReference<MainActivity> activityRef;
    private String mUrl;
    public AsyncDirectionsApiTask(MainActivity mainActivity, String url) {
        activityRef = new WeakReference<>(mainActivity);
        mUrl = url;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(mUrl).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "Error" + e.getMessage();
        }
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.startsWith("Error")) {
            Log.d(TAG, "SERVER ERROR: " + result);
        }
        else {
            Log.d(TAG, "jsonData: " + result);
            MainActivity activity = activityRef.get();
            activity.processJsonData(result);
        }
    }

}
