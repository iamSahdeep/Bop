package com.sahdeepsingh.Bop.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.sahdeepsingh.Bop.R;

class BottomControlsView extends LinearLayout {
    View view;

    public BottomControlsView(Context context) {
        super(context);
        init();
    }

    public BottomControlsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomControlsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        view = inflate(getContext(), R.layout.bottomcontrols, null);
        addView(view);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
