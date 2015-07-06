package com.nirenorie.spotifystreamer;

import java.util.List;

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
}
