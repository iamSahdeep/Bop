package com.sahdeepsingh.Bop.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;

public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("modes")) {
            Handler handler = new Handler();
            handler.postDelayed(this::recreate, 1);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

        }
    }

}