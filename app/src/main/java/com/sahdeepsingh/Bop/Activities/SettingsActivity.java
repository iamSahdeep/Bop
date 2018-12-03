package com.sahdeepsingh.Bop.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.settings.AppCompatPreferenceActivity;
import com.sahdeepsingh.Bop.utils.utils;

public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getFragmentManager().beginTransaction().replace(R.id.settings_frame, new MainPreferenceFragment()).commit();
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("modes") || s.equals("themes")) {
            //not working don't know why, will work on it later
            //recreate();
            Toast.makeText(this, "Changes Done", Toast.LENGTH_SHORT).show();
        }

    }

    public static class MainPreferenceFragment extends PreferenceFragment {

        Preference version, feedback;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            version = findPreference("version");
            version.setSummary(Main.versionName);

            feedback = findPreference(getResources().getString(R.string.key_send_feedback));
            feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    utils.sendFeedback(getActivity());
                    return true;
                }
            });

        }

    }
}