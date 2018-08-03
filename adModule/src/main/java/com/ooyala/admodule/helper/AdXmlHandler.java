package com.ooyala.admodule.helper;

import android.text.TextUtils;
import com.ooyala.admodule.model.Ad;
import com.ooyala.admodule.model.AdEvent;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Sam22 on 1/2/15.
 */
public class AdXmlHandler extends DefaultHandler {
    /** XML EVENT PROCESSING METHODS (DEFINED BY DefaultHandler) **/
    private class Field{
        String value="";
        String attr;
    }

    private Field currentField;
    Ad vastAd;

    public AdXmlHandler(){
        vastAd = new Ad();
    }

    public Ad getParsedAd(){
        return vastAd;
    }


    // startElement is the opening part of the tag
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("Ad")) {
            currentField = null;
            String id = attributes.getValue("id");
            if (id != null) {
                vastAd.setId(id);
            }
        }else if(qName.equals("VASTAdTagURI")){
            currentField = new Field();
        }else if(qName.equals("AdTitle")){
            currentField = new Field();
        }else if (qName.equals("Tracking")) {
            currentField = new Field();
            currentField.attr = attributes.getValue("event");
        }else if (qName.equals("MediaFile")) {
            currentField = new Field();
            currentField.attr = attributes.getValue("type");
        }else if (qName.equals("Impression") || qName.equals("ClickThrough") || qName.equals("ClickTracking")) {
            currentField = new Field();
            currentField.attr = attributes.getValue("id");
        }else if(qName.equals("Error")){
            currentField = new Field();
        }else if (qName.equals("TimeToLiveMinutes")) {
            currentField = new Field();
        }else{
            currentField = null;
        }

    }

    // endElement is the closing part of the tag
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (currentField != null && !TextUtils.isEmpty(currentField.value) && !currentField.value.equals("<![CDATA[]]>")) {
            if (qName.equals("Impression") ) {
                vastAd.setImpressionUrl(currentField.value);
            }else if(qName.equals("AdTitle")){
                vastAd.setName(currentField.value);
            }else if (qName.equals("ClickTracking")) {
                vastAd.setTracker(currentField.value, AdEvent.CLICK);
            } else if (qName.equals("ClickThrough")) {
                vastAd.setClickUrl(currentField.value);
            } else if (qName.equals("MediaFile") && currentField.attr.equals("video/mp4")) {
                vastAd.setMediaUrl(currentField.value);
            } else if (qName.equals("Tracking")) {
                vastAd.setTracker(currentField.value, AdEvent.fromVast(currentField.attr));
            }  else if (qName.equals("Error")) {
                vastAd.setTracker(currentField.value, AdEvent.ERROR);
            }
        }
    }

    // "characters" are the text in between tags
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentField != null) {
            currentField.value += new String(ch, start, length).trim();
        }
    }
}
