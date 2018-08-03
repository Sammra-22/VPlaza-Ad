package com.ooyala.admodule.controller;

import com.ooyala.admodule.helper.HttpAsyncTask;
import com.ooyala.admodule.listener.AdListener;
import com.ooyala.admodule.model.Ad;


/**
 * Created by Sam22 on 6/20/15.
 */
public class GetVastAd extends HttpAsyncTask {

    private AdListener mListener;

    public GetVastAd(AdListener listener){
        this.mListener = listener;
        setTask(Task.GET_AD);
    }

    @Override
    protected void onPostExecute(Ad ad) {
        super.onPostExecute(ad);
        if(ad!=null && ad.isValid())
            mListener.onAdReceived(ad);
        else
            mListener.onAdFailure();
    }



}
