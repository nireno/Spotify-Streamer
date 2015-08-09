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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {
    private static final String LOG_TAG = "PlayerActivityFragment";
    private final int SEEKBAR_UPDATE_DELAY_MILLIS = 100;
    private final Handler handler = new Handler();
    private boolean isSeeking = false;
    private Runnable seekBarUpdateRunnable;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    public PlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SpotifyTrack t = getActivity().getIntent().getParcelableExtra("track");
        final View view = inflater.inflate(R.layout.fragment_player, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.playerImageView);
        final ImageButton playButton = (ImageButton) view.findViewById(R.id.playerPlayImageButton);

        Helper.setViewText(view, R.id.playerArtistTextView, t.artist);
        Helper.setViewText(view, R.id.playerAlbumTextView, t.album);
        Helper.setViewText(view, R.id.playerTrackTextView, t.name);
        if (t.imageUrl != null) {
            Picasso.with(getActivity()).load(t.imageUrl).into(imageView);
        } else {
            Picasso.with(getActivity()).load(R.drawable.placeholder_128x128).into(imageView);
        }

        seekBar = (SeekBar) view.findViewById(R.id.playerSeekBar);
        seekBarUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying() && !isSeeking) {
                    SeekBar seekBar = (SeekBar) view.findViewById(R.id.playerSeekBar);
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, SEEKBAR_UPDATE_DELAY_MILLIS);
                }
            }
        };

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
                handler.postDelayed(seekBarUpdateRunnable, SEEKBAR_UPDATE_DELAY_MILLIS);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playButton.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    startPlaying();
                }
            }
        });

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                int duration = mediaPlayer.getDuration();
                Helper.setViewText(view, R.id.playerDurationTextView,
                        Helper.readableTrackDuration(duration));
                seekBar.setMax(duration);
                startPlaying();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playButton.setImageResource(android.R.drawable.ic_media_play);
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
        handler.removeCallbacks(seekBarUpdateRunnable);
        mediaPlayer.release();
    }

    private void startPlaying() {
        View view = getView();
        if (view != null) {
            ImageButton btn = (ImageButton) view.findViewById(R.id.playerPlayImageButton);
            btn.setImageResource(android.R.drawable.ic_media_pause);
            mediaPlayer.start();
            handler.postDelayed(seekBarUpdateRunnable, SEEKBAR_UPDATE_DELAY_MILLIS);
        }
    }
}
