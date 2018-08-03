package com.ooyala.admodule.api;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

import com.ooyala.admodule.listener.PlaybackListener;
import com.ooyala.admodule.model.AdEvent;

/**
 * Display Midroll
 * Content playback interrupted by an Ad
 * Created by Sam22 on 6/17/15.
 */
public class MidrollPlayer extends AdPlayer {
    private static final String TAG = MidrollPlayer.class.getName();

    enum State{INITIAL, CONTENT_PLAYBACK_1, AD_PLAYBACK, CONTENT_PLAYBACK_2}
    private boolean firstQuartileSent, midpointSent, thirdQuartileSent;
    private int mResumePosition = 0;
    private String mContentPath = null;
    private Uri mContentUri = null;
    private Context context;


    private VideoView mVideoView;
    private State mState;

    /**
     * Player constructor -- Initialize the Midroll player
     * @param context The Application context
     * @param videoView The Video view intended to display the playback
     * @param contentPath The path to the video content
     * @param listener Callback for playback events
     */
    public MidrollPlayer(@NonNull Context context, @NonNull VideoView videoView, @NonNull String contentPath, PlaybackListener listener){
        super(context, listener);
        this.context = context;
        mVideoView = videoView;
        mContentPath = contentPath;
        mState = State.INITIAL;
    }

    /**
     * Player constructor -- Initialize the Midroll player
     * @param context The Application context
     * @param videoView The Video view intended to display the playback
     * @param contentUri The uri to the video content
     * @param listener Callback for playback events
     */
    public MidrollPlayer(@NonNull Context context, @NonNull VideoView videoView, @NonNull Uri contentUri, PlaybackListener listener){
        super(context, listener);
        this.context = context;
        mVideoView = videoView;
        mState = State.INITIAL;
    }

    @Override
    public boolean isPlaying(){
        return mVideoView.isPlaying();
    }

    @Override
    public void play(){
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnTouchListener(this);
        mVideoView.setOnCompletionListener(this);
        if(mState == State.INITIAL){
            mState = State.CONTENT_PLAYBACK_1;
            mPlayListener.onPlaybackLoadingStart();
            if (mContentPath != null)
                mVideoView.setVideoPath(mContentPath);
            if (mContentUri != null)
                mVideoView.setVideoURI(mContentUri);
            mHandler.post(mVideoProgress);
        }else{
            mVideoView.seekTo(mResumePosition);
            mVideoView.start();
        }
    }

    @Override
    public void pause(){
        mResumePosition = mVideoView.getCurrentPosition();
        mVideoView.pause();
        mPlayListener.onPlaybackPaused();
    }


    @Override
    public void stop(){
        mState = State.INITIAL;
        mVideoView.stopPlayback();
        mHandler.removeCallbacks(mVideoProgress);
    }

    @Override
    public int getDuration() {
        return mVideoView.getDuration();
    }

    @Override
    public int getPosition() {
        return mVideoView.getCurrentPosition();
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mPlayListener.onPlaybackLoadingEnd();
        if(mResumePosition>0) {
            mVideoView.seekTo(mResumePosition);
            return;
        }
        switch (mState){
            case AD_PLAYBACK:
                dispatchAdEvent(AdEvent.START, 0);
                break;
            case CONTENT_PLAYBACK_2:
                if(mResumePosition<0)
                    mVideoView.seekTo(5000);
                else
                    mState = State.CONTENT_PLAYBACK_1;
                break;
            default:
                break;
        }
        mVideoView.start();

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mResumePosition = -1;
        if(mState == State.AD_PLAYBACK) {
            dispatchAdEvent(AdEvent.COMPLETE, 0);
            dispatchAdEvent(AdEvent.IMPRESSION, 1000);
            mState = State.CONTENT_PLAYBACK_2;
            mPlayListener.onPlaybackLoadingStart();
            if (mContentPath != null)
                mVideoView.setVideoPath(mContentPath);
            if (mContentUri != null)
                mVideoView.setVideoURI(mContentUri);
        }else {
            stop();
            mPlayListener.onCompleted();
        }
    }



    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (mState){
            case CONTENT_PLAYBACK_1:
                mPlayListener.onTouchDisplay();
                break;
            case CONTENT_PLAYBACK_2:
                mPlayListener.onTouchDisplay();
                break;
            case AD_PLAYBACK:
                if (mVideoView.isPlaying()) {
                    pause();
                    redirect();
                } else {
                    mVideoView.seekTo(mResumePosition);
                    mVideoView.start();
                    mPlayListener.onPlaybackResumed();
                }
                break;
            default:
                break;
        }
        return false;
    }

    private void redirect(){
        Uri uri = Uri.parse(mAdManager.getAd().getClickUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private Runnable mVideoProgress= new Runnable(){
        @Override
        public void run() {
            int duration= mVideoView.getDuration();
            int current= mVideoView.getCurrentPosition();

            switch (mState){
                case CONTENT_PLAYBACK_1:
                    if(current/1000>=5 && mAdManager.getAd()!=null){
                        firstQuartileSent= false;
                        midpointSent= false;
                        thirdQuartileSent= false;
                        mResumePosition = -1;
                        mVideoView.stopPlayback();
                        mPlayListener.onPlaybackLoadingStart();
                        mState = State.AD_PLAYBACK;
                        mVideoView.setVideoPath(mAdManager.getAd().getMediaAssetUrl());
                    }
                    break;
                case AD_PLAYBACK:
                    if(current>=duration/4 && !firstQuartileSent) {
                        firstQuartileSent = true;
                        dispatchAdEvent(AdEvent.FIRSTQUARTILE, 0);
                    } else if(current>=duration/2 && !midpointSent) {
                        midpointSent = true;
                        dispatchAdEvent(AdEvent.MIDPOINT, 0);
                    } else if(current>=3*duration/4 && !thirdQuartileSent) {
                        thirdQuartileSent = true;
                        dispatchAdEvent(AdEvent.THIRDQUARTILE, 0);
                    }
                    break;
            }

//            if(current>0)
//                mResumePosition = current;
            mHandler.postDelayed(mVideoProgress,500);
        }
    };

    @Override
    State getState(){return mState;}


}
