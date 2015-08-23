package com.nirenorie.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {
    private static final String TRACKS_FRAGMENT_TAG = "TRACKS";
    private static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.top_tracks_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                Fragment f = new TracksActivityFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.top_tracks_container, f, TRACKS_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(String artistId) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putString(TracksActivityFragment.ARTIST_ARG, artistId);
            Fragment f = new TracksActivityFragment();
            f.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_container, f, TRACKS_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, TracksActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, artistId);
            startActivity(intent);
        }

    }
}
