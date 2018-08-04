package com.ooyala.admodule.api;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.ooyala.admodule.controller.AdManager;
import com.ooyala.admodule.helper.DefaultPlaybackListener;
import com.ooyala.admodule.listener.PlaybackListener;
import com.ooyala.admodule.model.AdEvent;

/**
 * Created by Sam22 on 6/20/15.
 */
public abstract class AdPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, View.OnTouchListener {

    AdManager mAdManager;
    PlaybackListener mPlayListener;
    Handler mHandler;

    AdPlayer(Context context, PlaybackListener listener) {
        if (listener != null) {
            mPlayListener = listener;
        } else {
            mPlayListener = new DefaultPlaybackListener();
        }
        mAdManager = new AdManager(context);
        mHandler = new Handler(Looper.getMainLooper());
        mAdManager.fetchAd(mPlayListener);
    }

    /**
     * Get the state of the playback.
     *
     * @return Whether the video is playing
     */
    abstract public boolean isPlaying();

    /**
     * Start the playback.
     * <p>
     * The playback will be resumed if this function has been called earlier and the playback was interrupted.
     * For a new fresh playback it is recommended to call stop() and then start() to play from the beginning.
     */
    abstract public void play();

    /**
     * Pause the playback.
     */
    abstract public void pause();

    /**
     * Stop the playback.
     */
    abstract public void stop();

    /**
     * Get playback total duration.
     */
    abstract public int getDuration();

    /**
     * Get playback current position.
     */
    abstract public int getPosition();


    void dispatchAdEvent(final AdEvent event, int delay) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdManager.fireTracker(event);
            }
        }, delay);
    }

    abstract Object getState();
}
