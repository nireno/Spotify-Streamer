package com.nirenorie.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {
    private static final String LOG_TAG = "PlayerActivityFragment";
    public PlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SpotifyTrack t = getActivity().getIntent().getParcelableExtra("track");
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        Helper.setViewText(view, R.id.playerArtistTextView, t.artist);
        Helper.setViewText(view, R.id.playerAlbumTextView, t.album);
        Helper.setViewText(view, R.id.playerTrackTextView, t.name);
        long durationMinutes = TimeUnit.MILLISECONDS.toMinutes(t.duration);
        long durationSeconds = TimeUnit.MILLISECONDS.toSeconds(t.duration)
                - TimeUnit.MINUTES.toSeconds(durationMinutes);
        Helper.setViewText(view, R.id.playerDurationTextView,
                String.format("%d:%02d", durationMinutes, durationSeconds));

        ImageView imageView = (ImageView) view.findViewById(R.id.playerImageView);
        if (t.imageUrl != null) {
            Picasso.with(getActivity()).load(t.imageUrl).into(imageView);
        } else {
            Picasso.with(getActivity()).load(R.drawable.placeholder_128x128).into(imageView);
        }
        return view;
    }
}
