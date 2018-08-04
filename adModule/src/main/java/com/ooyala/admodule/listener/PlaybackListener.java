package com.ooyala.admodule.listener;

/**
 * Created by Sam22 on 6/16/15.
 */
public interface PlaybackListener {
    /**
     * All is ready for playback -- including Ad resources
     */
    void onPlaybackReady();

    /**
     * The video started loading -- display a progress indicator
     */
    void onPlaybackLoadingStart();

    /**
     * The video finished loading -- hide any progress indicator
     */
    void onPlaybackLoadingEnd();

    /**
     * The video is paused -- display a paused state indicator
     */
    void onPlaybackPaused();

    /**
     * The video is resumed -- hide any pause state indicator
     */
    void onPlaybackResumed();

    /**
     * Action listener for the video display touch
     */
    void onTouchDisplay();

    /**
     * The video has completed the playback
     */
    void onCompleted();
}
