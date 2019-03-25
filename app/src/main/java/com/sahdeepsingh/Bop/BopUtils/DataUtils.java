package com.sahdeepsingh.Bop.BopUtils;

import com.sahdeepsingh.Bop.SongData.Song;

import java.util.ArrayList;
import java.util.Collections;

public class DataUtils {
    private static ArrayList<Song> songs = SongUtils.getSongs();

    /**
     * Returns an alphabetically sorted list with all the
     * artists of the scanned data.
     *
     * @note This method might take a while depending on how
     * many data you have.
     */
    public static ArrayList<String> getArtists() {

        ArrayList<String> artists = new ArrayList<String>();

        for (Song song : songs) {
            String artist = song.getArtist();

            if ((artist != null) && (!artists.contains(artist)))
                artists.add(artist);
        }

        // Making them alphabetically sorted
        Collections.sort(artists);

        return artists;
    }

    /**
     * Returns an alphabetically sorted list with all the
     * albums of the scanned data.
     *
     * @note This method might take a while depending on how
     * many data you have.
     */
    public static ArrayList<String> getAlbums() {

        ArrayList<String> albums = new ArrayList<String>();

        for (Song song : songs) {
            String album = song.getAlbum();

            if ((album != null) && (!albums.contains(album)))
                albums.add(album);
        }

        // Making them alphabetically sorted
        Collections.sort(albums);

        return albums;
    }

    /**
     * Returns an alphabetically sorted list with all
     * existing genres on the scanned data.
     */
    public static ArrayList<String> getGenres() {

        ArrayList<String> genres = new ArrayList<String>();

        for (Song song : songs) {
            String genre = song.getGenre();

            if ((genre != null) && (!genres.contains(genre)))
                genres.add(genre);
        }

        Collections.sort(genres);

        return genres;
    }

    /**
     * Returns a list with all years your data have.
     *
     * @note It is a list of Strings. To access the
     * years, do a `Integer.parseInt(string)`.
     */
    public static ArrayList<String> getYears() {

        ArrayList<String> years = new ArrayList<String>();

        for (Song song : songs) {
            String year = Integer.toString(song.getYear());

            if ((Integer.parseInt(year) > 0) && (!years.contains(year)))
                years.add(year);
        }

        // Making them alphabetically sorted
        Collections.sort(years);

        return years;
    }
}
