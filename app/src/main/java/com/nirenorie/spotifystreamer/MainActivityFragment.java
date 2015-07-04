package com.nirenorie.spotifystreamer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String CLASS_TAG = MainActivityFragment.class.getSimpleName();
    private final int IMAGE_WIDTH = ArtistListAdapter.IMAGE_WIDTH;
    ArtistLoader loader;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);
        loader = new ArtistLoader(getActivity());
        ListView listViewArtists = (ListView)v.findViewById(R.id.lvArtists);

        listViewArtists.setAdapter(loader.getAdapter());
        EditText editText = (EditText) v.findViewById(R.id.searchArtistEditText);
        editText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String name = v.getText().toString();
                    if(name != ""){
                        loader.loadArtists(v.getText().toString());
                    }
                    handled = true;
                }
                return handled;
            }
        });
        return v;

    }


    private class ArtistLoader {
        private ArtistListAdapter adapter;

        public ArtistLoader(Context context){
            adapter = new ArtistListAdapter(context);
        }

        public ArtistListAdapter getAdapter(){
            return adapter;
        }

        private Image getOptimalImage(List<Image> images){
            int lowest = images.get(0).width;
            Image image = images.get(0);
            for(Image i : images){
                if (i.width < IMAGE_WIDTH) continue;
                if(i.width - IMAGE_WIDTH < lowest){
                    lowest = i.width;
                    image = i;
                }
            }
            return image;
        }

        private void loadArtists(String name){
            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();
            service.searchArtists(name, new Callback<ArtistsPager>() {
                @Override
                public void success(ArtistsPager artistsPager, Response response) {
                    List<Artist> artists = artistsPager.artists.items;
                    for(Artist a: artists){
                        if(a.images.size() > 0){
                            Image image = getOptimalImage(a.images);
                            a.images.clear();
                            a.images.add(image);
                        }
                    }
                    adapter.clear();
                    adapter.addAll(artists);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(CLASS_TAG, "Spotify API artist-search error:" + error.getMessage());
                }
            });
        }
    }

}
