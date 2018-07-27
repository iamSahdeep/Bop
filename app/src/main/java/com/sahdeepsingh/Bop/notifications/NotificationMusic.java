package com.sahdeepsingh.Bop.notifications;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.ui.PlayingNow;
import com.squareup.picasso.Picasso;

/**
 * Specific way to stick an on-going message on the system
 * with the current song I'm playing.
 *
 * This is a rather complicated set of functions because
 * it interacts with a great deal of the Android API.
 * Read with care.
 *
 * Thanks:
 *
 * - Gave me a complete example on how to add a custom
 *   action to a button click on the Notification:
 *   http://stackoverflow.com/a/21927248
 */
public class NotificationMusic extends NotificationSimple {

    /**
     * Reference to the context that notified.
     */
    Context context = null;

    /**
     * Reference to the service we're attached to.
     */
    Service service = null;

    /**
     * Used to create and update the same notification.
     */
    NotificationCompat.Builder notificationBuilder = null;

    /**
     * Custom appearance of the notification, also updated.
     */
    RemoteViews notificationView = null;

    /**
     * Used to actually broadcast the notification.
     * Depends on the Activity that originally called
     * the nofitication.
     */
    NotificationManager notificationManager = null;

    /**
     * Sends a system notification with a song's information.
     *
     * If the user clicks the notification, will be redirected
     * to the "Now Playing" Activity.
     *
     * If the user clicks on any of the buttons inside it,
     * custom actions will be executed on the
     * `NotificationButtonHandler` class.
     *
     * @param context Activity that calls this function.
     * @param service Service that calls this function.
     *                Required so the Notification can
     *                run on the background.
     * @param song    Song which we'll display information.
     *
     * @note By calling this function multiple times, it'll
     *       update the old notification.
     */
    public void notifySong(Context context, Service service, Song song) {

        if (this.context == null)
            this.context = context;
        if (this.service == null)
            this.service = service;



        // Intent that launches the "Now Playing" Activity
        Intent notifyIntent = new Intent(context, PlayingNow.class);
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
        notificationView.setImageViewResource(R.id.pauseNoti, R.drawable.ic_pause_white);
        notificationView.setImageViewResource(R.id.skipNoti, R.drawable.ic_skip_white);
        notificationView.setImageViewResource(R.id.stopNoti,R.drawable.ic_cancel_white);
        notificationView.setTextViewText(R.id.songNameNoti, song.getTitle());
        notificationView.setTextViewText(R.id.ArtistNameNoti, song.getArtist());
        String path = Main.songs.getAlbumArt(song);




        // On the notification we have two buttons - Play and Skip
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

        // And now, building and attaching the Skip button.
        Intent buttonStopIntent = new Intent(context, NotificationStopButtonHandler.class);
        buttonSkipIntent.putExtra("action", "stop");

        PendingIntent buttonStopPendingIntent = PendingIntent.getBroadcast(context, 0, buttonStopIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.stopNoti, buttonStopPendingIntent);




        // Finally... Actually creating the Notification
        notificationBuilder = new NotificationCompat.Builder(context);

        notificationBuilder.setContentIntent(pendingIntent)
               .setSmallIcon(R.drawable.ic_launcher_white)
                .setOngoing(true)
                .setStyle(new NotificationCompat.BigPictureStyle())
                .setCustomContentView(notificationView)
                .setCustomBigContentView(notificationView);

        Notification notification = notificationBuilder.build();

        Picasso.get().load(path).into(notificationView,R.id.albumArtNoti,NOTIFICATION_ID,notification);


        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//		notificationManager.notify(NOTIFICATION_ID, notification);

        // Sets the notification to run on the foreground.
        // (why not the former commented line?)
        service.startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * Called when user clicks the "play/pause" button on the on-going system Notification.
     */
    public static class NotificationPlayButtonHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
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

    public static class NotificationStopButtonHandler extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Main.musicService.onDestroy();
            System.exit(0);
        }
    }

    /**
     * Updates the Notification icon if the music is paused.
     */
    public void notifyPaused(boolean isPaused) {
        if ((notificationView == null) || (notificationBuilder == null))
            return;

        int iconID = ((isPaused)?
                R.drawable.ic_play_white:
                R.drawable.ic_pause_white);

        notificationView.setImageViewResource(R.id.pauseNoti, iconID);

        notificationBuilder.setContent(notificationView);

//		notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

        // Sets the notification to run on the foreground.
        // (why not the former commented line?)
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
     * Cancels all sent notifications.
     */
    public static void cancelAll(Context c) {
        NotificationManager manager = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }
}