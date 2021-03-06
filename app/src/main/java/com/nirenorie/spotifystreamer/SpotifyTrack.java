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
    public String artist;
    public String previewUrl;

    public SpotifyTrack(String name, String album, String imageUrl, String artist, String previewUrl) {
        this.name = name;
        this.album = album;
        this.imageUrl = imageUrl;
        this.artist = artist;
        this.previewUrl = previewUrl;
    }

    private SpotifyTrack(Parcel in) {
        name = in.readString();
        album = in.readString();
        imageUrl = in.readString();
        artist = in.readString();
        previewUrl = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(album);
        out.writeString(imageUrl);
        out.writeString(artist);
        out.writeString(previewUrl);
    }
}
