package com.nirenorie.spotifystreamer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {
    private static final String LOG_TAG = "PlayerActivityFragment";
    private final int SEEKBAR_UPDATE_DELAY_MILLIS = 100;
    private boolean isSeeking = false;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
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

        seekBar = (SeekBar) view.findViewById(R.id.playerSeekBar);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                int duration = mediaPlayer.getDuration();
                Helper.setViewText(getView(), R.id.playerDurationTextView,
                        Helper.readableTrackDuration(duration));
                seekBar.setMax(duration);
                mediaPlayer.start();
            }
        });
        final Handler h = new Handler();
        final Runnable seekBarUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                View view = getView();
                if (view != null && !isSeeking) {
                    SeekBar seekBar = (SeekBar) view.findViewById(R.id.playerSeekBar);
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    h.postDelayed(this, SEEKBAR_UPDATE_DELAY_MILLIS);
                }
            }
        };
        h.postDelayed(seekBarUpdateRunnable, SEEKBAR_UPDATE_DELAY_MILLIS);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                mediaPlayer.seekTo(seekBar.getProgress());
                h.postDelayed(seekBarUpdateRunnable, SEEKBAR_UPDATE_DELAY_MILLIS);
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
