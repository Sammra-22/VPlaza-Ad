package com.ooyala.admodule.controller;

import android.content.Context;
import android.text.TextUtils;

import com.ooyala.admodule.listener.AdListener;
import com.ooyala.admodule.listener.PlaybackListener;
import com.ooyala.admodule.model.Ad;
import com.ooyala.admodule.model.AdEvent;

import java.util.List;

/**
 * Created by Sam22 on 6/20/15.
 */
public class AdManager implements AdListener{

    private Context context;
    private PlaybackListener mListener;
    private Ad mAd;


    public AdManager(Context context){
        this.context = context;
        mAd =null;
    }

    public Ad getAd(){return mAd;}


    /**
     * Fetch VAST AD
     */
    public void fetchAd(PlaybackListener listener) {
        mListener = listener;
        String requestUrl = "http://kitcat.videoplaza.tv/proxy/distributor/v2?rt=vast_3.0&t=preroll1&tt=p";
        GetVastAd task= new GetVastAd(this);
        task.execute(requestUrl);
    }

    /**
     * Send an event tracker
     * @param event The event to be reported
     */
    public void fireTracker(AdEvent event) {
        List<String> trackerUrls = mAd.getTrackers(event);
        if(trackerUrls!=null && !trackerUrls.isEmpty()) {
            for(String trackerUrl:trackerUrls) {
                PushAdEvent task = new PushAdEvent(context, trackerUrl, event);
                task.execute(trackerUrl);
            }
        }
    }

    @Override
    public void onAdReceived(Ad ad) {
        mAd = ad;
        mListener.onPlaybackReady();
    }

    @Override
    public void onAdFailure() {
        mAd = null;
        mListener.onPlaybackReady();
    }


}
