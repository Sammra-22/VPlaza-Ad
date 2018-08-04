package com.ooyala.vplaza;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * The activity renders and plays a specified content video along with an Ad according to the selected variant of the player.
 * It supports user interaction and provides basic playback information.
 * <p>
 * Created by Sam22 on 15/06/15.
 */
public class VideoContentActivity extends PlayActivity {

    static CountDownTimer mCounter;
    ProgressBar mProgress;
    TextView mProgressText, mIndexText, mDurationText;
    Handler mHandler;
    ProgressBar spinner;
    ImageButton playBtn;
    boolean isPlayReady;
    BroadcastReceiver mEventReceiver = new PlaybackEventReceiver();

    /**
     * Video progress controller-- updates the view with the playback progress
     **/
    private Runnable mVideoProgress = new Runnable() {
        @Override
        public void run() {
            int duration = mPlayer.getDuration();
            int current = mPlayer.getPosition();
            mDurationText.setText(playbackTimeFormat(duration));
            mProgressText.setText(playbackTimeFormat(current));
            mProgress.setProgress(100 * current / duration);
            mHandler.postDelayed(mVideoProgress, 500);
        }
    };

    /**
     * Format the playback time into a user friendly display
     **/
    private static String playbackTimeFormat(long timeMillis) {
        long timeSec = (timeMillis / 1000) % (3600 * 24);
        long hours = timeSec / 3600;
        long minutes = (timeSec % 3600) / 60;
        long seconds = timeSec - (3600 * hours + 60 * minutes);
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append(":");
        }
        if (minutes > 0) {
            sb.append(minutes).append(":");
        }
        if (hours == 0 && minutes == 0) {
            sb.append("0:");
        }
        if (seconds >= 10) {
            sb.append(seconds);
        } else if (seconds > 0) {
            sb.append("0").append(seconds);
        } else {
            sb.append("00");
        }
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        updateControllerView();
        isPlayReady = false;
        mHandler = new Handler();
        spinner = (ProgressBar) findViewById(R.id.progress);
        playBtn = (ImageButton) findViewById(R.id.play);
        mIndexText = (TextView) findViewById(R.id.videoIndex);
        adjustVideoDisplay(getResources().getConfiguration());
    }

    public void onPlayVideo(View v) {
        playBtn.setVisibility(View.GONE);
        mVideoView.setBackgroundResource(0);
        showControllerView();
        if (isPlayReady) {
            spinner.setVisibility(View.GONE);
            mPlayer.play();
        } else {
            spinner.setVisibility(View.VISIBLE);
            isPlayReady = true;
        }
    }

    /*******************************
     *  Playback callbacks
     *  Define the playback rendering behavior
     *******************************/

    @Override
    public void onPlaybackReady() {
        if (isPlayReady) {
            onPlayVideo(null);
        } else {
            isPlayReady = true;
        }
    }

    @Override
    public void onPlaybackLoadingStart() {
        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlaybackLoadingEnd() {
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void onPlaybackPaused() {
        playBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlaybackResumed() {
        playBtn.setVisibility(View.GONE);
    }

    @Override
    public void onTouchDisplay() {
        if (mPlayer.isPlaying()) {
            playBtn.setVisibility(View.VISIBLE);
            mPlayer.pause();
        } else {
            playBtn.setVisibility(View.GONE);
            mPlayer.play();
        }
    }

    @Override
    public void onCompleted() {
        playBtn.setVisibility(View.VISIBLE);
    }

    /****************************
     * Activity callbacks
     ***************************/

    @Override
    protected void onStart() {
        super.onStart();
        registerEventReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(mVideoProgress);
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mVideoProgress);
        mPlayer.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mCounter != null) {
            mCounter.cancel();
        }
        unregisterEventReceiver();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adjustVideoDisplay(newConfig);
        updateControllerView();
    }

    /*********************************
     * Utility functions
     *********************************/

    protected void updateControllerView() {
        /* Adapt the progress view to the screen orientation **/
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getSupportActionBar().show();
            mProgress = (ProgressBar) findViewById(R.id.progress_video_port);
            mProgressText = (TextView) findViewById(R.id.progress_text_port);
            mDurationText = (TextView) findViewById(R.id.duration_text_port);
        } else {
            getSupportActionBar().hide();
            mProgress = (ProgressBar) findViewById(R.id.progress_video_land);
            mProgressText = (TextView) findViewById(R.id.progress_text_land);
            mDurationText = (TextView) findViewById(R.id.duration_text_land);
        }
        if (mVideoView.getCurrentPosition() > 0) {
            showControllerView();
        }
    }

    private void showControllerView() {
        /*  Adjust layout visibility depending on the screen orientation**/
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            findViewById(R.id.skipLayoutLand).setVisibility(View.GONE);
            findViewById(R.id.skipLayoutPort).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.skipLayoutPort).setVisibility(View.GONE);
            findViewById(R.id.skipLayoutLand).setVisibility(View.VISIBLE);
        }
    }

    private void adjustVideoDisplay(Configuration newConfig) {
        /* Adapt the video display to the screen orientation **/
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mVideoView.setDimensions(metrics.widthPixels, metrics.heightPixels);
            mVideoView.getHolder().setFixedSize(metrics.widthPixels, metrics.heightPixels);
            findViewById(R.id.ad_frame).setPadding(0, 0, 0, 0);
            findViewById(R.id.container).setPadding(0, 0, 0, 0);
        } else {
            mVideoView.setDimensions(metrics.widthPixels, 3 * metrics.widthPixels / 5);//metrics.heightPixels/3
            mVideoView.getHolder().setFixedSize(metrics.widthPixels, 3 * metrics.widthPixels / 5);
            findViewById(R.id.ad_frame).setPadding(10, 10, 10, 10);
            findViewById(R.id.container).setPadding(0, 20, 0, 0);
        }
    }

    private void registerEventReceiver() {
        IntentFilter adFilter = new IntentFilter();
        adFilter.addAction(Intent.ACTION_SCREEN_OFF);
        adFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        this.registerReceiver(mEventReceiver, adFilter);
    }

    private void unregisterEventReceiver() {
        this.unregisterReceiver(mEventReceiver);
    }

    class PlaybackEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mPlayer.isPlaying()) {
                if (intent.getAction() != null
                        && intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    mPlayer.pause();
                } else if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
                        && intent.getExtras() != null) {
                    String state = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                    if (state != null && !state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        mPlayer.pause();
                    }
                }
            }
        }
    }

}
