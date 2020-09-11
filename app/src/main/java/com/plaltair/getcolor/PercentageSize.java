package com.plaltair.getcolor;

import android.content.res.Resources;
import android.view.View;

/**
 * Created by Pierluca Lippi on 18/03/18.
 */

public class PercentageSize {

    public static int percentageHorizontal(int percentage) {
        final int MAX_WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;

        return (MAX_WIDTH * percentage) / 100;
    }

    public static int percentageHorizontal(View masterView, int percentage) {
        final int MAX_WIDTH = masterView.getLayoutParams().width;

        return (MAX_WIDTH * percentage) / 100;
    }

    public static int percentageVertical(int percentage) {
        final int MAX_HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;

        return (MAX_HEIGHT * percentage) / 100;
    }

    public static int percentageVertical(View masterView, int percentage) {
        final int MAX_HEIGHT = masterView.getLayoutParams().height;

        return (MAX_HEIGHT * percentage) / 100;
    }

}
