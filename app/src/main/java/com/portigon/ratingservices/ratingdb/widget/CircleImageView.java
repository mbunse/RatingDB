package com.portigon.ratingservices.ratingdb.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.portigon.ratingservices.ratingdb.widget.outlineprovider.RoundOutlineProvider;

/**
 * Created by Moritz on 24.08.2015.
 * Circle-shaped ImageView
 */
public class CircleImageView extends ImageView {

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipToOutline(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            setOutlineProvider(new RoundOutlineProvider(Math.min(w, h)));
        }
    }


}
