package com.sahdeepsingh.Bop.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Master Activity from which every other Activity inherits
 * (except for `ActivityMenuSettings`).
 *
 * If contains some things they all have in common:
 *
 * - They can change the color theme at runtime;
 * - They all have the same context menu (bottom menu).
 *   (note that there's an extra item "Now Playing" that
 *    only appears if user started playing something)
 *
 * What we do is make each Activity keep track of which
 * theme it currently has.
 * Whenever they have focus, we test to see if the global theme
 * was changed by the user.
 * If it was, it `recreate()`s itself.
 *
 * @note We must call `Activity.setTheme()` BEFORE
 *       `Activity.setContentView()`!
 *
 * Sources that made me apply this idea, thank you so much:
 * - http://stackoverflow.com/a/4673209
 * - http://stackoverflow.com/a/11875930
 */
@SuppressLint("Registered") // No need to register this class on AndroidManifest
public class ActivityMaster extends AppCompatActivity {

	/**
	 * Keeping track of the current theme name.
	 *
	 * @note It's name and valid values are defined on
	 *       `res/values/strings.xml`, at the fields
	 *       we can change on the Settings menu.
	 */
	protected String currentTheme = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);


		Main.startMusicService(this);
	}

	/**
	 * Called when the user returns to this activity after leaving.
	 */
	@Override
	protected void onResume() {
		super.onResume();

		SlidingUpPanelLayout slidingUpPanelLayout = findViewById(R.id.sliding_layout);
		if (Main.mainMenuHasNowPlayingItem)
		{
			TextView t = findViewById(R.id.bottomtextView);
			TextView a = findViewById(R.id.bottomtextartist);
			t.setText(Main.musicService.currentSong.getTitle());
			a.setText("by " + Main.musicService.currentSong.getArtist());
			t.setSelected(true);
			slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
		}
		else
		{
			slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
			slidingUpPanelLayout.setCoveredFadeColor(getResources().getColor(R.color.transparent));

		}
	}



	/**
	 * Let's set a context menu (menu that appears when
	 * the user presses the "menu" button).
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Default options specified on the XML
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_context, menu);

		// Extra option to go to Now Playing screen
		// (only activated when there's an actual Now Playing screen)
		if (Main.mainMenuHasNowPlayingItem)
			menu.findItem(R.id.context_menu_now_playing).setVisible(true);

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * This method gets called whenever the user clicks an
	 * item on the context menu.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		// I know it's bad to force quiting the program,
		// but I just love when applications have this option
		case R.id.context_menu_end:
			Main.forceExit(this);
			break;

		case R.id.context_menu_settings:
			startActivity(new Intent(this, ActivityMenuSettings.class));
			break;

		case R.id.context_menu_now_playing:
			Intent nowPlayingIntent = new Intent(this, ActivityNowPlaying.class);
			nowPlayingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			startActivity(nowPlayingIntent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}


}

