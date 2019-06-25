package com.sahdeepsingh.Bop.SongData;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

// KMP <3

/**
 * Global interface to all the data this application can see.
 * <p>
 * Tasks:
 * - Scans for data on the device
 * (both internal and external memories)
 * - Has query functions to data and their attributes.
 * <p>
 * Thanks:
 * <p>
 * - Showing me how to get a music's full PATH:
 * http://stackoverflow.com/a/21333187
 * <p>
 * - Teaching me the queries to get Playlists
 * and their data:
 * http://stackoverflow.com/q/11292125
 */
public class Data {

    /**
     * Big list with all the Songs found.
     */
    public ArrayList<Song> songs = new ArrayList<>();

    /**
     * Big list with all the Playlists found.
     */
    public ArrayList<Playlist> playlists = new ArrayList<>();

    /**
     * Maps song's genre IDs to song's genre names.
     *
     * @note It's only available after calling `scanSongs`.
     */
    private HashMap<String, String> genreIdToGenreNameMap = new HashMap<>();

    /**
     * Maps song's IDs to song genre IDs.
     *
     * @note It's only available after calling `scanSongs`.
     */
    private HashMap<String, String> songIdToGenreIdMap = new HashMap<>();

    /**
     * Flag that tells if successfully scanned all data.
     */
    private boolean scannedSongs;

    /**
     * Flag that tells if we're scanning data right now.
     */
    private boolean scanningSongs;

    private ContentResolver resolver;

    /**
     * Tells if we've successfully scanned all data on
     * the device.
     * <p>
     * This will return `false` both while we're scanning
     * for data and if some error happened while scanning.
     */
    public boolean isInitialized() {
        return scannedSongs;
    }

    /**
     * Tells if we're currently scanning data on the device.
     */
    public boolean isScanning() {
        return scanningSongs;
    }

    /**
     * Scans the device for data.
     * <p>
     * This function takes a lot of time to execute and
     * blocks the program UI.
     * So you should call it on a separate thread and
     * query `isInitialized` when needed.
     * <p>
     * Inside it, we make a lot of queries to the system's
     * databases - getting data, genres and playlists.
     *
     * @param c         The current Activity's Context.
     * @param fromWhere Where should we scan for data.
     *                  <p>
     *                  Accepted values to `fromWhere` are:
     *                  - "internal" To scan for data on the phone's memory.
     *                  - "external" To scan for data on the SD card.
     *                  - "both"     To scan for data anywhere.
     * @note If you call this function twice, it rescans
     * the data, refreshing internal lists.
     * It doesn't add up data.
     */
    public void scanSongs(Context c, String fromWhere) {


        if (scanningSongs)
            return;
        scanningSongs = true;

        destroy();
        updateGenres(c, fromWhere);
        updateSongs(c, fromWhere);
        updatePlaylists(c, fromWhere);

        scannedSongs = true;
        scanningSongs = false;

    }

    public void updateSongs(Context context, String fromWhere) {

        //data.clear();
        resolver = context.getContentResolver();
        Cursor cursor;
        Uri musicUri = ((fromWhere.equals("internal")) ?
                android.provider.MediaStore.Audio.Media.INTERNAL_CONTENT_URI :
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

        String SONG_ID = android.provider.MediaStore.Audio.Media._ID;
        String SONG_TITLE = android.provider.MediaStore.Audio.Media.TITLE;
        String SONG_ARTIST = android.provider.MediaStore.Audio.Media.ARTIST;
        String SONG_ALBUM = android.provider.MediaStore.Audio.Media.ALBUM;
        String SONG_YEAR = android.provider.MediaStore.Audio.Media.YEAR;
        String SONG_TRACK_NO = android.provider.MediaStore.Audio.Media.TRACK;
        String SONG_FILEPATH = android.provider.MediaStore.Audio.Media.DATA;
        String SONG_DURATION = android.provider.MediaStore.Audio.Media.DURATION;
        String SONG_ALBUM_ID = MediaStore.Audio.Media.ALBUM_ID;

        String[] columns = {
                SONG_ID,
                SONG_TITLE,
                SONG_ARTIST,
                SONG_ALBUM,
                SONG_ALBUM_ID,
                SONG_YEAR,
                SONG_TRACK_NO,
                SONG_FILEPATH,
                SONG_DURATION
        };

        // Thing that limits results to only show music files.
        //
        // It's a SQL "WHERE" clause - it becomes `WHERE IS_MUSIC=1`.
        //
        // (note: using `IS_MUSIC!=0` takes a f#$%load of time)
        final String musicsOnly = MediaStore.Audio.Media.IS_MUSIC + "=1";

        // Actually querying the system
        cursor = resolver.query(musicUri, columns, musicsOnly, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // NOTE: I tried to use MediaMetadataRetriever, but it was too slow.
            //       Even with 10 data, it took like 13 seconds,
            //       No way I'm releasing it this way - I have like 4.260 data!

            do {
                // Creating a song from the values on the row
                Song song = new Song(cursor.getInt(cursor.getColumnIndex(SONG_ID)),
                        cursor.getString(cursor.getColumnIndex(SONG_FILEPATH)));

                song.setTitle(cursor.getString(cursor.getColumnIndex(SONG_TITLE)));
                song.setArtist(cursor.getString(cursor.getColumnIndex(SONG_ARTIST)));
                song.setAlbum(cursor.getString(cursor.getColumnIndex(SONG_ALBUM)));
                song.setYear(cursor.getInt(cursor.getColumnIndex(SONG_YEAR)));
                song.setTrackNumber(cursor.getInt(cursor.getColumnIndex(SONG_TRACK_NO)));
                song.setDuration(cursor.getInt(cursor.getColumnIndex(SONG_DURATION)));
                song.setAlbumid(cursor.getString(cursor.getColumnIndex(SONG_ALBUM_ID)));

                // Using the previously created genre maps
                // to fill the current song genre.
                String currentGenreID = songIdToGenreIdMap.get(Long.toString(song.getId()));
                String currentGenreName = genreIdToGenreNameMap.get(currentGenreID);
                song.setGenre(currentGenreName);

                // Adding the song to the global list
                //We will check of the album art otherwise wont add just remove in future after solution
                //if (getAlbumArt(song) != null)
                songs.add(song);

            }
            while (cursor.moveToNext());
        } else {
            // What do I do if I can't find any data?
        }
        if (cursor != null) {
            cursor.close();
        }

        // Finally, let's sort the song list alphabetically
        // based on the song title.

        Collections.sort(songs, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }

    public void updatePlaylists(Context context, String fromWhere) {

        // Alright, now I'll get all the Playlists.
        // First I grab all playlist IDs and Names and then for each
        // one of those, getting all data inside them.

        // As you know, the columns for the database.
        playlists.clear();

        String PLAYLIST_ID = MediaStore.Audio.Playlists._ID;
        String PLAYLIST_NAME = MediaStore.Audio.Playlists.NAME;
        String PLAYLIST_SONG_ID = MediaStore.Audio.Playlists.Members.AUDIO_ID;

        // This is what I'll get for all playlists.
        String[] playlistColumns = {
                PLAYLIST_ID,
                PLAYLIST_NAME
        };
        Uri playlistUri = ((fromWhere.equals("internal")) ?
                android.provider.MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI :
                android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI);
        resolver = context.getContentResolver();
        Cursor cursor;
        final String musicsOnly = MediaStore.Audio.Media.IS_MUSIC + "=1";

        // The actual query - takes a while.
        cursor = resolver.query(playlistUri, playlistColumns, null, null, null);

        // Going through all playlists, creating my class and populating
        // it with all the song IDs they have.
        assert cursor != null;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            Playlist playlist = new Playlist(cursor.getLong(cursor.getColumnIndex(PLAYLIST_ID)),
                    cursor.getString(cursor.getColumnIndex(PLAYLIST_NAME)));

            // For each playlist, get all song IDs
            Uri currentUri = MediaStore.Audio.Playlists.Members.getContentUri(fromWhere, playlist.getID());
            Cursor cursor2;
            cursor2 = resolver.query(currentUri,
                    new String[]{PLAYLIST_SONG_ID},
                    musicsOnly,
                    null, null);

            // Adding each song's ID to it
            assert cursor2 != null;
            for (cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext()) {
                playlist.add(cursor2.getLong(cursor2.getColumnIndex(PLAYLIST_SONG_ID)));
            }

            if (!playlist.getSongIds().isEmpty())
                playlists.add(playlist);
            cursor2.close();
        }

        Collections.sort(playlists, new Comparator<Playlist>() {
            public int compare(Playlist a, Playlist b) {
                return a.getName().compareTo(b.getName());
            }
        });

    }

    public void updateGenres(Context context, String fromWhere) {

        //genreIdToGenreNameMap.clear();
        Cursor cursor;

        resolver = context.getContentResolver();
        Uri genreUri = ((fromWhere.equals("internal")) ?
                android.provider.MediaStore.Audio.Genres.INTERNAL_CONTENT_URI :
                android.provider.MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI);

        String GENRE_ID = MediaStore.Audio.Genres._ID;
        String GENRE_NAME = MediaStore.Audio.Genres.NAME;
        String SONG_ID = android.provider.MediaStore.Audio.Media._ID;
        // Creating the map  "Genre IDs" -> "Genre Names"
        genreIdToGenreNameMap = new HashMap<>();

        // This is what we'll ask of the genres
        String[] genreColumns = {
                GENRE_ID,
                GENRE_NAME
        };

        // Actually querying the genres database
        cursor = resolver.query(genreUri, genreColumns, null, null, null);

        // Iterating through the results and filling the map.
        assert cursor != null;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            genreIdToGenreNameMap.put(cursor.getString(0), cursor.getString(1));

        cursor.close();

        // Map from Songs IDs to Genre IDs
        songIdToGenreIdMap = new HashMap<>();

        // UPDATE URI HERE
        if (fromWhere.equals("both"))
            throw new RuntimeException("Can't scan from both locations - not implemented");

        // For each genre, we'll query the databases to get
        // all data's IDs that have it as a genre.
        for (String genreID : genreIdToGenreNameMap.keySet()) {

            Uri uri = MediaStore.Audio.Genres.Members.getContentUri(fromWhere,
                    Long.parseLong(genreID));

            cursor = resolver.query(uri, new String[]{SONG_ID}, null, null, null);

            // Iterating through the results, populating the map
            assert cursor != null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                long currentSongID = cursor.getLong(cursor.getColumnIndex(SONG_ID));

                songIdToGenreIdMap.put(Long.toString(currentSongID), genreID);
            }
            cursor.close();
        }

    }
    public void destroy() {
        songs.clear();
    }
}