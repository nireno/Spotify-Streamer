package com.nirenorie.spotifystreamer;

import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.models.Image;

public class Helper {

    /* Finds image whose width is closest to the preferred width */
    public static Image getOptimalImage(List<Image> images, int preferredWidth) {
        int lowest = Integer.MAX_VALUE;
        Image image = images.get(0);
        for (Image i : images) {
            int distance = Math.abs(i.width - preferredWidth);
            if (distance < lowest) {
                lowest = distance;
                image = i;
            }
        }
        return image;
    }

    public static void setViewText(View view, int textViewId, String text) {
        TextView tv = (TextView) view.findViewById(textViewId);
        tv.setText(text);
    }

    /* Convert millisecond duration to something of the form "m:ss" like: 3:30 */
    public static String readableTrackDuration(int durationMillis) {
        long durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        long durationSeconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis)
                - TimeUnit.MINUTES.toSeconds(durationMinutes);
        return String.format("%d:%02d", durationMinutes, durationSeconds);
    }
}
