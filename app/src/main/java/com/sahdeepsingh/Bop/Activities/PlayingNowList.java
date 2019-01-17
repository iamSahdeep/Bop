package com.sahdeepsingh.Bop.Activities;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sahdeepsingh.Bop.Adapters.AdapterSong;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.sahdeepsingh.Bop.Activities.MainScreen.BROADCAST_ACTION;

public class PlayingNowList extends BaseActivity implements MediaController.MediaPlayerControl {

    private TextView mTitleView, mCounterView, mPlaylistName;
    private LinearLayout mTitleViewq;
    private FloatingActionButton mFabView;
    private TextView mTimeView;
    private TextView mDurationView;
    private SeekBar seekArc;
    private RecyclerView songListView;

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
        if (seekArc != null) {
            seekArc.setProgress(getCurrentPosition());
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
        seekArc = findViewById(R.id.progress);
        mFabView = findViewById(R.id.fab);
        mTitleViewq = findViewById(R.id.title);
        mCounterView = findViewById(R.id.counter);
        mPlaylistName = findViewById(R.id.name);

        songListView.setLayoutManager(new LinearLayoutManager(this));
        songListView.setAdapter(new AdapterSong(Main.nowPlayingList));


        if (getIntent().getExtras() != null) {
            mPlaylistName.setText(Objects.requireNonNull(getIntent().getExtras()).getString("playlistname", "Current Playlist"));
            Main.musicService.playSong();
        }



        // While we're playing music, add an item to the
        // Main Menu that returns here.
        MainScreen.addNowPlayingItem();
        prepareSeekBar();
        changeSongBR = new ChangeSongBR();
    }


    public void initTransistion(View view) {
        //noinspection unchecked
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                new android.util.Pair<View, String>(mCoverView, "cover"),
                new android.util.Pair<View, String>(mTitleViewq, "title"),
                new android.util.Pair<View, String>(mTimeView, "time"),
                new android.util.Pair<View, String>(mDurationView, "duration"),
                new android.util.Pair<View, String>(seekArc, "progress"),
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

        seekArc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekArc.setMax((int) Main.musicService.currentSong.getDuration());
        final Handler handler = new Handler();
        PlayingNowList.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!Main.mainMenuHasNowPlayingItem)
                    finish();
                if (isPlaying()) {
                    int position = getCurrentPosition() / 1000;
                    int duration = (int) Main.musicService.currentSong.getDurationSeconds();
                    onUpdateProgress(position, duration);
                }
                handler.postDelayed(this, 1000);
            }
        });

        workOnImages();
    }

    public void playlistOptions(View view) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.inflate(R.menu.playlist_options);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.one:
                    showPlaylistDialog();
                    return true;
                /*case R.id.two:
                    //handle menu2 click
                    return true;
                case R.id.three:
                    //handle menu3 click
                    return true;*/
                default:
                    return false;
            }
        });
        popup.show();
    }

    class ChangeSongBR extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            seekArc.setMax((int) Main.musicService.currentSong.getDuration());
            songListView.setAdapter(new AdapterSong(Main.nowPlayingList));
            songListView.scrollToPosition(Main.musicService.currentSongPosition);
            mTitleView.setText(Main.musicService.currentSong.getTitle());
            mTitleView.setSelected(true);
            mCounterView.setText(String.format("%s Songs", String.valueOf(Main.nowPlayingList.size())));
            workOnImages();
        }
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


    private void showPlaylistDialog() {
        final ListView listView;
        Button create, cancel;
        ArrayList<String> allPlaylists = Main.songs.getPlaylistNames();
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = getLayoutInflater().inflate(R.layout.newplaylistdialog, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_newplaylistdialog, allPlaylists);
        listView = view.findViewById(R.id.playlistListview);
        listView.setAdapter(arrayAdapter);
        EditText name = view.findViewById(R.id.newPlaylistName);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String namenew = listView.getItemAtPosition(i).toString();
                name.setText(namenew);
            }
        });
        create = view.findViewById(R.id.createPlaylist);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().isEmpty()) {
                    name.setError("cant be empty");
                    return;
                }
                Main.showProgressDialog(PlayingNowList.this);
                Main.songs.newPlaylist(PlayingNowList.this, "external", name.getText().toString(), (ArrayList<Song>) Main.nowPlayingList);
                Toast.makeText(PlayingNowList.this, "Done", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                Main.hideProgressDialog();
            }
        });
        cancel = view.findViewById(R.id.cancelPlaylist);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }


}
