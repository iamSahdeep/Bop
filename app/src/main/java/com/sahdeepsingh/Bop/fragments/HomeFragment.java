package com.sahdeepsingh.Bop.fragments;


import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.utils.utils;

public class HomeFragment extends Fragment {
    NestedScrollView graphicBack;
    Button playlist, genere, album, allSongs;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        allSongs = view.findViewById(R.id.allsongsBanner);
        graphicBack = view.findViewById(R.id.graphicbackground);
        playlist = view.findViewById(R.id.playlistBanner);
        album = view.findViewById(R.id.albumBanner);
        genere = view.findViewById(R.id.genreBanner);
        graphicBack.setBackground(new BitmapDrawable(getResources(), utils.blurMyImage(BitmapFactory.decodeResource(getResources(), R.drawable.back), getActivity())));
        allSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager viewPager = getActivity().findViewById(R.id.container);
                viewPager.setCurrentItem(1, true);
            }
        });
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager viewPager = getActivity().findViewById(R.id.container);
                viewPager.setCurrentItem(2, true);
            }
        });
        genere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager viewPager = getActivity().findViewById(R.id.container);
                viewPager.setCurrentItem(3, true);
            }
        });
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager viewPager = getActivity().findViewById(R.id.container);
                viewPager.setCurrentItem(4, true);
            }
        });
        return view;
    }

}
