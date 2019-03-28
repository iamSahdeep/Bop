package com.sahdeepsingh.Bop.fragments;


import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahdeepsingh.Bop.BopUtils.ExtraUtils;
import com.sahdeepsingh.Bop.BopUtils.SongUtils;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.squareup.picasso.Picasso;

import androidx.fragment.app.Fragment;

public class SongDetailsFragment extends Fragment {

    private static final String Songparam = "param1";
    TextView share, delete, makeRingtone, addtoPlaylist, songname, songgame1, album, artist, year, duration, location;
    ImageView albumart;
    private Song song;


    public SongDetailsFragment() {
        // Required empty public constructor
    }

    public static SongDetailsFragment newInstance(Long songid) {
        SongDetailsFragment fragment = new SongDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(Songparam, songid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Long s = getArguments().getLong(Songparam);
            song = SongUtils.getSongById(s);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_details, container, false);
        albumart = view.findViewById(R.id.albumArt);
        share = view.findViewById(R.id.share);
        delete = view.findViewById(R.id.delete);
        addtoPlaylist = view.findViewById(R.id.addtoPlaylist);
        makeRingtone = view.findViewById(R.id.makeRingtone);
        songname = view.findViewById(R.id.songName);
        album = view.findViewById(R.id.album);
        artist = view.findViewById(R.id.artist);
        duration = view.findViewById(R.id.duration);
        location = view.findViewById(R.id.location);
        year = view.findViewById(R.id.year);

        songgame1 = view.findViewById(R.id.songName1);
        songgame1.setText(song.getTitle());
        songname.setText(song.getTitle());
        artist.setText(song.getArtist());
        album.setText(song.getAlbum());
        year.setText(String.valueOf(song.getYear()));
        duration.setText(DateUtils.formatElapsedTime(song.getDurationSeconds()));
        location.setText(song.getFilePath());
        Picasso.get().load(ExtraUtils.getUrifromAlbumID(song)).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_foreground).into(albumart);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareSong(v);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSong(v);
            }
        });

        addtoPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addtoPlaylist(v);
            }
        });
        makeRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRingtone(v);
            }
        });
        return view;
    }

    private void makeRingtone(View v) {

    }

    private void deleteSong(View v) {

    }

    private void addtoPlaylist(View v) {

    }

    private void shareSong(View v) {

    }

}
