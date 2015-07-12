package com.nirenorie.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String CLASS_TAG = MainActivityFragment.class.getSimpleName();
    private int IMAGE_SIZE;
    private ArtistListAdapter adapter;
    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArtistListAdapter(getActivity());
        IMAGE_SIZE = Integer.parseInt(getString(R.string.thumbnail_size));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listViewArtists = (ListView)v.findViewById(R.id.lvArtists);

        listViewArtists.setAdapter(adapter);

        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), TracksActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, adapter.getItem(i).id);
                startActivity(intent);
            }
        });

        EditText editText = (EditText) v.findViewById(R.id.searchArtistEditText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String name = v.getText().toString();
                    loadArtists(name);
                    handled = true;
                }
                return handled;
            }
        });
        return v;

        /*TODO: List does not reload when screen orientation changes */
    }

    private void loadArtists(String name) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();
        if (!name.equals("")) {
            service.searchArtists(name, new Callback<ArtistsPager>() {
                @Override
                public void success(ArtistsPager artistsPager, Response response) {
                    List<Artist> artists = artistsPager.artists.items;
                    List<ArtistListItem> items = new ArrayList<>();
                    for (Artist artist : artists) {
                        ArtistListItem item = new ArtistListItem();
                        item.id = artist.id;
                        item.name = artist.name;
                        if (artist.images.size() > 0) {
                            item.imageUrl = Helper.getOptimalImage(artist.images, IMAGE_SIZE).url;
                        }
                        items.add(item);
                    }
                    adapter.clear();
                    adapter.addAll(items);

                    if(items.size() == 0) {
                        Toast.makeText(getActivity(), getText(R.string.no_results_artist), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(CLASS_TAG, "Spotify API artist-search error:" + error.getMessage());
                }
            });
        }
    }

    private class ArtistListItem {
        public String id;
        public String name;
        public String imageUrl;
    }

    private class ArtistListAdapter extends ArrayAdapter<ArtistListItem> {
        public ArtistListAdapter(Context context, List<ArtistListItem> artists) {
            super(context, 0, artists);
        }

        public ArtistListAdapter(Context context) {
            this(context, new ArrayList<ArtistListItem>());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ArtistListItem artist = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
            }

            ImageView artistImageView = (ImageView) convertView.findViewById(R.id.artistImageView);
            if (artist.imageUrl != null) {
            /*  It's unclear that I need to ensure the optimal image is in the 0th position when adding Artists to the Adapter
            * TODO: Follow example in TracksActivityFragment to setup an Adapter of ArtistListItems that only stores the url of the appropriately sized image */
                Picasso.with(getContext()).load(artist.imageUrl).resize(IMAGE_SIZE, IMAGE_SIZE).centerCrop().into(artistImageView);
            } else {
                Picasso.with(getContext()).load(R.drawable.placeholder_128x128).resize(IMAGE_SIZE, IMAGE_SIZE).into(artistImageView);
            }

            TextView artistTextView = (TextView) convertView.findViewById(R.id.artistTextView);
            artistTextView.setText(artist.name);

            return convertView;
        }
    }
}
