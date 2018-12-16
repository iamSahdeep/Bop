package com.sahdeepsingh.Bop.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.RecentsListHorizontalAdapter;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    TextView recentsViewAll, playlistsViewAll, nothingRecent;
    List<Song> recents = new ArrayList<>();
    RecentsListHorizontalAdapter rAdapter;
    RecyclerView recentsRecycler;

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

        playlistsViewAll = view.findViewById(R.id.playlists_view_all);
        recentsRecycler = view.findViewById(R.id.recentsMusicList_home);
        nothingRecent = view.findViewById(R.id.recentsNothingText);
        playlistsViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager viewPager = getActivity().findViewById(R.id.container);
                viewPager.setCurrentItem(2, true);
            }
        });

        if (Main.recentlyPlayed != null && Main.recentlyPlayed.size() > 0) {

            for (int i = 0; i < Math.min(10, Main.recentlyPlayed.size()); i++) {
                recents.add(Main.recentlyPlayed.get(i));
            }
            recentsRecycler.setVisibility(View.VISIBLE);
            nothingRecent.setVisibility(View.GONE);
            rAdapter = new RecentsListHorizontalAdapter(recents, getActivity());
            recentsRecycler = (RecyclerView) view.findViewById(R.id.recentsMusicList_home);
            recentsRecycler.setNestedScrollingEnabled(false);
            recentsRecycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            recentsRecycler.setItemAnimator(new DefaultItemAnimator());
            recentsRecycler.setAdapter(rAdapter);
        } else {
            recentsRecycler.setVisibility(View.GONE);
            nothingRecent.setVisibility(View.VISIBLE);
        }

        return view;
    }

}
