package com.nirenorie.spotifystreamer;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.nirenorie.spotifystreamer.data.DataContract.TrackEntry;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {
    private static final String LOG_TAG = "PlayerActivityFragment";
    private static final String[] TRACK_COLUMNS = {
            TrackEntry.COLUMN_ARTIST_NAME,
            TrackEntry.COLUMN_ALBUM_NAME,
            TrackEntry.COLUMN_TRACK_NAME,
            TrackEntry.COLUMN_ALBUM_IMAGE_URL,
            TrackEntry.COLUMN_PREVIEW_URL,
    };
    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_ARTIST_NAME = 0;
    private static final int COL_ALBUM_NAME = 1;
    private static final int COL_TRACK_NAME = 2;
    private static final int COL_ALBUM_IMAGE_URL = 3;
    private static final int COL_PREVIEW_URL = 4;
    private final int SEEKBAR_UPDATE_DELAY_MILLIS = 100;
    private final Handler handler = new Handler();
    private final String SAVE_STATE_TRACK_INDEX = "saveStateTrackIndex";
    private final String SAVE_STATE_ELAPSED = "saveStateElapsed";
    private boolean isSeeking = false;
    private Runnable seekBarUpdateRunnable;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private String artistId;
    private int trackIndex;
    private View playerFragmentView;
    private ImageButton playButton;
    private int mediaPlayerCurrentPosition = 0;

    public PlayerActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_STATE_TRACK_INDEX, trackIndex);
        outState.putInt(SAVE_STATE_ELAPSED, mediaPlayerCurrentPosition);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getActivity().getIntent();
        artistId = i.getStringExtra(TracksActivityFragment.EXTRA_ARTIST_ID);
        boolean hasSavedState = savedInstanceState != null;
        if (hasSavedState && savedInstanceState.containsKey(SAVE_STATE_TRACK_INDEX)) {
            trackIndex = savedInstanceState.getInt(SAVE_STATE_TRACK_INDEX);
        } else {
            trackIndex = i.getIntExtra(TracksActivityFragment.EXTRA_POSITION, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String whereClause = TrackEntry.COLUMN_ARTIST_ID + "= ?";
        final Cursor c = getActivity().getContentResolver().
                query(TrackEntry.CONTENT_URI, TRACK_COLUMNS, whereClause, new String[]{artistId}, null);
        c.moveToPosition(trackIndex);
        playerFragmentView = inflater.inflate(R.layout.fragment_player, container, false);
        playButton = (ImageButton) playerFragmentView.findViewById(R.id.playerPlayImageButton);

        seekBar = (SeekBar) playerFragmentView.findViewById(R.id.playerSeekBar);
        seekBarUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying() && !isSeeking) {
                    SeekBar seekBar = (SeekBar) playerFragmentView.findViewById(R.id.playerSeekBar);
                    int elapsedTime = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(elapsedTime);
                    Helper.setViewText(playerFragmentView, R.id.playerElapsedTextView,
                            Helper.formatTimeForPlayer(elapsedTime));
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
                mediaPlayer.seekTo(seekBar.getProgress());
                isSeeking = false;
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

        loadTrackDetails(playerFragmentView, c);
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_STATE_ELAPSED)) {
            initializeMediaPlayer(savedInstanceState.getInt(SAVE_STATE_ELAPSED));
        } else {
            initializeMediaPlayer(0);
        }
        prepareMediaPlayer(c);

        ImageButton nextTrackButton = (ImageButton) playerFragmentView.findViewById(R.id.playerNextImageButton);
        nextTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c.moveToPosition(c.getPosition() + 1);
                if (c.isAfterLast()) {
                    c.moveToFirst();
                }
                trackIndex = c.getPosition();
                loadTrackDetails(playerFragmentView, c);
                resetPlayer();
                initializeMediaPlayer(0);
                prepareMediaPlayer(c);
            }
        });

        ImageButton prevTrackButton = (ImageButton) playerFragmentView.findViewById(R.id.playerPrevImageButton);
        prevTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c.moveToPosition(c.getPosition() - 1);
                if (c.isBeforeFirst()) {
                    c.moveToLast();
                }
                trackIndex = c.getPosition();
                loadTrackDetails(playerFragmentView, c);
                resetPlayer();
                initializeMediaPlayer(0);
                prepareMediaPlayer(c);
            }
        });
        return playerFragmentView;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(seekBarUpdateRunnable);
        mediaPlayerCurrentPosition = mediaPlayer.getCurrentPosition();
        resetPlayer();
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

    private void loadTrackDetails(View view, Cursor c) {
        Helper.setViewText(view, R.id.playerArtistTextView, c.getString(COL_ARTIST_NAME));
        Helper.setViewText(view, R.id.playerAlbumTextView, c.getString(COL_ALBUM_NAME));
        Helper.setViewText(view, R.id.playerTrackTextView, c.getString(COL_TRACK_NAME));
        String imageUrl = c.getString(COL_ALBUM_IMAGE_URL);
        ImageView imageView = (ImageView) view.findViewById(R.id.playerImageView);
        if (imageUrl != null) {
            Picasso.with(getActivity()).load(imageUrl).into(imageView);
        } else {
            Picasso.with(getActivity()).load(R.drawable.placeholder_128x128).into(imageView);
        }
    }

    private void prepareMediaPlayer(Cursor c) {
        try {
            mediaPlayer.setDataSource(c.getString(COL_PREVIEW_URL));
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }

    private void initializeMediaPlayer(final int elapsed) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                int duration = mediaPlayer.getDuration();
                Helper.setViewText(playerFragmentView, R.id.playerDurationTextView,
                        Helper.formatTimeForPlayer(duration));
                seekBar.setMax(duration);
                mediaPlayer.seekTo(elapsed);
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
    }

    private void resetPlayer() {
        mediaPlayer.release();
        mediaPlayer = null;
        seekBar.setProgress(0);
    }
}
