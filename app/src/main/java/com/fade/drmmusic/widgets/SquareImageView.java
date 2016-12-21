package com.fade.drmmusic.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by SnailSet on 2016/12/10.
 */

public class SquareImageView extends ImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int lenth = Math.min(width, height);

        setMeasuredDimension(lenth, lenth);
    }
}
