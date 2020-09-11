package com.plaltair.getcolor;

import android.content.res.Resources;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by Pierluca Lippi on 13/03/18.
 */

public class ScaledFont {

    private final int MAX_WIDTH;

    private int fontSize;

    public ScaledFont(int fontSize) {
        MAX_WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
        this.fontSize = fontSize;
    }

    public void fixTextSize(TextView textView) {

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (MAX_WIDTH/fontSize));
    }

    public static void fixTextSize(TextView textView, int fontSize) {
        final int MAX_WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (MAX_WIDTH/fontSize));
    }

}
