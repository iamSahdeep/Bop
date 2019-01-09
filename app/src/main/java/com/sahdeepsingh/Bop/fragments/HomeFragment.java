package com.sahdeepsingh.Bop.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sahdeepsingh.Bop.Adapters.MostPlayedSongsAdapter;
import com.sahdeepsingh.Bop.Adapters.RecentSongsAdapter;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class HomeFragment extends Fragment {

    RecentSongsAdapter recentSongsAdapter;
    MostPlayedSongsAdapter mostPlayedSongsAdapter;
    LinearLayout recents, mostPlayed, openAllSongs, openAlbums, openPlaylists, openGenres;


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
        mostPlayed = view.findViewById(R.id.noMostPlayedSongs);
        openAllSongs = view.findViewById(R.id.openAllSongs);
        openAlbums = view.findViewById(R.id.openAlbums);
        openPlaylists = view.findViewById(R.id.openPlaylists);
        openGenres = view.findViewById(R.id.openGenres);

        ViewPager mViewPager = getActivity().findViewById(R.id.container);

        openAllSongs.setOnClickListener(view14 -> mViewPager.setCurrentItem(1, true));

        openGenres.setOnClickListener(view13 -> mViewPager.setCurrentItem(4, true));

        openPlaylists.setOnClickListener(view12 -> mViewPager.setCurrentItem(2, true));

        openAlbums.setOnClickListener(view1 -> mViewPager.setCurrentItem(3, true));

        List<Long> recentSongs = Main.songs.getRecentSongs(getActivity());
        List<Song> mostPlayedSongs = Main.songs.getMostPlayedSongs(getActivity());

        if (recentSongs == null || recentSongs.size() == 0)
            recents.setVisibility(View.VISIBLE);
        else recents.setVisibility(View.GONE);

        if (mostPlayedSongs == null || mostPlayedSongs.size() == 0) {
            mostPlayed.setVisibility(View.VISIBLE);
        } else mostPlayed.setVisibility(View.GONE);

        RecyclerView recentRecycler = view.findViewById(R.id.recyclerRecent);
        recentRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recentRecycler.setHasFixedSize(true);
        recentRecycler.setNestedScrollingEnabled(false);
        recentSongsAdapter = new RecentSongsAdapter(recentSongs);
        recentRecycler.setAdapter(recentSongsAdapter);

        RecyclerView mostPlayedRecycler = view.findViewById(R.id.recyclerMostPlayed);
        mostPlayedRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mostPlayedRecycler.setHasFixedSize(true);
        mostPlayedRecycler.setNestedScrollingEnabled(false);
        mostPlayedSongsAdapter = new MostPlayedSongsAdapter(getActivity(), mostPlayedSongs);
        mostPlayedRecycler.setAdapter(mostPlayedSongsAdapter);

        return view;
    }
}
