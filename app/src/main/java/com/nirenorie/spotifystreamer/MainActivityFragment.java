package com.nirenorie.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

    private ArtistListAdapter adapter;
    private ListView listViewArtists;

    public MainActivityFragment() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();

        View v = inflater.inflate(R.layout.fragment_main, container, false);
        listViewArtists = (ListView)v.findViewById(R.id.lvArtists);

        service.searchArtists("rihanna", new Callback<ArtistsPager>() {
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
                adapter = new ArtistListAdapter(getActivity(), artists);
                listViewArtists.setAdapter(adapter);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(CLASS_TAG, "Spotify API artist-search error:" + error.getMessage());
            }
        });

        return v;
    }
}
