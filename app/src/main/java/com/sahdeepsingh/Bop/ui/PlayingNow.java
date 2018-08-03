package com.sahdeepsingh.Bop.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.AdapterSong;
import com.sahdeepsingh.Bop.controls.CircularSeekBar;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.visualizer.barVisuals;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;

import static com.sahdeepsingh.Bop.ui.MainScreen.BROADCAST_ACTION;

public class PlayingNow extends ActivityMaster implements MediaController.MediaPlayerControl, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    /**
     * Gets called when the Activity is getting initialized.
     */
    private Menu menu;
    private static final float BLUR_RADIUS = 25f;
    CircularSeekBar circularSeekBar;
    ImageView blurimage, centreimage, aa;
    TextView name, artist , TopName , TopArttist;
    ImageButton shuffletoggle, previousSong, PlayPause, nextSong, repeatToggle, pp;

    ChangeSongBR changeSongBR;

    SlidingUpPanelLayout slidingUpPanelLayout;

    /**
     * List that will display all the songs.
     */
    private ListView songListView;
    private boolean paused = false;
    private boolean playbackPaused = false;
    barVisuals barVisualss;

    /**
     * Thing that maps songs to items on the ListView.
     * <p>
     * We're keeping track of it so we can refresh the ListView if the user
     * wishes to change it's order.
     * <p>
     * Check out the leftmost menu and it's options.
     */
    private AdapterSong songAdapter;
    /**
     * Little menu that will show when the user
     * clicks the ActionBar.
     * It serves to sort the current song list.
     */
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_now);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        songListView = findViewById(R.id.list_nowplaying);

        circularSeekBar = findViewById(R.id.circularSeekBar);
        blurimage = findViewById(R.id.BlurImage);
        centreimage = findViewById(R.id.CircleImage);
        shuffletoggle = findViewById(R.id.shuffle);
        previousSong = findViewById(R.id.previous);
        PlayPause = findViewById(R.id.playPause);
        nextSong = findViewById(R.id.skip_next);
        repeatToggle = findViewById(R.id.repeat);
        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        name = findViewById(R.id.bottomtextView);
        TopName = findViewById(R.id.songMainTitle);
        TopArttist = findViewById(R.id.songMainArtist);
        artist = findViewById(R.id.bottomtextartist);
        pp = findViewById(R.id.bottomImagebutton);
        aa = findViewById(R.id.bottomImageview);

        barVisualss = findViewById(R.id.barVisuals);

        songAdapter = new AdapterSong(this, Main.nowPlayingList);
        songListView.setAdapter(songAdapter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {

            // There's the other optional extra - sorting rule
            if (bundle.containsKey("sort"))
                Main.musicService.sortBy((String) bundle.get("sort"));

            // If we received an extra with the song position
            // inside the now playing list, start playing it
            if (bundle.containsKey("songPosition")) {
                int songToPlayIndex = bundle.getInt("songPosition");
                Main.musicService.setSong(songToPlayIndex);
                Log.e("sD",String.valueOf(songToPlayIndex));
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
        songListView.setSelection(Main.musicService.currentSongPosition);

        // We'll get warned when the user clicks on an item
        // and when he long selects an item.
        songListView.setOnItemClickListener(this);
        songListView.setOnItemLongClickListener(this);


        // While we're playing music, add an item to the
        // Main Menu that returns here.
        MainScreen.addNowPlayingItem(this);
        prepareSeekBar();
        setControllListeners();

        changeSongBR = new ChangeSongBR();

        slidingUpPanelLayoutListen();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.removeItem(R.id.nowPlayingIcon);
        return true;
    }



    private void slidingUpPanelLayoutListen() {
        final LinearLayout songNameDisplay , BottomControls;
        songNameDisplay = findViewById(R.id.SongNameTop);
        BottomControls = findViewById(R.id.layout_item);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED)
                {
                    BottomControls.setVisibility(View.GONE);
                    songNameDisplay.setAlpha(0f);
                    songNameDisplay.setVisibility(View.VISIBLE);
                    songNameDisplay.animate().alpha(1.0f).setDuration(300).setListener(null);
                }else if (newState == SlidingUpPanelLayout.PanelState.DRAGGING){
                    BottomControls.setAlpha(0f);
                    songNameDisplay.setAlpha(0f);
                }else{
                    songNameDisplay.setVisibility(View.GONE);
                    BottomControls.setAlpha(0f);
                    BottomControls.setVisibility(View.VISIBLE);
                    BottomControls.animate().alpha(1.0f).setDuration(300).setListener(null);
                }
            }
        });
    }

    class ChangeSongBR extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            name.setText(Main.musicService.currentSong.getTitle());
            artist.setText(Main.musicService.currentSong.getArtist());
            TopName.setText(Main.musicService.currentSong.getTitle());
            TopArttist.setText(Main.musicService.currentSong.getArtist());
            name.setSelected(true);
            artist.setSelected(true);
            TopName.setSelected(true);
            TopArttist.setSelected(true);
            workOnImages();
            if (!Main.musicService.isPaused()) {
                pp.setImageResource(R.drawable.ic_pause_white);
                PlayPause.setImageResource(R.drawable.ic_pause_white);
            } else {
                pp.setImageResource(R.drawable.ic_play_white);
                PlayPause.setImageResource(R.drawable.ic_play_white);
            }
            Bitmap newImage;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 4;
            newImage = BitmapFactory.decodeFile(Main.songs.getAlbumArt(Main.musicService.currentSong));
            if (newImage != null)
            aa.setImageBitmap(newImage);
            else aa.setImageResource(R.drawable.ic_cancel_dark);
        }

    }

    private void setControllListeners() {


        if (Main.musicService.isShuffle())
            shuffletoggle.setImageResource(R.drawable.ic_shuffle_on_white);
        else shuffletoggle.setImageResource(R.drawable.ic_shuffle_off_white);


        if (Main.musicService.isRepeat())
            repeatToggle.setImageResource(R.drawable.ic_repeat_on_white);
        else repeatToggle.setImageResource(R.drawable.ic_repeat_off_white);


        shuffletoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.toggleShuffle();
                if (Main.musicService.isShuffle())
                    shuffletoggle.setImageResource(R.drawable.ic_shuffle_on_white);
                else shuffletoggle.setImageResource(R.drawable.ic_shuffle_off_white);

            }
        });
        previousSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPrevious();
            }
        });
        PlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.togglePlayback();
                if (Main.musicService.isPaused()) {
                    PlayPause.setImageResource(R.drawable.ic_play_white);
                    pp.setImageResource(R.drawable.ic_play_white);
                } else {
                    PlayPause.setImageResource(R.drawable.ic_pause_white);
                    pp.setImageResource(R.drawable.ic_pause_white);
                }
            }
        });
        nextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNext();
            }
        });
        repeatToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.toggleRepeat();
                if (Main.musicService.isRepeat())
                    repeatToggle.setImageResource(R.drawable.ic_repeat_on_white);
                else repeatToggle.setImageResource(R.drawable.ic_repeat_off_white);
            }
        });

        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.togglePlayback();
                if (Main.musicService.isPaused()) {
                    PlayPause.setImageResource(R.drawable.ic_play_white);
                    pp.setImageResource(R.drawable.ic_play_white);
                } else {
                    PlayPause.setImageResource(R.drawable.ic_pause_white);
                    pp.setImageResource(R.drawable.ic_pause_white);
                }
            }
        });
    }

    private void prepareSeekBar() {

        barVisualss.setColor(ContextCompat.getColor(this, R.color.gray));
        barVisualss.setDensity(70);
        barVisualss.setPlayer(getAudioSessionId());


        circularSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                if (fromUser)
                    seekTo(progress);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });


        circularSeekBar.setMax((int) Main.musicService.currentSong.getDuration());
        final Handler handler = new Handler();
        PlayingNow.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isPlaying())
                    circularSeekBar.setProgress(getCurrentPosition());
                handler.postDelayed(this, 1);
            }
        });

        workOnImages();
    }

    private void workOnImages() {
        File path = null;
        if (Main.songs.getAlbumArt(Main.musicService.currentSong) != null)
            path = new File(Main.songs.getAlbumArt(Main.musicService.currentSong));
        Bitmap bitmap;
        if (path != null && path.exists()) {
            bitmap = BitmapFactory.decodeFile(path.getAbsolutePath());
        } else bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_cancel_dark);
        centreimage.setImageBitmap(bitmap);
        Bitmap blurredBitmap = blurMyImage(bitmap);
        blurimage.setImageBitmap(blurredBitmap);
    }

    private Bitmap blurMyImage(Bitmap image) {
        if (null == image) return null;

        Bitmap bitmaplol = image.copy(image.getConfig(), true);
        RenderScript renderScript = RenderScript.create(this);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, bitmaplol);

//Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(bitmaplol);
        renderScript.destroy();
        return bitmaplol;

    }


    /**
     * Shows a Dialog asking the user for a new Playlist name,
     * creating it if so possible.
     */
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


    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        else super.onBackPressed();

    }

    /**
     * Another Activity is taking focus. (either from user going to another
     * Activity or home)
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(changeSongBR);
        barVisualss.release();
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
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        if (paused) {
            paused = false;
        }

        // Scroll the list view to the current song.
        if (Main.settings.get("scroll_on_focus", true))
            songListView.setSelection(Main.musicService.currentSongPosition);

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

        // Scroll the list view to the current song.
        songListView.setSelection(position);

        Main.musicService.playSong();

        if (playbackPaused) {
            playbackPaused = false;
        }
        onResume();
    }

    /**
     * When the user long clicks a music inside the "Now Playing List".
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {

        Toast.makeText(this, Main.musicService.getSong(position).getGenre(),
                Toast.LENGTH_LONG).show();

        // Just a catch - if we return `false`, when an user
        // long clicks an item, the list will react as if
        // we've long clicked AND clicked.
        //
        // So by returning `false`, it will call both
        // `onItemLongClick` and `onItemClick`!
        return true;
    }
}
