package com.ooyala.admodule.api;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.widget.VideoView;
import com.ooyala.admodule.listener.PlaybackListener;

/**
 * Created by Sam22 on 6/21/15.
 */
public class PlayerTest extends InstrumentationTestCase implements PlaybackListener{

    VideoView mVideo;
    Context context;
    String vidPath = "http://downloads.bbc.co.uk/schools/school-report/webmasterclassfinal_fido.mp4";
    AdPlayer mPlayer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = getInstrumentation().getTargetContext();
        mVideo = new VideoView(context);
    }


    public void testPreroll(){
        mPlayer = new PrerollPlayer(context, mVideo, vidPath, this);
        try {
            synchronized(Thread.currentThread()) {
                Thread.currentThread().wait(8000);
            }
            mPlayer.pause();
            assertFalse("Player wrong state: should not be playing",mPlayer.isPlaying());

            synchronized(Thread.currentThread()) {
                Thread.currentThread().wait(5000);
            }
            mPlayer.play();
            synchronized(Thread.currentThread()) {
                Thread.currentThread().wait(10000);
            }

            mPlayer.stop();
            assertFalse("Player wrong state: should not be playing", mPlayer.isPlaying());
            assertEquals("Player wrong phase",PrerollPlayer.State.INITIAL, mPlayer.getState());
        }catch(InterruptedException e){
            fail("Thread interrupted");
        }
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    @Override
    public void onPlaybackReady() {
        assertFalse("Player wrong state: should not be playing",mPlayer.isPlaying());
        assertEquals("Player wrong phase", PrerollPlayer.State.INITIAL, mPlayer.getState());
        mPlayer.play();
        assertEquals("Player wrong phase", PrerollPlayer.State.AD_PLAYBACK, mPlayer.getState());
    }

    @Override
    public void onPlaybackLoadingStart() {

    }

    @Override
    public void onPlaybackLoadingEnd() {

    }

    @Override
    public void onPlaybackPaused() {
        assertFalse("Player wrong state: should not be playing",mPlayer.isPlaying());
    }

    @Override
    public void onPlaybackResumed() {
        assertTrue("Player wrong state: should be playing",mPlayer.isPlaying());
    }

    @Override
    public void onTouchDisplay() {
        assertNotSame("Callback delivered for the wrong phase", PrerollPlayer.State.AD_PLAYBACK, mPlayer.getState());
    }

    @Override
    public void onCompleted() {
        assertFalse("Player wrong state: should not be playing",mPlayer.isPlaying());
    }
}
