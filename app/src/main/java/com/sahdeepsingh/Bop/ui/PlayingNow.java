package com.sahdeepsingh.Bop.ui;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.AdapterSong;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.view.ProgressView;

import java.io.File;

import static com.sahdeepsingh.Bop.ui.MainScreen.BROADCAST_ACTION;

public class PlayingNow extends AppCompatActivity implements MediaController.MediaPlayerControl, AdapterView.OnItemClickListener {

    private TextView mTitleView, mCounterView;
    private LinearLayout mTitleViewq;
    private FloatingActionButton mFabView;
    private TextView mTimeView;
    private TextView mDurationView;
    private ProgressView mProgressView;
    private RecyclerView songListView;
    @SuppressLint("HandlerLeak")
    private final Handler mUpdateProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final int position = getCurrentPosition() / 1000;
            final int duration = (int) Main.musicService.currentSong.getDurationSeconds();
            onUpdateProgress(position, duration);
            sendEmptyMessageDelayed(0, DateUtils.SECOND_IN_MILLIS);
        }
    };

    ChangeSongBR changeSongBR;

    private boolean paused = false;
    private boolean playbackPaused = false;
    private ImageView mCoverView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.playerview_list);

        songListView = findViewById(R.id.tracks_nowplaying);

        mCoverView = findViewById(R.id.cover);
        mTitleView = findViewById(R.id.titleTrack);
        mTimeView = findViewById(R.id.time);
        mDurationView = findViewById(R.id.duration);
        mProgressView = findViewById(R.id.progress);
        mFabView = findViewById(R.id.fab);
        mTitleViewq = findViewById(R.id.title);
        mCounterView = findViewById(R.id.counter);

        mFabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabClick();
            }
        });
        songListView.setLayoutManager(new LinearLayoutManager(this));
        songListView.setAdapter(new AdapterSong(Main.nowPlayingList));

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {

            if (bundle.containsKey("file")) {
                File file = (File) bundle.get("file");
                if (Main.songs.getSongbyFile(file) != null) {
                    Main.musicService.add(Main.songs.getSongbyFile(file));
                    Main.musicService.playSong();
                }
            }

            // There's the other optional extra - sorting rule
            if (bundle.containsKey("sort"))
                Main.musicService.sortBy((String) bundle.get("sort"));

            // If we received an extra with the song position
            // inside the now playing list, start playing it
            if (bundle.containsKey("songPosition")) {
                int songToPlayIndex = bundle.getInt("songPosition");
                Main.musicService.setSong(songToPlayIndex);
                Main.musicService.playSong();
            }
            if (bundle.containsKey("playlistName")) {
                if (!Main.nowPlayingList.isEmpty())
                    Main.musicService.setList(Main.musicList);
                Main.musicService.playSong();

            }
            if (bundle.containsKey("genreName")) {
                if (!Main.nowPlayingList.isEmpty())
                    Main.musicService.setList(Main.musicList);
                Main.musicService.playSong();
            }
            if (bundle.containsKey("albumName")) {
                if (!Main.nowPlayingList.isEmpty())
                    Main.musicService.setList(Main.musicList);
                Main.musicService.playSong();
            }
        }

        // Scroll the list view to the current song.
        //songListView.getLayoutManager().scrollToPosition(Main.nowPlayingList.);

        // We'll get warned when the user clicks on an item
        // and when he long selects an item.
        // songListView.setOnItemClickListener(this);
        //  songListView.setOnItemLongClickListener(this);


        // While we're playing music, add an item to the
        // Main Menu that returns here.
        MainScreen.addNowPlayingItem();
        prepareSeekBar();
        changeSongBR = new ChangeSongBR();
    }

    public void onFabClick() {
        //noinspection unchecked
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                new android.util.Pair<View, String>(mCoverView, "cover"),
                new android.util.Pair<View, String>(mTitleViewq, "title"),
                new android.util.Pair<View, String>(mTimeView, "time"),
                new android.util.Pair<View, String>(mDurationView, "duration"),
                new android.util.Pair<View, String>(mProgressView, "progress"),
                new android.util.Pair<View, String>(mFabView, "fab"));
        startActivity(new Intent(this, PlayerView.class), options.toBundle());
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

    private void prepareSeekBar() {

        /*final Handler handler = new Handler();
        PlayingNow.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isPlaying()) {

                    mTimeView.setText(DateUtils.formatElapsedTime(Main.musicService.currentSong.getDurationSeconds()));
                    mDurationView.setText(DateUtils.formatElapsedTime(getDuration()));
                }
                handler.postDelayed(this, 1);
            }
        });*/

        workOnImages();
    }

    class ChangeSongBR extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mUpdateProgressHandler.sendEmptyMessage(0);
            songListView.setAdapter(new AdapterSong(Main.nowPlayingList));
            songListView.scrollToPosition(Main.musicService.currentSongPosition);
            mTitleView.setText(Main.musicService.currentSong.getTitle());
            mTitleView.setSelected(true);
            mCounterView.setText(String.format("%s Songs", String.valueOf(Main.nowPlayingList.size())));
            workOnImages();
            if (!Main.musicService.isPaused()) {
                mFabView.setImageResource(R.mipmap.ic_pause);
            } else {
                mFabView.setImageResource(R.mipmap.ic_play);
            }
        }
    }

    private void newPlaylist() {

        // The input box where user will type new name
        final EditText input = new EditText(PlayingNow.this);

        // Labels
        String dialogTitle = PlayingNow.this.getString(R.string.menu_now_playing_dialog_create_playlist_title);
        String dialogText = PlayingNow.this.getString(R.string.menu_now_playing_dialog_create_playlist_subtitle);
        String buttonOK = PlayingNow.this.getString(R.string.menu_now_playing_dialog_create_playlist_button_ok);
        String buttonCancel = PlayingNow.this.getString(R.string.menu_now_playing_dialog_create_playlist_button_cancel);

        // Creating the dialog box that asks the user,
        // with the question and options.
        new AlertDialog.Builder(PlayingNow.this)
                .setTitle(dialogTitle)
                .setMessage(dialogText)
                .setView(input)

                // Creates the OK button, attaching the action to create the Playlist
                .setPositiveButton(buttonOK, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        String playlistName = input.getText().toString();

                        // TODO: Must somehow update the Playlist Activity if it's
                        //       on the background!
                        //       The ListView only updates when Playlist Menu gets
                        //       created from scratch.
                        Main.songs.newPlaylist(PlayingNow.this, "external", playlistName, Main.nowPlayingList);

                        String createPlaylistText = PlayingNow.this.getString(R.string.menu_now_playing_dialog_create_playlist_success, playlistName);

                        // Congratulating the user with the
                        // new Playlist name
                        Toast.makeText(PlayingNow.this,
                                createPlaylistText,
                                Toast.LENGTH_SHORT).show();

                    }

                    // Creates the CANCEL button, that
                    // doesn't do nothing
                    // (since a Playlist is only created
                    // when pressing OK).
                })
                .setNegativeButton(buttonCancel,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do nothing, yay!
                            }

                            // Lol, this is where we actually call the Dialog.
                            // Note for newcomers: The code continues to execute.
                            // This is an asynchronous task.
                        }).show();
    }


    /**
     * Another Activity is taking focus. (either from user going to another
     * Activity or home)
     */
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
    }

    /**
     * Callback to when the user pressed the `pause` button.
     */
    @Override
    public void pause() {
        Main.musicService.pausePlayer();
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

    /**
     * When the user selects a music inside the "Now Playing List", we'll start
     * playing it right away.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Prepare the music service to play the song.
        Main.musicService.setSong(position);
        Main.musicService.playSong();

        if (playbackPaused) {
            playbackPaused = false;
        }
        onResume();
    }
}
