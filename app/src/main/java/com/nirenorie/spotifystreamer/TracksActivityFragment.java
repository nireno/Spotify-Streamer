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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
public class TracksActivityFragment extends Fragment {
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
        adapter = new TrackListAdapter(getActivity());
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

        if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_TRACKS)) {
            loadTracks(artistId);
        } else {
            topTracks = savedInstanceState.getParcelableArrayList(KEY_TRACKS);
            adapter.addAll(topTracks);
            tracksListView.setEmptyView(emptyListView);
        }

        tracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SpotifyTrack t = topTracks.get(i);
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra("track", t);
                startActivity(intent);
            }
        });
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
                    contentValues.put(DataContract.TrackEntry.COLUMN_NAME, track.preview_url);
                    contentResolver.insert(DataContract.TrackEntry.CONTENT_URI, contentValues);
                }

                /* TODO: remove debug code */
                Cursor cursor = contentResolver.query(DataContract.TrackEntry.buildTrackUriWithArtistId(artistId), null, null, null, null);
                Log.d(LOG_TAG, "Cursor row count: " + cursor.getCount());
                while (cursor.moveToNext()) {
                    Log.d(LOG_TAG, cursor.getString(cursor.getColumnIndex(DataContract.TrackEntry.COLUMN_PREVIEW_URL)));
                }
                cursor.close();
                adapter.addAll(topTracks);

                /* setEmptyView here to avoid brief flash of the "no tracks found" message*/
                tracksListView.setEmptyView(emptyListView);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(CLASS_TAG, "Error retrieving top tracks: " + error.getMessage());
            }
        });
    }


    private class TrackListAdapter extends ArrayAdapter<SpotifyTrack> {
        public TrackListAdapter(Context context) {
            this(context, 0);
        }

        public TrackListAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SpotifyTrack t = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_track, parent, false);
            }

            TextView tv = (TextView) convertView.findViewById(R.id.artistTextView);
            tv.setText(t.name);
            tv = (TextView) convertView.findViewById(R.id.albumTextView);
            tv.setText(t.album);

            ImageView iv = (ImageView) convertView.findViewById(R.id.artistImageView);
            if (t.imageUrl != null) {
                Picasso.with(getContext()).load(t.imageUrl).resize(IMAGE_SIZE, IMAGE_SIZE).centerCrop().into(iv);
            } else {
                Picasso.with(getContext()).load(R.drawable.placeholder_128x128).resize(IMAGE_SIZE, IMAGE_SIZE).into(iv);
            }

            return convertView;
        }
    }

}
