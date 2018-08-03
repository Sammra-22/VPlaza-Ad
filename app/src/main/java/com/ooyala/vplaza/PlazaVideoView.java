package com.ooyala.vplaza;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;




/**
 * Created by Sam22 on 15/06/15.
 */
public class PlazaVideoView extends VideoView {


    private int mForceHeight = 0;
    private int mForceWidth = 0;


    public PlazaVideoView(Context context) {
        super(context);
    }

    public PlazaVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlazaVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDimensions(int w, int h) {
        this.mForceHeight = h;
        this.mForceWidth = w;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mForceWidth, mForceHeight);
    }
}