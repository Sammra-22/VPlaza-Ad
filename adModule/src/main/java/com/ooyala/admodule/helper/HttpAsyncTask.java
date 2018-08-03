package com.ooyala.admodule.helper;

import android.os.AsyncTask;
import android.util.Log;


import com.ooyala.admodule.model.Ad;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Sam22 on 06/20/15.
 */
public abstract class HttpAsyncTask extends AsyncTask<String, Void, Ad> {
    protected enum Task{GET_AD, FIRE_PIXEL}
    Task mTask;

    protected void setTask(Task task){mTask = task;}


    @Override
    protected Ad doInBackground(String... url){

        URL urlObj;
        try {
            urlObj = new URL(url[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        Log.i("HTTP", "Request sent [GET] --> URL: " + url[0]);
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) urlObj.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            if(mTask==Task.GET_AD) {
                AdXmlHandler mXmlHandler = new AdXmlHandler();
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                parser.parse(in, mXmlHandler);
                Log.i("HTTP", "Response to [GET] " + url[0]);
                Log.i("HTTP", "Success");
                Ad ad = mXmlHandler.getParsedAd();
                return ad;
            }

        } catch(SAXException saxException) {
            saxException.printStackTrace();
        } catch(ParserConfigurationException parserException){
            parserException.printStackTrace();
        }catch(IOException ioException) {
            ioException.printStackTrace();
        }finally {
            urlConnection.disconnect();
        }

        return null;

    }




}
