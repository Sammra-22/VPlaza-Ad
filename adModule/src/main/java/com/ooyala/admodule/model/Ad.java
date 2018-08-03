package com.ooyala.admodule.model;

import android.text.TextUtils;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sam22 on 6/20/15.
 */
public class Ad implements Serializable {

    String name;
    String id;
    String mediaAssetUrl, clickThrough;
    ArrayList<String> impressions;
    Map<AdEvent, ArrayList<String>> eventTrackers;

    public Ad() {
        name ="";
        id = "";
        mediaAssetUrl = null;
        clickThrough = null;
        eventTrackers = new HashMap<>();
        impressions = new ArrayList<>();
    }

    public void setName(String name){this.name = name;}

    public void setId(String id){this.id = id;}

    public void setMediaUrl(String mediaAssetUrl){this.mediaAssetUrl = mediaAssetUrl;}

    public void setClickUrl(String clickThrough){this.clickThrough = clickThrough;}

    public void setImpressionUrl(String impressionUrl){
        this.impressions.add(impressionUrl);
    }

    public void setTracker(String trackerUrl, AdEvent event){
        List<String> trackers = this.eventTrackers.get(event);
        if(trackers!=null)
            trackers.add(trackerUrl);
        else{
            ArrayList<String> t=new ArrayList<>();
            t.add(trackerUrl);
            this.eventTrackers.put(event, t);
        }

    }

    public boolean isValid(){
        return (!TextUtils.isEmpty(mediaAssetUrl) && !impressions.isEmpty()) || eventTrackers.containsKey(AdEvent.ERROR) ;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getTrackers(AdEvent event) {
        if(event== AdEvent.IMPRESSION)
            return impressions;
        return eventTrackers.get(event);
    }

    public String getClickUrl(){return clickThrough;}

    public String getMediaAssetUrl(){return mediaAssetUrl;}

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(id);
        stream.writeObject(name);
        stream.writeObject(mediaAssetUrl);
        stream.writeObject(clickThrough);
        stream.writeObject(eventTrackers);
        stream.writeObject(impressions);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        id = (String) stream.readObject();
        name = (String) stream.readObject();
        mediaAssetUrl = (String) stream.readObject();
        clickThrough = (String) stream.readObject();
        eventTrackers = (HashMap<AdEvent,ArrayList<String>>) stream.readObject();
        impressions = (ArrayList<String>) stream.readObject();
    }



    @Override
    public boolean equals(Object o) {
        Ad ad = (Ad)o;
        if(ad!=null && id == ad.id && name.equals(ad.name)){
                return true;
        }
        return false;
    }
}
