package com.nirenorie.spotifystreamer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nirenorie.spotifystreamer.data.DataContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class TracksActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_ARTIST_ID = "extraArtistId";
    public static final String EXTRA_POSITION = "extraPosition";
    private static final int TOP_TRACKS_LOADER = 0;
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String CLASS_TAG = this.getClass().getSimpleName();

    private String artistId = "";
    private int IMAGE_SIZE;
    private TrackListAdapter adapter;
    private View view;
    private ArrayList<SpotifyTrack> topTracks = new ArrayList<>();
    private String KEY_TRACKS = "tracks";
    private TextView emptyListView;
    private ListView tracksListView;

    public TracksActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_TRACKS, topTracks);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TrackListAdapter(getActivity(), null, 0);
        Intent intent = getActivity().getIntent();
        artistId = intent.getStringExtra(Intent.EXTRA_TEXT);
        IMAGE_SIZE = Integer.parseInt(getString(R.string.thumbnail_size));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tracks, container);
        tracksListView = (ListView) view.findViewById(R.id.tracksListView);
        tracksListView.setAdapter(adapter);
        emptyListView = (TextView) view.findViewById(R.id.noTracksTextView);
        tracksListView.setEmptyView(emptyListView);
        tracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra(EXTRA_ARTIST_ID, artistId);
                intent.putExtra(EXTRA_POSITION, i);
                startActivity(intent);
            }
        });
        getLoaderManager().restartLoader(TOP_TRACKS_LOADER, null, this);
        return view;
    }

    private void loadTracks(final String artistId) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();
        Map<String, Object> options = new HashMap<>();

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String countryCode = p.getString(getString(R.string.pref_country_key), getString(R.string.pref_country_default));

        options.put("country", countryCode);
        service.getArtistTopTrack(artistId, options, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                topTracks.clear();
                ContentResolver contentResolver = getActivity().getContentResolver();

                for (Track track : tracks.tracks) {
                    String imageUrl = null;
                    if (track.album.images.size() > 0) {
                        imageUrl = Helper.getOptimalImage(track.album.images, IMAGE_SIZE).url;
                    }
                    SpotifyTrack spotifyTrack = new SpotifyTrack(track.name, track.album.name, imageUrl
                            , track.artists.get(0).name, track.preview_url);
                    topTracks.add(spotifyTrack);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DataContract.TrackEntry.COLUMN_ALBUM_IMAGE_URL, imageUrl);
                    contentValues.put(DataContract.TrackEntry.COLUMN_ALBUM_NAME, track.album.name);
                    contentValues.put(DataContract.TrackEntry.COLUMN_ARTIST_ID, artistId);
                    contentValues.put(DataContract.TrackEntry.COLUMN_ARTIST_NAME, track.artists.get(0).name);
                    contentValues.put(DataContract.TrackEntry.COLUMN_PREVIEW_URL, track.preview_url);
                    contentValues.put(DataContract.TrackEntry.COLUMN_TRACK_NAME, track.name);
                    contentResolver.insert(DataContract.TrackEntry.CONTENT_URI, contentValues);
                }

                /* If no tracks were inserted, no need to restart the loader. Additionally this
                   currently prevents an infinite loop within onLoadFinished. */
                if (tracks.tracks.size() > 0) {
                    getLoaderManager().restartLoader(TOP_TRACKS_LOADER, null, TracksActivityFragment.this);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(CLASS_TAG, "Error retrieving top tracks: " + error.getMessage());
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), DataContract.TrackEntry.CONTENT_URI, null,
                DataContract.TrackEntry.COLUMN_ARTIST_ID + " = ?", new String[]{artistId}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            adapter.swapCursor(data);
        } else {
            loadTracks(artistId);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private class TrackListAdapter extends CursorAdapter {
        public TrackListAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context).inflate(R.layout.list_item_track, null, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String name = cursor.getString(cursor.getColumnIndex(DataContract.TrackEntry.COLUMN_TRACK_NAME));
            String album = cursor.getString(cursor.getColumnIndex(DataContract.TrackEntry.COLUMN_ALBUM_NAME));
            String imageUrl = cursor.getString(cursor.getColumnIndex(DataContract.TrackEntry.COLUMN_ALBUM_IMAGE_URL));
            String artist = cursor.getString(cursor.getColumnIndex(DataContract.TrackEntry.COLUMN_ARTIST_NAME));
            String previewUrl = cursor.getString(cursor.getColumnIndex(DataContract.TrackEntry.COLUMN_PREVIEW_URL));
            SpotifyTrack t = new SpotifyTrack(name, album, imageUrl, artist, previewUrl);

            TextView tv = (TextView) view.findViewById(R.id.artistTextView);
            tv.setText(t.name);
            tv = (TextView) view.findViewById(R.id.albumTextView);
            tv.setText(t.album);

            ImageView iv = (ImageView) view.findViewById(R.id.artistImageView);
            if (t.imageUrl != null) {
                Picasso.with(context).load(t.imageUrl).resize(IMAGE_SIZE, IMAGE_SIZE).centerCrop().into(iv);
            } else {
                Picasso.with(context).load(R.drawable.placeholder_128x128).resize(IMAGE_SIZE, IMAGE_SIZE).into(iv);
            }

        }
    }

}
