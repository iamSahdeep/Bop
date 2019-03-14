package com.sahdeepsingh.Bop.notifications;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.sahdeepsingh.Bop.Activities.MainScreen;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.services.ServicePlayMusic;
import com.sahdeepsingh.Bop.utils.utils;

import androidx.core.app.NotificationCompat;

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
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel;
            mChannel = new NotificationChannel(id, name, importance);

            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationBuilder.setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setShowWhen(false)
                .setOngoing(true)
                .setColor(0x0000000)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(Main.musicService.mMediaSessionCompat.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(retreivePlaybackAction(4)))
                .setChannelId(id)
                .setLargeIcon(utils.getBitmapfromAlbumId(context, song))
                .setContentText(song.getArtist())
                .setContentInfo(song.getAlbum())
                .setContentTitle(song.getTitle())
                .addAction(R.drawable.ic_previous, "prev", retreivePlaybackAction(3))
                .addAction(R.drawable.ic_pause, "pause", retreivePlaybackAction(1))
                .addAction(R.drawable.ic_skip, "next", retreivePlaybackAction(2));

        // Sets the notification to run on the foreground.
        Notification notification = notificationBuilder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
        service.startForeground(NOTIFICATION_ID, notification);
    }

    private PendingIntent retreivePlaybackAction(int which) {
        Intent action;
        PendingIntent pendingIntent;
        final ComponentName serviceName = new ComponentName(context, ServicePlayMusic.class);
        switch (which) {
            case 1:
                // Play and pause
                action = new Intent("togglePause");
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(context, 1, action, 0);
                return pendingIntent;
            case 2:
                // Skip tracks
                action = new Intent("skip");
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(context, 2, action, 0);
                return pendingIntent;
            case 3:
                // Previous tracks
                action = new Intent("previous");
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(context, 3, action, 0);
                return pendingIntent;
            case 4:
                action = new Intent("stop");
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(context, 4, action, 0);
                return pendingIntent;
            default:
                break;
        }
        return null;
    }
    /**
     * Updates the Notification icon if the music is paused.
     */
    public void notifyPaused(boolean isPaused) {

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

}