package com.sahdeepsingh.Bop.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.settings.AppCompatPreferenceActivity;

public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String mode = Main.settings.get("modes", "Day");
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

        String theme = Main.settings.get("themes", "Red");

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
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("modes")) {
            //not working don't know why, will work on it later
            recreate();
        }

    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

}