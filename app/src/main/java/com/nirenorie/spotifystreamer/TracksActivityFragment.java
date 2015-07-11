package com.nirenorie.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private final String CLASS_TAG = this.getClass().getSimpleName();
    String artistId = "";
    private int IMAGE_SIZE;
    private TrackListAdapter adapter;
    private View view;
    public TracksActivityFragment() {
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
        ListView lv = (ListView) view.findViewById(R.id.tracksListView);

        lv.setAdapter(adapter);
        loadTracks(artistId);

        return view;
    }

    private void loadTracks(String artistId) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();
        Map<String, Object> options = new HashMap<>();

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String countryCode = p.getString(getString(R.string.pref_country_key), getString(R.string.pref_country_default));

        options.put("country", countryCode);
        service.getArtistTopTrack(artistId, options, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                List<TrackListItem> items = new ArrayList<>();

                for (Track track : tracks.tracks) {
                    TrackListItem item = new TrackListItem();
                    item.name = track.name;
                    item.album = track.album.name;
                    if (track.album.images.size() > 0) {
                        item.imageUrl = Helper.getOptimalImage(track.album.images, IMAGE_SIZE).url;
                    }
                    items.add(item);
                }
                adapter.addAll(items);

                TextView noTracksTextView = (TextView) view.findViewById(R.id.noTracksTextView);
                if (items.size() > 0) {
                    noTracksTextView.setVisibility(View.INVISIBLE);
                } else {
                    noTracksTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(CLASS_TAG, "Error retrieving top tracks: " + error.getMessage());
            }
        });
    }

    private class TrackListItem {
        public String name;
        public String album;
        public String imageUrl;
    }

    private class TrackListAdapter extends ArrayAdapter<TrackListItem> {
        public TrackListAdapter(Context context) {
            this(context, 0);
        }

        public TrackListAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TrackListItem t = getItem(position);
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
