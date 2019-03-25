package com.sahdeepsingh.Bop.BopUtils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.sahdeepsingh.Bop.SongData.Playlist;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;

import java.util.ArrayList;
import java.util.Collections;

public class PlaylistUtils {

    /**
     * Creates a new Playlist.
     *
     * @param c          Activity on which we're creating.
     * @param fromWhere  "internal" or "external".
     * @param name       Playlist name.
     * @param songsToAdd List of song IDs to place on it.
     */
    public static void newPlaylist(Context c, String fromWhere, String name, ArrayList<Song> songsToAdd) {

        // CHECK IF PLAYLIST EXISTS!

        if (getPlaylistNames().contains(name)) {
            addSongsToplaylist(c, name, songsToAdd);
            return;
        }


        ContentResolver resolver = c.getContentResolver();

        Uri playlistUri = ((fromWhere.equals("internal")) ?
                android.provider.MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI :
                android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI);

        // Setting the new playlists' values
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.NAME, name);
        values.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());

        // Actually inserting the new playlist.
        Uri newPlaylistUri = resolver.insert(playlistUri, values);

        //Okay its strange that sometimes newPlaylistUri is null, It means playlist is already there, So i am deleting the playlist and creating new
        if (newPlaylistUri == null) {
            deletePlaylist(c, name);
            newPlaylistUri = resolver.insert(playlistUri, values);
        }

        // Getting the new Playlist ID
        String PLAYLIST_ID = MediaStore.Audio.Playlists._ID;
        String PLAYLIST_NAME = MediaStore.Audio.Playlists.NAME;

        // This is what I'll get for all playlists.
        String[] playlistColumns = {
                PLAYLIST_ID,
                PLAYLIST_NAME
        };

        // The actual query - takes a while.
        Cursor cursor = resolver.query(playlistUri, playlistColumns, null, null, null);

        long playlistID = 0;

        // Going through all playlists, creating my class and populating
        // it with all the song IDs they have.
        assert cursor != null;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            if (name.equals(cursor.getString(cursor.getColumnIndex(PLAYLIST_NAME))))
                playlistID = cursor.getLong(cursor.getColumnIndex(PLAYLIST_ID));
        cursor.close();
        // Now, to it's data
        Uri songUri = Uri.withAppendedPath(newPlaylistUri, MediaStore.Audio.Playlists.Members.CONTENT_DIRECTORY);
        int songOrder = 1;

        for (Song song : songsToAdd) {

            ContentValues songValues = new ContentValues();

            songValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, song.getId());
            songValues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, songOrder);

            resolver.insert(songUri, songValues);
            songOrder++;
        }

        // Finally, we're updating our internal list of Playlists
        Playlist newPlaylist = new Playlist(playlistID, name);

        for (Song song : songsToAdd)
            newPlaylist.add(song.getId());

        Main.data.playlists.add(newPlaylist);
    }

    public static ArrayList<String> getPlaylistNames() {

        ArrayList<String> names = new ArrayList<String>();

        for (Playlist playlist : Main.data.playlists)
            names.add(playlist.getName());

        return names;
    }

    public static ArrayList<Song> getSongsByPlaylist(String playlistName) {

        ArrayList<Long> songIDs = new ArrayList<>();
        for (Playlist playlist : Main.data.playlists) {
            if (playlist.getName().equals(playlistName)) {
                songIDs = playlist.getSongIds();
                break;
            }
        }

        ArrayList<Song> currentSongs = new ArrayList<Song>();
        if (songIDs != null)
            for (Long songID : songIDs)
                currentSongs.add(SongUtils.getSongById(songID));
        Collections.sort(currentSongs, (song, t1) -> song.getTitle().compareTo(t1.getTitle()));
        return currentSongs;
    }

    /* Deleting a Playlist*/
    public static void deletePlaylist(Context context, String selectedplaylist) {
        String playlistid = getPlayListId(context, selectedplaylist);
        ContentResolver resolver = context.getContentResolver();
        String where = MediaStore.Audio.Playlists._ID + "=?";
        String[] whereVal = {playlistid};
        resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, where, whereVal);
        Main.data.updatePlaylists(context, "external");
    }

    // Getting Playlist unique ID
    public static String getPlayListId(String selectedplaylist) {
        String id = "";
        if (getPlaylistNames().contains(selectedplaylist)) {
            for (Playlist playlist : Main.data.playlists) {
                if (selectedplaylist.equals(playlist.getName()))
                    id = String.valueOf(playlist.getID());
            }
        }
        return id;
    }

    public static String getPlayListId(Context c, String playlist) {

        //  read this record and get playlistid

        Uri newuri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

        final String playlistid = MediaStore.Audio.Playlists._ID;

        final String playlistname = MediaStore.Audio.Playlists.NAME;

        String where = MediaStore.Audio.Playlists.NAME + "=?";

        String[] whereVal = {playlist};

        String[] projection = {playlistid, playlistname};

        ContentResolver resolver = c.getContentResolver();

        Cursor record = resolver.query(newuri, projection, where, whereVal, null);

        int recordcount = record.getCount();

        String foundplaylistid = "";

        if (recordcount > 0) {
            record.moveToFirst();

            int idColumn = record.getColumnIndex(playlistid);

            foundplaylistid = record.getString(idColumn);

            record.close();
        }

        return foundplaylistid;
    }

    /* Renaming  Playlist */
    public static void renamePlaylist(Context context, String newplaylist, long playlist_id) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        String where = MediaStore.Audio.Playlists._ID + " =? ";
        String[] whereVal = {Long.toString(playlist_id)};
        values.put(MediaStore.Audio.Playlists.NAME, newplaylist);
        resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values, where, whereVal);
        Toast.makeText(context, "Renamed, Changes might take some time", Toast.LENGTH_SHORT).show();
    }

    /* Delete single song from Playlist*/
    public static void deletePlaylistTrack(Context context, String name, long audioId) {
        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.parseLong(getPlayListId(name)));
        final ContentResolver resolver = context.getContentResolver();
        resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID + " = ? ", new String[]{
                Long.toString(audioId)
        });
        for (Playlist p :
                Main.data.playlists) {
            if (p.getName().equals(name)) {
                p.removeSong(audioId);
            }
        }
    }

    /* Add single song to Playlist */
    public static void addToPlaylist(ContentResolver resolver, long playlistid, long audioId) {

        String[] cols = new String[]{
                "count(*)"
        };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        assert cur != null;
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, (int) (base + audioId));
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
        resolver.insert(uri, values);
    }

    /* Add Multiple Songs in the Playlist */
    public static void addSongsToplaylist(Context c, String name, ArrayList<Song> songsToAdd) {
        long playlistID = Long.parseLong(getPlayListId(name));
        for (Song s : songsToAdd
        ) {
            addToPlaylist(c.getContentResolver(), playlistID, s.getId());
        }
        Toast.makeText(c, "Added, Changes might take some time", Toast.LENGTH_SHORT).show();
    }
}
