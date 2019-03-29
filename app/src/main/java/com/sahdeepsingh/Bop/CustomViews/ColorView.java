package com.sahdeepsingh.Bop.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.settings.Theme;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


public class ColorView extends View {

    private Theme theme = new Theme(R.color.primaryColorAmber, R.color.primaryDarkColorAmber, R.color.secondaryColorAmber);
    private Paint primary;
    private Paint accent;
    private Paint border;
    private Paint back, dark;

    private float stroke;

    public ColorView(Context context) {
        super(context);
        init();
    }

    public ColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void addColors(Theme theme) {
        this.theme = theme;
        init();
        invalidate();
    }

    private void init() {
        try {
            border = new Paint();
            border.setStyle(Paint.Style.STROKE);
            if (this.isSelected()) {
                border.setColor(Color.BLUE);
            } else {
                border.setColor(Color.GRAY);
            }

            back = new Paint();
            back.setStyle(Paint.Style.FILL);
            int color = android.R.color.background_light;
            TypedValue a = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
            if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT)
                color = a.data;    // windowBackground is a color
            back.setColor(color);

            primary = new Paint();
            primary.setStyle(Paint.Style.FILL);
            primary.setColor(ContextCompat.getColor(getContext(), theme.getPrimaryColor()));

            dark = new Paint();
            dark.setStyle(Paint.Style.FILL);
            dark.setColor(ContextCompat.getColor(getContext(), theme.getPrimaryDarkColor()));

            accent = new Paint();
            accent.setStyle(Paint.Style.FILL);
            accent.setColor(ContextCompat.getColor(getContext(), theme.getAccentColor()));
            accent.setAntiAlias(true);
            accent.setDither(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final float height = getHeight();
        final float width = getWidth();
        stroke = height * 8 / 100f;
        final float statusbar = height * 16 / 100f;
        final float toolbar = height * 72 / 100f;

        if (this.isActivated()) {
            border.setColor(ContextCompat.getColor(getContext(), R.color.md_blue_900));
        } else {
            border.setColor(ContextCompat.getColor(getContext(), R.color.md_blue_grey_400));
        }
        border.setStrokeWidth(stroke);
        canvas.drawRect(0, 0, width, height, back);
        canvas.drawRect(0, 0, width, statusbar, primary);
        canvas.drawRect(0, statusbar, width, toolbar, dark);
        canvas.drawCircle(width - stroke - height * 20 / 100f, toolbar, height * 16 / 100, accent);
        canvas.drawRect(0, 0, width, height, border);
    }
}