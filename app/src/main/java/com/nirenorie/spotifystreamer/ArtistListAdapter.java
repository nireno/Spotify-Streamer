package com.nirenorie.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by niren on 7/3/15.
 */
public class ArtistListAdapter extends ArrayAdapter<Artist>{
    private final String CLASS_TAG = this.getClass().getSimpleName();
    public static final int IMAGE_WIDTH = 128;

    public ArtistListAdapter(Context context, List<Artist> artists) {
        super(context, 0, artists);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Artist artist = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
        }

        ImageView artistImageView = (ImageView) convertView.findViewById(R.id.artistImageView);
        if(artist.images.size() > 0) {
            Picasso.with(getContext()).load(artist.images.get(0).url).resize(IMAGE_WIDTH, IMAGE_WIDTH).centerCrop().into(artistImageView);
        } else {
            Picasso.with(getContext()).load(R.drawable.placeholder_128x128).resize(IMAGE_WIDTH, IMAGE_WIDTH).into(artistImageView);
        }

        TextView artistTextView = (TextView) convertView.findViewById(R.id.artistTextView);
        artistTextView.setText(artist.name);

        return convertView;
    }
}
