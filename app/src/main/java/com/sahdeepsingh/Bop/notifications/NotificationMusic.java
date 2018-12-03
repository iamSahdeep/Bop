package com.sahdeepsingh.Bop.notifications;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.sahdeepsingh.Bop.Activities.MainScreen;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;

//Copied from KMP

/**
 * Specific way to stick an on-going message on the system
 * with the current song I'm playing.
 * <p>
 * This is a rather complicated set of functions because
 * it interacts with a great deal of the Android API.
 * Read with care.
 * <p>
 * Thanks:
 * <p>
 * - Gave me a complete example on how to add a custom
 * action to a button click on the Notification:
 * http://stackoverflow.com/a/21927248
 */
public class NotificationMusic extends NotificationSimple {

    /**
     * Reference to the context that notified.
     */
    private Context context = null;

    /**
     * Reference to the service we're attached to.
     */
    private Service service = null;

    /**
     * Used to create and update the same notification.
     */
    private NotificationCompat.Builder notificationBuilder;

    /**
     * Custom appearance of the notification, also updated.
     */
    private RemoteViews notificationView = null;

    /**
     * Used to actually broadcast the notification.
     * Depends on the Activity that originally called
     * the notification.
     */
    private NotificationManager notificationManager = null;

    /**
     * Cancels all sent notifications.
     */
    public static void cancelAll(Context c) {
        NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.cancelAll();
    }

    /**
     * Sends a system notification with a song's information.
     * <p>
     * If the user clicks the notification, will be redirected
     * to the "Now Playing" Activity.
     * <p>
     * If the user clicks on any of the buttons inside it,
     * custom actions will be executed on the
     * `NotificationButtonHandler` class.
     *
     * @param context Activity that calls this function.
     * @param service Service that calls this function.
     *                Required so the Notification can
     *                run on the background.
     * @param song    Song which we'll display information.
     * @note By calling this function multiple times, it'll
     * update the old notification.
     */
    public void notifySong(Context context, Service service, Song song) {

        if (this.context == null)
            this.context = context;
        if (this.service == null)
            this.service = service;


        // Intent that launches the "Now Playing" Activity
        Intent notifyIntent = new Intent(context, MainScreen.class);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Letting the Intent be executed later by other application.
        PendingIntent pendingIntent = PendingIntent.getActivity
                (context,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


        // Setting our custom appearance for the notification
        notificationView = new RemoteViews(Main.packageName, R.layout.notification);

        // Manually settings the buttons and text
        // (ignoring the defaults on the XML)
        notificationView.setImageViewResource(R.id.pauseNoti, R.drawable.ic_pause);
        notificationView.setImageViewResource(R.id.skipNoti, R.drawable.ic_skip);
        notificationView.setImageViewResource(R.id.stopNoti, R.drawable.ic_cancel);
        notificationView.setTextViewText(R.id.songNameNoti, song.getTitle());
        notificationView.setTextViewText(R.id.ArtistNameNoti, song.getArtist());
        Bitmap newImage;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 5;
        newImage = BitmapFactory.decodeFile(Main.songs.getAlbumArt(Main.musicService.currentSong));
        if (newImage != null)
            notificationView.setImageViewBitmap(R.id.albumArtNoti, newImage);
        else notificationView.setImageViewResource(R.id.albumArtNoti, R.mipmap.ic_launcher);

        // On the notification we have three buttons - Play, close and Skip
        // Here we make sure the class `NotificationButtonHandler`
        // gets called when user selects one of those.
        //
        // First, building the play button and attaching it.
        Intent buttonPlayIntent = new Intent(context, NotificationPlayButtonHandler.class);
        buttonPlayIntent.putExtra("action", "togglePause");

        PendingIntent buttonPlayPendingIntent = PendingIntent.getBroadcast(context, 0, buttonPlayIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.pauseNoti, buttonPlayPendingIntent);

        // And now, building and attaching the Skip button.
        Intent buttonSkipIntent = new Intent(context, NotificationSkipButtonHandler.class);
        buttonSkipIntent.putExtra("action", "skip");

        PendingIntent buttonSkipPendingIntent = PendingIntent.getBroadcast(context, 0, buttonSkipIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.skipNoti, buttonSkipPendingIntent);

        // And now, building and attaching the cancel button.
        Intent buttonStopIntent = new Intent(context, NotificationStopButtonHandler.class);
        buttonStopIntent.putExtra("action", "stop");

        PendingIntent buttonStopPendingIntent = PendingIntent.getBroadcast(context, 0, buttonStopIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.stopNoti, buttonStopPendingIntent);

        // The id of the channel.
        String id = "Bop-MusicPlayer";
        // Finally... Actually creating the Notification
        notificationBuilder = new NotificationCompat.Builder(context, id);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // The user-visible name of the channel.
        CharSequence name = "bop";

        // The user-visible description of the channel.
        String description = "music-player";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel;
            mChannel = new NotificationChannel(id, name, importance);

            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationBuilder.setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setOngoing(true)
                .setStyle(new NotificationCompat.BigPictureStyle())
                .setCustomContentView(notificationView)
                .setChannelId(id)
                .setCustomBigContentView(notificationView);

        // Sets the notification to run on the foreground.
        Notification notification = notificationBuilder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
        service.startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * Updates the Notification icon if the music is paused.
     */
    public void notifyPaused(boolean isPaused) {
        if ((notificationView == null) || (notificationBuilder == null))
            return;

        int iconID = ((Main.musicService.isPaused()) ?
                R.drawable.ic_play :
                R.drawable.ic_pause);

        notificationView.setImageViewResource(R.id.pauseNoti, iconID);

        notificationBuilder.setContent(notificationView);

        // Sets the notification to run on the foreground.
        service.startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    /**
     * Cancels this notification.
     */
    public void cancel() {
        service.stopForeground(true);

        notificationManager.cancel(NOTIFICATION_ID);
    }

    /**
     * Called when user clicks the "play/pause" button on the on-going system Notification.
     */
    public static class NotificationPlayButtonHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Main.musicService.notifyCurrentSong();
            Main.musicService.togglePlayback();
        }
    }

    /**
     * Called when user clicks the "skip" button on the on-going system Notification.
     */
    public static class NotificationSkipButtonHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Main.musicService.next(true);
            Main.musicService.playSong();
        }
    }

    /**
     * When user clicks the "cancel" button
     */
    public static class NotificationStopButtonHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Main.musicService.removedFromNotification();
        }
    }
}