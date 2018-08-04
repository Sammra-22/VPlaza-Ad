package com.ooyala.admodule.controller;

import android.content.Context;
import android.widget.Toast;

import com.ooyala.admodule.helper.HttpAsyncTask;
import com.ooyala.admodule.model.Ad;
import com.ooyala.admodule.model.AdEvent;

/**
 * Created by Sam22 on 6/20/15.
 */
public class PushAdEvent extends HttpAsyncTask {

    private AdEvent mEvent;
    private String mTrackerUrl;
    private Context context;

    PushAdEvent(Context context, String tracker, AdEvent event) {
        this.context = context;
        mEvent = event;
        mTrackerUrl = tracker;
        setTask(Task.FIRE_PIXEL);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context, mEvent.getDisplayName() + " delivered", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPostExecute(Ad ad) {
        super.onPostExecute(ad);
    }
}
