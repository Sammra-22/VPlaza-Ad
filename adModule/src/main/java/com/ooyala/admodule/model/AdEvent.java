package com.ooyala.admodule.model;

/**
 * Created by Sam22 on 9/20/15.
 */
public enum AdEvent {
    IMPRESSION,
    START,
    FIRSTQUARTILE,
    MIDPOINT,
    THIRDQUARTILE,
    COMPLETE,
    CLICK,
    REQUEST,
    ERROR,
    FULLSCREEN,
    UNKNOWN;


    public static AdEvent fromVast(String name) {
        if ("impression".equals(name)) return IMPRESSION;
        if ("click".equals(name)) return CLICK;
        if ("start".equals(name)) return START;
        if ("firstQuartile".equals(name)) return FIRSTQUARTILE;
        if ("midpoint".equals(name)) return MIDPOINT;
        if ("thirdQuartile".equals(name)) return THIRDQUARTILE;
        if ("complete".equals(name)) return COMPLETE;
        if ("fullscreen".equals(name)) return FULLSCREEN;
        if ("request".equals(name)) return REQUEST;
        if ("error".equals(name)) return ERROR;
        return UNKNOWN;
    }

    public String getDisplayName() {
        switch (this) {
            case IMPRESSION:
                return "Impression";
            case CLICK:
                return "Click";
            case START:
                return "Start";
            case FIRSTQUARTILE:
                return "First quartile";
            case MIDPOINT:
                return "Midpoint";
            case THIRDQUARTILE:
                return "Third quartile";
            case COMPLETE:
                return "Complete";
            case REQUEST:
                return "Request";
            case ERROR:
                return "Error";
            case FULLSCREEN:
                return "Fullscreen";
            default:
                return "";
        }
    }


}
