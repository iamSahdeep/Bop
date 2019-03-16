package com.sahdeepsingh.Bop.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.transition.Transition;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andremion.music.MusicCoverView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.Visualizers.CircleBarVisualizer;
import com.sahdeepsingh.Bop.equalizer.EqualizerFragment;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.utils.utils;
import com.sahdeepsingh.Bop.views.TransitionAdapter;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import me.tankery.lib.circularseekbar.CircularSeekBar;

public class PlayerView extends BaseActivity implements MediaController.MediaPlayerControl {

    ImageView next, previous, rewind, forward, shuffle, repeat, eq;
    private MusicCoverView mCoverView;
    private FloatingActionButton mFabView;
    private TextView mTimeView;
    private TextView mDurationView;
    private CircularSeekBar mProgressView;
    private CircleBarVisualizer circleBarVisualizer;
    private TextView mTitleView;


    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            updatePlaybackState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
                updateMediaDescription(metadata.getDescription());
                updateDuration(metadata);
            }
        }
    };

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
        eq = findViewById(R.id.equaButton);
        circleBarVisualizer = findViewById(R.id.visualizer);
        circleBarVisualizer.setColor(utils.getThemeAttrColor(this, R.styleable.Theme_primaryColor));

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
    }

    private void setclickListeners() {

        eq.setImageDrawable(utils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_equalizer)));
        next = findViewById(R.id.next);
        next.setImageDrawable(utils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_skip)));
        previous = findViewById(R.id.previous);
        previous.setImageDrawable(utils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_previous)));
        forward = findViewById(R.id.forward);
        forward.setImageDrawable(utils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_forward)));
        rewind = findViewById(R.id.rewind);
        rewind.setImageDrawable(utils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_rewind)));
        shuffle = findViewById(R.id.shuffle);
        repeat = findViewById(R.id.repeat);

        if (!Main.musicService.isShuffle()) {
            shuffle.setImageDrawable((utils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_shuffle_off))));
        } else {
            shuffle.setImageDrawable((utils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_shuffle_on))));
        }
        if (Main.musicService.isRepeat() == 0) {
            repeat.setImageDrawable((utils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_repeat_one))));
        } else if (Main.musicService.isRepeat() == 1) {
            repeat.setImageDrawable((utils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_repeat_on))));
        } else
            repeat.setImageDrawable((utils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_repeat_off))));

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
                    shuffle.setImageDrawable((utils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_shuffle_off))));
                } else {
                    shuffle.setImageDrawable((utils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_shuffle_on))));
                }
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.toggleRepeat();
                if (Main.musicService.isRepeat() == 0) {
                    repeat.setImageDrawable((utils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_repeat_one))));
                } else if (Main.musicService.isRepeat() == 1) {
                    repeat.setImageDrawable((utils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_repeat_on))));
                } else
                    repeat.setImageDrawable((utils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_repeat_off))));
            }
        });
    }


    public void onFabClick(View view) {
        Main.musicService.togglePlayback();
        if (!Main.musicService.isPaused()) {
            mFabView.setImageDrawable((utils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_pause))));
        } else {
            mFabView.setImageDrawable((utils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_play))));
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
    }

    /**
     * Activity has become visible.
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            connectToSession(Main.musicService.getSessionToken());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Activity is no longer visible.
     */

    @Override
    public void start() {
        Main.musicService.unpausePlayer();
        mFabView.setImageDrawable((utils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_pause))));
    }

    /**
     * Callback to when the user pressed the `pause` button.
     */
    @Override
    public void pause() {
        Main.musicService.pausePlayer();
        mFabView.setImageDrawable((utils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_play))));
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

    }

    /**
     * Jumps to the previous song and starts playing it right now.
     */
    public void playPrevious() {
        Main.musicService.previous(true);
        Main.musicService.playSong();
    }

    @Override
    protected void onStop() {
        super.onStop();

        MediaControllerCompat controllerCompat = MediaControllerCompat.getMediaController(PlayerView.this);
        if (controllerCompat != null) {
            controllerCompat.unregisterCallback(mCallback);
        }
    }

    private void prepareSeekBar() {

        mProgressView.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                if (fromUser) {
                    seekTo((int) progress * 1000);
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
                if (!Main.mainMenuHasNowPlayingItem)
                    finish();
                if (isPlaying()) {
                    int position = getCurrentPosition() / 1000;
                    int duration = (int) Main.musicService.currentSong.getDurationSeconds();
                    onUpdateProgress(position, duration);
                }
                handler.postDelayed(this, 100);
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
            mProgressView.setProgress(position);
        }
    }

    private void updateMediaDescription(MediaDescriptionCompat description) {
        if (description == null) {
            return;
        }

        mTitleView.setText(description.getTitle());
        circleBarVisualizer.setPlayer(getAudioSessionId());
        mTitleView.setSelected(true);
        mCoverView.setImageBitmap(description.getIconBitmap());

    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        mProgressView.setMax(duration);
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }

        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                mFabView.setImageDrawable((utils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause))));
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mFabView.setImageDrawable((utils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play))));
                break;
            case PlaybackStateCompat.STATE_NONE:
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                finish();
                break;
            default:
        }

    }

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController == null) {
            mediaController = new MediaControllerCompat(PlayerView.this, token);
        }
        if (mediaController.getMetadata() == null) {
            finish();
            return;
        }

        MediaControllerCompat.setMediaController(PlayerView.this, mediaController);
        mediaController.registerCallback(mCallback);
        PlaybackStateCompat state = mediaController.getPlaybackState();
        updatePlaybackState(state);
        MediaMetadataCompat metadata = mediaController.getMetadata();
        if (metadata != null) {
            updateMediaDescription(metadata.getDescription());
            updateDuration(metadata);
        }
    }


    public void equalizer(View view) {
        Main.musicService.player.setLooping(true);
        EqualizerFragment equalizerFragment = EqualizerFragment.newBuilder()
                .setAccentColor(utils.getThemeAttrColor(PlayerView.this, R.styleable.Theme_primaryDarkColor))
                .setAudioSessionId(getAudioSessionId())
                .build();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, equalizerFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        mCoverView.stop();
    }

}
