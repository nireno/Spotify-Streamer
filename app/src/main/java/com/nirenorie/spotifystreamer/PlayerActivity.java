package com.nirenorie.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PlayerActivity extends AppCompatActivity {
    public static final String EXTRA_ARTIST_ID = "ARTIST";
    public static final String EXTRA_TRACK_INDEX = "TRACK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle arguments = new Bundle();
            arguments.putString(PlayerActivityFragment.ARG_ARTIST,
                    intent.getStringExtra(EXTRA_ARTIST_ID));
            arguments.putInt(PlayerActivityFragment.ARG_TRACK_INDEX,
                    intent.getIntExtra(EXTRA_TRACK_INDEX, 0));

            PlayerActivityFragment fragment = new PlayerActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.player_container, fragment)
                    .commit();
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_player, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
