package com.sahdeepsingh.Bop.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.transition.Transition;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andremion.music.MusicCoverView;
import com.bullhead.equalizer.EqualizerFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.Visualizers.CircleBarVisualizer;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.views.TransitionAdapter;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import me.tankery.lib.circularseekbar.CircularSeekBar;

import static com.sahdeepsingh.Bop.Activities.MainScreen.BROADCAST_ACTION;

public class PlayerView extends AppCompatActivity implements MediaController.MediaPlayerControl {

    ChangeSongBR changeSongBR;
    ImageView next, previous, rewind, forward, shuffle, repeat;
    private MusicCoverView mCoverView;
    private FloatingActionButton mFabView;
    private TextView mTimeView;
    private TextView mDurationView;
    private CircularSeekBar mProgressView;
    private CircleBarVisualizer circleBarVisualizer;
    private TextView mTitleView;
    private boolean paused = false;
    private boolean playbackPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        Main.settings.load(this);
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.playerview);
        RelativeLayout wq = findViewById(R.id.fdsg);
        wq.bringToFront();
        mCoverView = findViewById(R.id.cover);
        mTitleView = findViewById(R.id.titleTrack);
        mTimeView = findViewById(R.id.time);
        mDurationView = findViewById(R.id.duration);
        mProgressView = findViewById(R.id.progress);
        mFabView = findViewById(R.id.fab);
        circleBarVisualizer = findViewById(R.id.visualizer);
        circleBarVisualizer.setColor(ContextCompat.getColor(this, R.color.primaryLightColor));

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
        setclickListeners();
        prepareSeekBar();
        changeSongBR = new ChangeSongBR();

    }

    private void setclickListeners() {

        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        forward = findViewById(R.id.forward);
        rewind = findViewById(R.id.rewind);
        shuffle = findViewById(R.id.shuffle);
        repeat = findViewById(R.id.repeat);

        if (!Main.musicService.isShuffle()) {
            shuffle.setImageResource(R.drawable.ic_shuffle_off);
        } else {
            shuffle.setImageResource(R.drawable.ic_shuffle_on);
        }
        if (Main.musicService.isRepeat() == 0) {
            repeat.setImageResource(R.drawable.ic_repeat_one);
        } else if (Main.musicService.isRepeat() == 1) {
            repeat.setImageResource(R.drawable.ic_repeat_on);
        } else repeat.setImageResource(R.drawable.ic_repeat_off);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNext();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPrevious();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekTo(getCurrentPosition() + 10000);
            }
        });

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekTo(getCurrentPosition() - 10000);
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.toggleShuffle();
                if (!Main.musicService.isShuffle()) {
                    shuffle.setImageResource(R.drawable.ic_shuffle_off);
                } else {
                    shuffle.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.toggleRepeat();
                if (Main.musicService.isRepeat() == 0) {
                    repeat.setImageResource(R.drawable.ic_repeat_one);
                } else if (Main.musicService.isRepeat() == 1) {
                    repeat.setImageResource(R.drawable.ic_repeat_on);
                } else repeat.setImageResource(R.drawable.ic_repeat_off);
            }
        });
    }


    public void onFabClick(View view) {
        Main.musicService.togglePlayback();
        if (!Main.musicService.isPaused()) {
            mFabView.setImageResource(R.drawable.ic_pause);
        } else {
            mFabView.setImageResource(R.drawable.ic_play);
        }
    }

    private void workOnImages() {
        File path = null;
        if (Main.songs.getAlbumArt(Main.musicService.currentSong) != null)
            path = new File(Main.songs.getAlbumArt(Main.musicService.currentSong));
        Bitmap bitmap;
        if (path != null && path.exists()) {
            bitmap = BitmapFactory.decodeFile(path.getAbsolutePath());
        } else bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.back);
        mCoverView.setImageBitmap(bitmap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(changeSongBR);
        paused = true;
        playbackPaused = true;
    }

    /**
     * Activity has become visible.
     */
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        registerReceiver(changeSongBR, intentFilter);
        Main.musicService.notifyCurrentSong();
        if (paused) {
            paused = false;
        }
    }

    /**
     * Activity is no longer visible.
     */

    @Override
    public void start() {
        Main.musicService.unpausePlayer();
        mFabView.setImageResource(R.drawable.ic_pause);
    }

    /**
     * Callback to when the user pressed the `pause` button.
     */
    @Override
    public void pause() {
        Main.musicService.pausePlayer();
        mFabView.setImageResource(R.drawable.ic_play);
    }

    @Override
    public int getDuration() {
        if (Main.musicService != null && Main.musicService.musicBound
                && Main.musicService.isPlaying())
            return Main.musicService.getDuration();
        else
            return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (Main.musicService != null && Main.musicService.musicBound
                && Main.musicService.isPlaying())
            return Main.musicService.getPosition();
        else
            return 0;
    }

    @Override
    public void seekTo(int position) {
        Main.musicService.seekTo(position);
    }

    @Override
    public boolean isPlaying() {
        if (Main.musicService != null && Main.musicService.musicBound)
            return Main.musicService.isPlaying();

        return false;
    }

    @Override
    public int getBufferPercentage() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return Main.musicService.getAudioSession();
    }

    // Back to the normal methods

    /**
     * Jumps to the next song and starts playing it right now.
     */
    public void playNext() {
        Main.musicService.next(true);
        Main.musicService.playSong();

        // To prevent the MusicPlayer from behaving
        // unexpectedly when we pause the song playback.
        if (playbackPaused) {
            playbackPaused = false;
        }

/*
        musicController.show();
*/
    }

    /**
     * Jumps to the previous song and starts playing it right now.
     */
    public void playPrevious() {
        Main.musicService.previous(true);
        Main.musicService.playSong();

        // To prevent the MusicPlayer from behaving
        // unexpectedly when we pause the song playback.
        if (playbackPaused) {
            playbackPaused = false;
        }

/*
        musicController.show();
*/
    }

    private void prepareSeekBar() {

        mProgressView.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                if (fromUser) {
                    seekTo((int) progress);
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });

        final Handler handler = new Handler();
        PlayerView.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isPlaying()) {
                    int position = getCurrentPosition() / 1000;
                    int duration = (int) Main.musicService.currentSong.getDurationSeconds();
                    onUpdateProgress(position, duration);
                }
                handler.postDelayed(this, 1);
            }
        });

        workOnImages();
    }

    private void onUpdateProgress(int position, int duration) {
        if (mTimeView != null) {
            mTimeView.setText(DateUtils.formatElapsedTime(position));
        }
        if (mDurationView != null) {
            mDurationView.setText(DateUtils.formatElapsedTime(duration));
        }
        if (mProgressView != null) {
            mProgressView.setProgress(position * 1000);
        }
    }

    public void equalizer(View view) {
        Main.musicService.player.setLooping(true);
        EqualizerFragment equalizerFragment = EqualizerFragment.newBuilder()
                .setAccentColor(ContextCompat.getColor(this, R.color.primaryColor))
                .setAudioSessionId(getAudioSessionId())
                .build();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, equalizerFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        mCoverView.stop();
    }

    class ChangeSongBR extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mProgressView.setMax((int) Main.musicService.currentSong.getDuration());
            circleBarVisualizer.setPlayer(getAudioSessionId());
            mTitleView.setText(Main.musicService.currentSong.getTitle());
            mTitleView.setSelected(true);
            workOnImages();
            if (!Main.musicService.isPaused()) {
                mFabView.setImageResource(R.drawable.ic_pause);
            } else {
                mFabView.setImageResource(R.drawable.ic_play);
            }
        }
    }
}
