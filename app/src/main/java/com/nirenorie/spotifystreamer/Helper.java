package com.nirenorie.spotifystreamer;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

public class Helper {
    public static Image getOptimalImage(List<Image> images, int minWidth) {
        int lowest = images.get(0).width;
        Image image = images.get(0);
        for (Image i : images) {
            if (i.width < minWidth) continue;
            if (i.width - minWidth < lowest) {
                lowest = i.width;
                image = i;
            }
        }
        return image;
    }
}
