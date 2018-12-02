package com.sahdeepsingh.Bop.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Master Activity from which every other Activity inherits
 * (except for `Activityettings`).
 * <p>
 * If contains some things they all have in common:
 * <p>
 * - They can change the color theme at runtime;
 * - They all have the same context menu (bottom menu).
 * (note that there's an extra item "Now Playing" that
 * only appears if user started playing something)
 * <p>
 * What we do is make each Activity keep track of which
 * theme it currently has.
 * Whenever they have focus, we test to see if the global theme
 * was changed by the user.
 * If it was, it `recreate()`s itself.
 *
 * @note We must call `Activity.setTheme()` BEFORE
 * `Activity.setContentView()`!
 * <p>
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
     * `res/values/strings.xml`, at the fields
     * we can change on the Settings menu.
     */
    protected String currentMode = "";
    protected String currentTheme = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshMode();
        refreshTheme();
    }

    /**
     * Called when the user returns to this activity after leaving.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (refreshMode()) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        if (refreshTheme()) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        ActivityMaster.this.invalidateOptionsMenu();
        SlidingUpPanelLayout slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setTouchEnabled(false);
        if (Main.mainMenuHasNowPlayingItem) {
            Main.musicService.notifyCurrentSong();
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            slidingUpPanelLayout.setCoveredFadeColor(getResources().getColor(R.color.transparent));
        } else {
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
        if (Main.musicService.isPlaying())
            menu.findItem(R.id.nowPlayingIcon).setVisible(true);

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
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.nowPlayingIcon:
                startActivity(new Intent(this,PlayingNow.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean refreshMode() {

        String mode = Main.settings.get("modes", "Day");

        if (!currentMode.equals(mode)) {
            switch (mode) {
                case "Day":
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case "Night":
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case "System":
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
                case "Automatic":
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                    break;
            }
            currentMode = mode;
            return true;
        }
        return false;
    }

    public boolean refreshTheme() {

        String theme = Main.settings.get("themes", "Red");

        if (!currentTheme.equals(theme)) {
            switch (theme) {
                case "Red":
                    setTheme(R.style.AppTheme_RED);
                    break;
                case "Pink":
                    setTheme(R.style.AppTheme_PINK);
                    break;
                case "Purple":
                    setTheme(R.style.AppTheme_PURPLE);
                    break;
                case "DeepPurple":
                    setTheme(R.style.AppTheme_DEEPPURPLE);
                    break;
                case "Indigo":
                    setTheme(R.style.AppTheme_INDIGO);
                    break;
                case "Blue":
                    setTheme(R.style.AppTheme_BLUE);
                    break;
                case "LightBlue":
                    setTheme(R.style.AppTheme_LIGHTBLUE);
                    break;
                case "Cyan":
                    setTheme(R.style.AppTheme_CYAN);
                    break;
                case "Teal":
                    setTheme(R.style.AppTheme_TEAL);
                    break;
                case "Green":
                    setTheme(R.style.AppTheme_GREEN);
                    break;
                case "LightGreen":
                    setTheme(R.style.AppTheme_LIGHTGREEN);
                    break;
                case "Lime":
                    setTheme(R.style.AppTheme_LIME);
                    break;
                case "Yellow":
                    setTheme(R.style.AppTheme_YELLOW);
                    break;
                case "Amber":
                    setTheme(R.style.AppTheme_YELLOW);
                    break;
                case "Orange":
                    setTheme(R.style.AppTheme_ORANGE);
                    break;
                case "DeepOrange":
                    setTheme(R.style.AppTheme_DEEPORANGE);
                    break;
                case "Brown":
                    setTheme(R.style.AppTheme_BROWN);
                    break;
                case "Gray":
                    setTheme(R.style.AppTheme_GRAY);
                    break;
                case "BlueGray":
                    setTheme(R.style.AppTheme_BLUEGRAY);
                    break;
            }
            currentTheme = theme;
            return true;
        }
        return false;
    }


}

