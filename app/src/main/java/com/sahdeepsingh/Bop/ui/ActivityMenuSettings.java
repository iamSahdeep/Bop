package com.sahdeepsingh.Bop.ui;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;

/**
 * A menu that allows the user to change the application's
 * settings/preferences/configuration.
 * <p>
 * This ListView is populated automatically from the
 * file `res/xml/preferences.xml`.
 * <p>
 * Thanks:
 * - For teaching me how to build the Settings Activity:
 * http://android-elements.blogspot.com.br/2011/06/creating-android-preferences-screen.html
 * <p>
 * If the user changes the application's theme, the changes are
 * applied through all the application.
 * <p>
 * This Activity listens for any change on the Theme, and if it
 * happens, we `recreate()` this Activity.
 * <p>
 * Every other class handles it's theme according to the methods
 * inside `ActivityMaster`.
 */
public class ActivityMenuSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String theme = Main.settings.get("themes", "default");

        switch (theme) {
            case "default":
                setTheme(R.style.darkTheme);
                break;
            case "light":
                setTheme(R.style.lightTheme);
                break;
            case "dark":
                setTheme(R.style.darkTheme);
                break;
        }

        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        PreferenceManager
                .getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("themes")) {
            recreate();
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}