package com.ooyala.admodule.api;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

import com.ooyala.admodule.listener.PlaybackListener;
import com.ooyala.admodule.model.AdEvent;

/**
 * Display Preroll
 * Content playback preceeded by an Ad
 * Created by Sam22 on 6/17/15.
 */
public class PrerollPlayer extends AdPlayer {
    private static final String TAG = PrerollPlayer.class.getName();

    enum State{INITIAL, AD_PLAYBACK, CONTENT_PLAYBACK}

    private int mResumePosition = 0;
    private String mContentPath = null;
    private Uri mContentUri = null;
    private Context context;

    private VideoView mVideoView;
    private State mState;

    /**
     * Player constructor -- Initialize the Preroll player
     * @param context The Application context
     * @param videoView The Video view intended to display the playback
     * @param contentPath The path to the video content
     * @param listener Callback for playback events
     */
    public PrerollPlayer(@NonNull Context context, @NonNull VideoView videoView, @NonNull String contentPath, PlaybackListener listener){
        super(context, listener);
        this.context = context;
        mVideoView = videoView;
        mContentPath = contentPath;
        mState = State.INITIAL;
    }

    /**
     * Player constructor -- Initialize the Preroll player
     * @param context The Application context
     * @param videoView The Video view intended to display the playback
     * @param contentUri The uri to the video content
     * @param listener Callback for playback events
     */
    public PrerollPlayer(@NonNull Context context, @NonNull VideoView videoView, @NonNull Uri contentUri, PlaybackListener listener){
        super(context, listener);
        this.context = context;
        mVideoView = videoView;
        mContentUri = contentUri;
        mState = State.INITIAL;
    }

    @Override
    public boolean isPlaying(){ return mVideoView.isPlaying();}

    @Override
    public void play(){
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnTouchListener(this);
        mVideoView.setOnCompletionListener(this);
        if(mState == State.INITIAL){
            mState = State.AD_PLAYBACK;
            mPlayListener.onPlaybackLoadingStart();
            if(mAdManager.getAd()!=null)
                mVideoView.setVideoPath(mAdManager.getAd().getMediaAssetUrl());
            else{
                mState = State.CONTENT_PLAYBACK;
                if (mContentPath != null)
                    mVideoView.setVideoPath(mContentPath);
                if (mContentUri != null)
                    mVideoView.setVideoURI(mContentUri);
            }
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
        }else
            mVideoView.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mResumePosition = -1;
        if(mState == State.AD_PLAYBACK) {
            mState = State.CONTENT_PLAYBACK;
            dispatchAdEvent(AdEvent.IMPRESSION, 1000);
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
            case CONTENT_PLAYBACK:
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

    @Override
    State getState(){return mState;}



}
