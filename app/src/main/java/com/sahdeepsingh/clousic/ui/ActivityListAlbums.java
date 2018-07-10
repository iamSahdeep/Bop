package com.sahdeepsingh.clousic.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sahdeepsingh.clousic.R;
import com.sahdeepsingh.clousic.playerMain.Main;


/**
 * Shows a list of albums from a specified artist.
 */
public class ActivityListAlbums extends ActivityMaster
	implements OnItemClickListener {

	/**
	 * List of songs that will be shown to the user.
	 */
	private ListView songListView;

	/**
	 * Raw items that will be shown on ListView.
	 */
	private ArrayList<String> items;

	private String currentArtist;

	@Override
	protected void onCreate(Bundle tableSex) {
		super.onCreate(tableSex);

		setContentView(R.layout.activity_list_songs);

		songListView = (ListView)findViewById(R.id.activity_list_songs_list);

		// We expect an extra with the artist name
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();

		if (bundle == null)
			throw new RuntimeException("Expected Artist Name");

		// This is the artist that we'll display the albums.
		currentArtist = (String)bundle.get("artist");

		if (currentArtist == null || currentArtist.isEmpty())
			throw new RuntimeException("Expected Artist Name");

		this.setTitle(currentArtist);

		// Connects the song list to an adapter
		// (thing that creates several Layouts from the song list)
		items = Main.songs.getAlbumsByArtist(currentArtist);

		// Let's prepend all the albums with this label.
		// Then, when selecting the item, we'll need to
		// subtract one.
		items.add(0, getResources().getString(R.string.all_songs));

		// Adapter that will convert from Strings to List Items
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>
		(this, android.R.layout.simple_list_item_1, items);

		// Filling teh list with all the items
		songListView.setAdapter(adapter);

		songListView.setOnItemClickListener(this);

		// This enables the "Up" button on the top Action Bar
		// Note that it returns to the parent Activity, specified
		// on `AndroidManifest`
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Will react to the user selecting an item.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		// We can only handle the user choice from now on
		// if we've successfully scanned the songs from the
		// device.
		if (! Main.songs.isInitialized())
			return;

		// Let's switch to a song list.
		Intent intent = new Intent(this, ActivityListSongs.class);

		// This is the special case - the user selected "All Albums".
		// We'll show all songs from this artist, then.
		if (position == 0) {
			Main.musicList = Main.songs.getSongsByArtist(currentArtist);

			intent.putExtra("title", currentArtist);
		}
		// Regular case, user selected a specific album.
		// Show all songs from that album.
		else {
			String selectedAlbum = items.get(position);

			Main.musicList = Main.songs.getSongsByAlbum(selectedAlbum);

			intent.putExtra("title", selectedAlbum);
		}
		startActivity(intent);
	}
}
