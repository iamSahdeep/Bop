package com.sahdeepsingh.Bop.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sahdeepsingh.Bop.Adapters.RecentSongsAdapter;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;

public class HomeFragment extends Fragment {

    RecentSongsAdapter recentSongsAdapter;
    LinearLayout recents;


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

        recents = view.findViewById(R.id.noRecentSongs);
        if (Main.songs.getRecentSongs(getActivity()) == null || Main.songs.getRecentSongs(getActivity()).size() == 0)
            recents.setVisibility(View.VISIBLE);
        else recents.setVisibility(View.GONE);
        RecyclerView recentRecycler = view.findViewById(R.id.recyclerRecent);
        recentRecycler.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recentSongsAdapter = new RecentSongsAdapter(Main.songs.getRecentSongs(getActivity()));
        recentRecycler.setAdapter(recentSongsAdapter);
        return view;
    }
}
