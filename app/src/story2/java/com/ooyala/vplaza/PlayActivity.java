package com.ooyala.vplaza;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.ooyala.admodule.api.MidrollPlayer;
import com.ooyala.admodule.api.PrerollPlayer;
import com.ooyala.admodule.listener.PlaybackListener;

import java.lang.String;

/**
 * Created by Sam22 on 6/20/15.
 */
public abstract class PlayActivity extends ActionBarActivity implements PlaybackListener {

    PlazaVideoView mVideoView;
    MidrollPlayer mPlayer;
    String content = "http://downloads.bbc.co.uk/schools/school-report/webmasterclassfinal_fido.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_screen);
        mVideoView = (PlazaVideoView)findViewById(R.id.videoView);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.icon);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        mPlayer = new MidrollPlayer(this, mVideoView, content , this);

    }
}
