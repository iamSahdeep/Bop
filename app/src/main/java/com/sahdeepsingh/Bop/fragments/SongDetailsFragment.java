package com.sahdeepsingh.Bop.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahdeepsingh.Bop.R;

import androidx.fragment.app.Fragment;

public class SongDetailsFragment extends Fragment {

    private static final String Songparam = "param1";

    private Long song;


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
            song = getArguments().getLong(Songparam);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_details, container, false);

        return view;
    }

}
