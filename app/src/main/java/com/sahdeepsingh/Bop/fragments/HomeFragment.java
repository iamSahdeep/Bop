package com.sahdeepsingh.Bop.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sahdeepsingh.Bop.R;

public class HomeFragment extends Fragment {
    RelativeLayout localbanner;
    TextView playlist, genere, album;

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
        localbanner = view.findViewById(R.id.localBanner);
        playlist = view.findViewById(R.id.playlistBanner);
        album = view.findViewById(R.id.albumBanner);
        genere = view.findViewById(R.id.genreBanner);
        localbanner.setOnClickListener(new View.OnClickListener() {
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
