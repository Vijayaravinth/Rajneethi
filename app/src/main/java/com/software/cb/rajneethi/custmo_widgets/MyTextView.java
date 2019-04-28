package com.software.cb.rajneethi.custmo_widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class MyTextView extends android.support.v7.widget.AppCompatTextView {

public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        }

public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        }

public MyTextView(Context context) {
        super(context);
        init();
        }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    private void init() {
        if (!isInEditMode()) {

        }
        }
}
