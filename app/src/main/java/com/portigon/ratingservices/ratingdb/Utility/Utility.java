package com.portigon.ratingservices.ratingdb.Utility;

import android.content.Context;

/**
 * Created by Moritz on 25.08.2015.
 * Utility function collection
 */
public class Utility {
    static public int GetPixelFromDips(Context context, float pixels) {

        // Get the screen's density scale

        final float scale = context.getResources().getDisplayMetrics().density;

        // Convert the dps to pixels, based on density scale

        return (int) (pixels * scale + 0.5f);

    }
}
