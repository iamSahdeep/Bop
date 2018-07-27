package com.sahdeepsingh.Bop.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sahdeepsingh.Bop.R;

/**
 * TODO: document your custom view class.
 */
public class BottomControlsView extends LinearLayout {
    TextView textViewTitle = findViewById(R.id.bottomtextView);
    View view;

    public BottomControlsView(Context context) {
        super(context);
        init(null, 0);
    }

    public BottomControlsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BottomControlsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        // Set up a default TextPaint object
        view = inflate(getContext(), R.layout.bottomcontrols, null);
        addView(view);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
