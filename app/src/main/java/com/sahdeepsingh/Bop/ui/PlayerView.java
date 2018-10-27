package com.sahdeepsingh.Bop.ui;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.view.View;
import android.view.Window;

import com.andremion.music.MusicCoverView;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.view.TransitionAdapter;

public class PlayerView extends AppCompatActivity {

    private MusicCoverView mCoverView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        Main.settings.load(this);
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.playerview);

        mCoverView = (MusicCoverView) findViewById(R.id.cover);
        mCoverView.setCallbacks(new MusicCoverView.Callbacks() {
            @Override
            public void onMorphEnd(MusicCoverView coverView) {
                // Nothing to do
            }

            @Override
            public void onRotateEnd(MusicCoverView coverView) {
                supportFinishAfterTransition();
            }
        });

        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                mCoverView.start();
            }
        });
    }

    public void onFabClick(View view) {
        mCoverView.stop();
    }
}
