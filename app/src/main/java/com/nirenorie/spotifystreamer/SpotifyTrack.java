package com.nirenorie.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

public class SpotifyTrack implements Parcelable {
    public static final Parcelable.Creator<SpotifyTrack> CREATOR
            = new Parcelable.Creator<SpotifyTrack>() {
        public SpotifyTrack createFromParcel(Parcel in) {
            return new SpotifyTrack(in);
        }

        public SpotifyTrack[] newArray(int size) {
            return new SpotifyTrack[size];
        }
    };
    public String name;
    public String album;
    public String imageUrl;

    public SpotifyTrack(String name, String album, String imageUrl) {
        this.name = name;
        this.album = album;
        this.imageUrl = imageUrl;
    }

    private SpotifyTrack(Parcel in) {
        name = in.readString();
        album = in.readString();
        imageUrl = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(album);
        out.writeString(imageUrl);
    }
}
