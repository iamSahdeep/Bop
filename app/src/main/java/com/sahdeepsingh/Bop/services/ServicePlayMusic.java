package com.sahdeepsingh.Bop.services;

/*
 * This to be done in Service
 * 1. add transport controls
 * 2. lol for now
 * */

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.cleveroad.audiowidget.AudioWidget;
import com.sahdeepsingh.Bop.Handlers.NotificationHandler;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.utils.utils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

public class ServicePlayMusic extends MediaBrowserServiceCompat
        implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {


    public static final String ACTION_CMD = "com.sahdeepsingh.Bop.ACTION_CMD";

    public static final String CMD_NAME = "BopPlayer";

    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    //to handle the click delay if the the headshook is double clicked within 5ms
    static final long CLICK_DELAY = 500;
    //handling the pause event and stop service after 5 min if paused
    private static final int STOP_DELAY = 300000;
    //A static class to handle delay callbacks
    private final DelayedStopHandler mDelayedStopHandler = new DelayedStopHandler(this);

    static long lastClick = 0;


    // The tag we put on debug messages
    final static String TAG = "MusicService";

    /**
     * Token for the interaction between an Activity and this Service.
     */
    private final IBinder musicBind = new MusicBinder();
    /**
     * Index of the current song we're playing on the `songs` list.
     */
    public int currentSongPosition;
    /**
     * Copy of the current song being played (or paused).
     * <p>
     * Use it to get info from the current song.
     */
    public Song currentSong = null;
    /**
     * Tells if this service is bound to an Activity.
     */
    public boolean musicBound = false;
    /**
     * Current state of the Service.
     */
    ServiceState serviceState = ServiceState.Preparing;
    /**
     * Use this to get audio focus:
     * <p>
     * 1. Making sure other music apps don't play
     * at the same time;
     */
    AudioManager audioManager;
    //Just migrated to it to support everything where we are lacking
    public MediaSessionCompat mMediaSessionCompat;

    // Haven't Implemented it yet
    AudioWidget audioWidget;

    /**
     * Android Media Player - we control it in here.
     */
    public MediaPlayer player;
    /**
     * List of songs we're  currently playing.
     */
    private ArrayList<Song> songs;
    /**
     * Flag that indicates whether we're at Shuffle mode.
     */
    private boolean shuffleMode = false;
    /**
     * Random number generator for the Shuffle Mode.
     */
    private Random randomNumberGenerator;
    // 0 single, 1 repeat on , 2 repeat off
    private int repeatMode = 0;
    /**
     * Spawns an on-going notification with our current
     * playing song.
     */
    // private NotificationMusic notification = null;
    NotificationHandler notificationManager;


    private boolean pausedTemporarilyDueToAudioFocus = false;
    private boolean loweredVolumeDueToAudioFocus = false;

    //pausing player if getting noise
    private BroadcastReceiver mNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (player != null && player.isPlaying()) {
                pausePlayer();
            }
        }
    };

    //Handling call backs whenever the controlls are broadcasted, also handling headphones event here
    private MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            final String intentAction = mediaButtonEvent.getAction();
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intentAction)) {
                if (Main.settings.get("pause_headphone_unplugged", true)) {
                    pausePlayer();
                }
            } else if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                final KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (event == null) return super.onMediaButtonEvent(mediaButtonEvent);
                final int keycode = event.getKeyCode();
                final int action = event.getAction();
                final long eventTime = event.getEventTime();
                if (event.getRepeatCount() == 0 && action == KeyEvent.ACTION_DOWN) {
                    switch (keycode) {
                        case KeyEvent.KEYCODE_HEADSETHOOK:
                            if (eventTime - lastClick < CLICK_DELAY) {
                                next(true);
                                playSong();
                                lastClick = 0;
                            } else {
                                if (isPlaying())
                                    pausePlayer();
                                else unpausePlayer();
                                lastClick = eventTime;
                            }
                            break;
                        case KeyEvent.KEYCODE_MEDIA_STOP:
                            stopMusicPlayer();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            if (Main.mainMenuHasNowPlayingItem) {
                                if (isPlaying()) pausePlayer();
                                else unpausePlayer();
                            }
                            break;
                        case KeyEvent.KEYCODE_MEDIA_NEXT:
                            next(true);
                            playSong();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                            previous(true);
                            playSong();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            pausePlayer();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            unpausePlayer();
                            break;
                    }
                }
            }
            return super.onMediaButtonEvent(mediaButtonEvent);
        }

        @Override
        public void onPlay() {
            super.onPlay();
            unpausePlayer();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            playSong();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            seekTo((int) pos);
        }

        @Override
        public void onPause() {
            super.onPause();
            pausePlayer();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            next(true);
            playSong();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            previous(true);
            playSong();
        }

        @Override
        public void onStop() {
            super.onStop();
            stopMusicPlayer();
        }
    };


    //Now the Actual game Begins

    public void onCreate() {
        super.onCreate();

        currentSongPosition = 0;

        randomNumberGenerator = new Random();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        //Haven't added it yet just added to remember
        if (Main.settings.get("showFloatingWidget", true))
            createAudioWidget();

        initMusicPlayer();

        initMediaSession();

        initNoisyReceiver();


        try {
            notificationManager = new NotificationHandler(this);
        } catch (RemoteException e) {
            throw new IllegalStateException("Could not create a NotificationHandler", e);
        }

    }

    private void initNoisyReceiver() {
        //Handles headphones coming unplugged. cannot be done through a manifest receiver
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(mNoisyReceiver, filter);
    }

    private void initMediaSession() {
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "Tag", mediaButtonReceiver, null);

        mMediaSessionCompat.setCallback(mMediaSessionCallback);
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mMediaSessionCompat.setMediaButtonReceiver(pendingIntent);

        setSessionToken(mMediaSessionCompat.getSessionToken());
    }

    private void createAudioWidget() {
        audioWidget = new AudioWidget.Builder(getApplicationContext()).build();
    }

    /**
     * Initializes the Android's internal MediaPlayer.
     */
    public void initMusicPlayer() {
        if (player == null)
            player = new MediaPlayer();

        // Assures the CPU continues running this service
        // even when the device is sleeping.
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // These are the events that will "wake us up"
        player.setOnPreparedListener(this); // player initialized
        player.setOnCompletionListener(this); // song completed
        player.setOnErrorListener(this);

    }

    /**
     * Cleans resources from Android's native MediaPlayer.
     *
     * @note According to the MediaPlayer guide, you should release
     * the MediaPlayer as often as possible.
     * For example, when losing Audio Focus for an extended
     * period of time.
     */
    public void stopMusicPlayer() {
        if (player == null)
            return;

        Main.mainMenuHasNowPlayingItem = false;
        player.stop();
        player.release();
        player = null;
        notificationManager.stopNotification();
        mDelayedStopHandler.removeCallbacksAndMessages(null);

    }

    /**
     * Sets the "Now Playing List"
     */
    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
    }

    /**
     * Appends a song to the end of the currently playing queue.
     *
     * @param song New song to put at the end.
     */
    public void add(Song song) {
        songs.add(song);
    }

    /**
     * Asks the AudioManager for our application to
     * have the audio focus.
     */
    private boolean requestAudioFocus() {

        //Request audio focus for playback
        int result = audioManager.requestAudioFocus(
                this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        //Check if audio focus was granted. If not, stop the service.
        return (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
    }

    /**
     * Does something when the audio focus state changed
     *
     * @note Meaning it runs when we get and when we don't get
     * the audio focus from `#requestAudioFocus()`.
     * <p>
     * For example, when we receive a message, we lose the focus
     * and when the ringer stops playing, we get the focus again.
     * <p>
     * So we must avoid the bug that occurs when the user pauses
     * the player but receives a message - and since after that
     * we get the focus, the player will unpause.
     */
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {

            // Yay, gained audio focus! Either from losing it for
            // a long or short periods of time.
            case AudioManager.AUDIOFOCUS_GAIN:

                if (player == null)
                    initMusicPlayer();

                if (pausedTemporarilyDueToAudioFocus) {
                    pausedTemporarilyDueToAudioFocus = false;
                    unpausePlayer();
                }

                if (loweredVolumeDueToAudioFocus) {
                    loweredVolumeDueToAudioFocus = false;
                    player.setVolume(1.0f, 1.0f);
                }
                break;

            // Damn, lost the audio focus for a (presumable) long time
            case AudioManager.AUDIOFOCUS_LOSS:
                stopMusicPlayer();
                break;

            // Just lost audio focus but will get it back shortly
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:

                if (!isPaused()) {
                    pausePlayer();
                    pausedTemporarilyDueToAudioFocus = true;
                }
                break;

            // Temporarily lost audio focus but I can keep it playing
            // at a low volume instead of stopping completely
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.w(TAG, "audiofocus loss transient can duck");

                player.setVolume(0.1f, 0.1f);
                loweredVolumeDueToAudioFocus = true;
                break;
        }
    }


    /**
     * Called when the music is ready for playback.
     */
    @Override
    public void onPrepared(MediaPlayer mp) {

        serviceState = ServiceState.Playing;

        // Start playback
        player.start();

        mMediaSessionCompat.setActive(true);
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);


        // just crating new notification of current song
        notifyCurrentSong();
    }

    private void setMediaSessionMetaData() {
        mMediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, utils.getBitmapfromAlbumId(getApplicationContext(), currentSong))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentSong.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, currentSong.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentSong.getTitle())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, currentSong.getDurationSeconds())
                .build());
    }

    /**
     * Sets a specific song, already within internal Now Playing List.
     *
     * @param songIndex Index of the song inside the Now Playing List.
     */
    public void setSong(int songIndex) {

        if (songIndex < 0 || songIndex >= songs.size())
            currentSongPosition = 0;
        else
            currentSongPosition = songIndex;
    }

    /**
     * Will be called when the music completes - either when the
     * user presses 'next' or when the music ends or when the user
     * selects another track.
     */
    @Override
    public void onCompletion(MediaPlayer mp) {

        // Keep this state!
        serviceState = ServiceState.Playing;

        // Repeating current song if desired
        if (repeatMode == 0) {
            playSong();
            return;
        }

        // Remember that by calling next(), if played
        // the last song on the list, will reset to the
        // first one.
        next(false);

        // Reached the end, should we restart playing
        // from the first song or simply stop?
        if (currentSongPosition == 0) {
            if (Main.settings.get("repeat_list", true))
                playSong();
            else
                destroySelf();
            return;
        }
        // Common case - skipped a track or anything
        playSong();
    }

    /**
     * If something wrong happens with the MusicPlayer.
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        Log.w(TAG, "onError");
        return false;
    }

    @Override
    public void onDestroy() {

        cancelNotification();

        currentSong = null;

        if (audioManager != null)
            audioManager.abandonAudioFocus(this);

        stopMusicPlayer();
        Log.w(TAG, "onDestroy");

        mDelayedStopHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(mNoisyReceiver);
        super.onDestroy();
    }

    /**
     * Kills the service.
     *
     * @note Explicitly call this when the service is completed
     * or whatnot.
     */
    private void destroySelf() {
        stopSelf();
        currentSong = null;
    }

    // These methods are to be called by the Activity
    // to work on the music-playing.

    /**
     * Jumps to the previous song on the list.
     *
     * @note Remember to call `playSong()` to make the MusicPlayer
     * actually play the music.
     */
    public void previous(boolean userSkippedSong) {
        if (serviceState != ServiceState.Paused && serviceState != ServiceState.Playing)
            return;

        currentSongPosition--;
        if (currentSongPosition < 0)
            currentSongPosition = songs.size() - 1;
    }

    /**
     * Jumps to the next song on the list.
     *
     * @note Remember to call `playSong()` to make the MusicPlayer
     * actually play the music.
     */
    public void next(boolean userSkippedSong) {
        if (serviceState != ServiceState.Paused && serviceState != ServiceState.Playing)
            return;


        if (shuffleMode) {
            int newSongPosition = currentSongPosition;

            while (newSongPosition == currentSongPosition)
                newSongPosition = randomNumberGenerator.nextInt(songs.size());

            currentSongPosition = newSongPosition;
            return;
        }

        currentSongPosition++;

        if (currentSongPosition >= songs.size())
            currentSongPosition = 0;
    }

    public int getPosition() {
        return player.getCurrentPosition();
    }

    public int getDuration() {
        return player.getDuration();
    }

    public boolean isPlaying() {
        boolean returnValue = false;
        if (player != null)
            try {
                returnValue = player.isPlaying();
            } catch (IllegalStateException | NullPointerException e) {

            }

        return returnValue;
    }

    public boolean isPaused() {
        return serviceState == ServiceState.Paused;
    }

    /**
     * Actually plays the song set by `currentSongPosition`.
     */
    public void playSong() {

        if (player == null)
            initMusicPlayer();
        player.reset();
        // Get the song ID from the list, extract the ID and
        // get an URL based on it
        Song songToPlay = songs.get(currentSongPosition);
        Main.songs.addsong_toRecent(getApplicationContext(), songToPlay);
        Main.songs.addcountSongsPlayed(getApplicationContext(), songToPlay);
        currentSong = songToPlay;

        // Append the external URI with our songs'
        Uri songToPlayURI = ContentUris.withAppendedId
                (android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        songToPlay.getId());
        try {
            player.setDataSource(getApplicationContext(), songToPlayURI);
        } catch (IOException io) {
            Log.e(TAG, "IOException: couldn't change the song", io);
            destroySelf();
        } catch (Exception e) {
            Log.e(TAG, "Error when changing the song", e);
            destroySelf();
        }

        //setting meta data for notification and whole session
        setMediaSessionMetaData();

        // Prepare the MusicPlayer asynchronously.
        // When finished, will call `onPrepare`
        player.prepareAsync();

        serviceState = ServiceState.Preparing;

        Log.w(TAG, "play song");
    }


    private void setMediaPlaybackState(int state) {
        PlaybackStateCompat.Builder playbackstateBuilder = new PlaybackStateCompat.Builder();
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE);
        } else {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY);
        }
        playbackstateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mMediaSessionCompat.setPlaybackState(playbackstateBuilder.build());
    }

    public void pausePlayer() {
        if (serviceState != ServiceState.Paused && serviceState != ServiceState.Playing)
            return;

        player.pause();
        serviceState = ServiceState.Paused;

        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);

    }

    public void unpausePlayer() {
        if (serviceState != ServiceState.Paused && serviceState != ServiceState.Playing)
            return;

        player.start();
        serviceState = ServiceState.Playing;

        setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);

    }

    /**
     * Toggles between Pause and Unpause.
     */
    public void togglePlayback() {
        if (serviceState == ServiceState.Paused)
            unpausePlayer();
        else
            pausePlayer();
    }

    public void seekTo(int position) {
        player.seekTo(position);
    }

    /**
     * Toggles the Shuffle mode
     * (if will play songs in random order).
     */
    public void toggleShuffle() {
        shuffleMode = !shuffleMode;
    }

    /**
     * Shuffle mode state.
     *
     * @return If Shuffle mode is on/off.
     */
    public boolean isShuffle() {
        return shuffleMode;
    }

    /**
     * Toggles the Repeat mode
     * (if the current song will play again
     * when completed).
     */
    public void toggleRepeat() {
        if (repeatMode == 2)
            repeatMode = 0;
        else repeatMode += 1;

    }

    /**
     * Repeat mode state.
     *
     * @return If Repeat mode is on/off.
     */
    public int isRepeat() {
        return repeatMode;
    }

    // THESE ARE METHODS RELATED TO CONNECTING THE SERVICE
    // TO THE ANDROID PLATFORM
    // NOTHING TO DO WITH MUSIC-PLAYING

    /**
     * Called when the Service is finally bound to the app.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }

        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }


    /**
     * Called when the Service is unbound - user quitting
     * the app or something.
     */
    @Override
    public boolean onUnbind(Intent intent) {

        return false;
    }

    /**
     * Displays a notification on the status bar with the
     * current song and some nice buttons.
     */
    public void notifyCurrentSong() {

        if (currentSong == null)
            return;

        if (!requestAudioFocus()) {
            //Stop the service.
            stopSelf();
            Toast.makeText(getApplicationContext(), "FUCK", Toast.LENGTH_LONG).show();
            return;
        }

        notificationManager.startNotification();


    }


    public void cancelNotification() {
        notificationManager.stopNotification();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String action = intent.getAction();
            String command = intent.getStringExtra(CMD_NAME);
            if (ACTION_CMD.equals(action)) {
                if (ACTION_PAUSE.equals(command)) {
                    pausePlayer();
                }
            } else {
                MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);
            }
        }
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);

        return START_STICKY;
    }

    public int getAudioSession() {
        if (player == null)
            return 0;
        else return player.getAudioSessionId();
    }

    /**
     * Possible states this Service can be on.
     */
    enum ServiceState {

        Preparing,

        // Playback active - media player ready!
        // (but the media player may actually be paused in
        // this state if we don't have audio focus).
        Playing,

        // So that we know we have to resume playback once we get focus back)
        // playback paused (media player ready!)
        Paused
    }

    public class MusicBinder extends Binder {
        public ServicePlayMusic getService() {
            return ServicePlayMusic.this;
        }
    }


    /**
     * A simple handler that stops the service if playback is not active (playing)
     */
    private static class DelayedStopHandler extends Handler {//延迟停止服务handler
        private final WeakReference<ServicePlayMusic> mWeakReference;

        private DelayedStopHandler(ServicePlayMusic service) {
            mWeakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            ServicePlayMusic service = mWeakReference.get();
            if (service != null && Main.mainMenuHasNowPlayingItem) {
                if (service.isPlaying()) {
                    return;
                }
                service.stopMusicPlayer();
            }
        }
    }

}
