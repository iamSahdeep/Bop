package com.sahdeepsingh.Bop.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Master Activity from which every other Activity inherits
 * (except for `SettingsActivity`).
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
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    /**
     * Keeping track of the current theme and Mode name.
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

        // Checking if changes were made, look these methods for better understanding
        refreshTheme();
        refreshMode();

        //just using it for limited time, will use File provider soon
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    /**
     * Called when the user returns to this activity after leaving.
     */
    @Override
    protected void onResume() {
        super.onResume();
        /**
         * Why called it again?
         * Cause we want to apply changes when activity resumes.
         * and If Changes were made we will recreate the Activity
         * recreate() was not working will try later
         * you know this happens :/
         **/
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

    }

    /* For refreshing the Day/Night Mode */
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
            }
            currentMode = mode;
            return true;
        }
        return false;
    }

    /* Multi Color Theme for the Application, look credits for this many themes */
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