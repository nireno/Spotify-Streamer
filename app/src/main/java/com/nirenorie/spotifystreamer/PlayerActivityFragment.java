package com.nirenorie.spotifystreamer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {
    private static final String LOG_TAG = "PlayerActivityFragment";
    private MediaPlayer mediaPlayer;
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


        ImageView imageView = (ImageView) view.findViewById(R.id.playerImageView);
        if (t.imageUrl != null) {
            Picasso.with(getActivity()).load(t.imageUrl).into(imageView);
        } else {
            Picasso.with(getActivity()).load(R.drawable.placeholder_128x128).into(imageView);
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Helper.setViewText(getView(), R.id.playerDurationTextView,
                        Helper.readableTrackDuration(mediaPlayer.getDuration()));
                mediaPlayer.start();
            }
        });
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(t.previewUrl);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.release();
    }
}
