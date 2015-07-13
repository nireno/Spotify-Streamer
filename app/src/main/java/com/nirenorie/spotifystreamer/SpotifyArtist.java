package com.nirenorie.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

public class SpotifyArtist implements Parcelable {
    public static final Parcelable.Creator<SpotifyArtist> CREATOR
            = new Parcelable.Creator<SpotifyArtist>() {
        public SpotifyArtist createFromParcel(Parcel in) {
            return new SpotifyArtist(in);
        }

        public SpotifyArtist[] newArray(int size) {
            return new SpotifyArtist[size];
        }
    };

    public String id;
    public String name;
    public String imageUrl;

    public SpotifyArtist(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    private SpotifyArtist(Parcel in) {
        name = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(imageUrl);
    }
}
