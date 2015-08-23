package com.nirenorie.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class TracksActivity extends AppCompatActivity implements TracksActivityFragment.Callback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(TracksActivityFragment.ARTIST_ARG,
                    getIntent().getStringExtra(Intent.EXTRA_TEXT));

            TracksActivityFragment fragment = new TracksActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.top_tracks_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onTrackItemClick(String artistId, int trackIndex) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.EXTRA_ARTIST_ID, artistId);
        intent.putExtra(PlayerActivity.EXTRA_TRACK_INDEX, trackIndex);
        startActivity(intent);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_tracks, menu);
//        return true;
//    }
//
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
        return true;
    } else if (id == android.R.id.home) {
        onBackPressed();
        return true;
    }

    return super.onOptionsItemSelected(item);
}
}
