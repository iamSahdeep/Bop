package com.sahdeepsingh.Bop.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sahdeepsingh.Bop.Activities.PlayingNowList;
import com.sahdeepsingh.Bop.Adapters.ViewPlaylistAdapter;
import com.sahdeepsingh.Bop.BopUtils.PlaylistUtils;
import com.sahdeepsingh.Bop.BopUtils.RVUtils;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


public class ViewPlaylist extends Fragment {


    EditText search;
    List<Song> filtered = new ArrayList<>();
    List<Song> all = new ArrayList<>();
    ViewPlaylistAdapter songsRecyclerViewAdapter;
    LinearLayout noData;
    FastScrollRecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    private String playListName;


    public ViewPlaylist() {
        // Required empty public constructor
    }

    public static ViewPlaylist newInstance(String param1) {
        ViewPlaylist fragment = new ViewPlaylist();
        Bundle args = new Bundle();
        args.putString("name", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playListName = getArguments().getString("name");
            all = PlaylistUtils.getSongsByPlaylist(playListName);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_playlist, container, false);

        search = view.findViewById(R.id.searchSongs);
        noData = view.findViewById(R.id.noData);
        floatingActionButton = view.findViewById(R.id.fabplayAll);
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        songsRecyclerViewAdapter = new ViewPlaylistAdapter(all, playListName);
        recyclerView.setAdapter(songsRecyclerViewAdapter);
        RVUtils.makenoDataVisible(recyclerView, noData);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filtered.clear();
                charSequence = charSequence.toString().toLowerCase();
                if (charSequence.length() == 0) {
                    filtered.addAll(all);
                } else
                    for (int j = 0; j < all.size(); j++) {
                        final Song song = all.get(j);
                        String name = song.getTitle();
                        if (name.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filtered.add(all.get(j));
                        }
                    }
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                Collections.sort(filtered, (u1, t1) -> u1.getTitle().compareToIgnoreCase(t1.getTitle()));
                songsRecyclerViewAdapter.updateData((ArrayList<Song>) filtered);
                songsRecyclerViewAdapter.notifyDataSetChanged();
                RVUtils.makenoDataVisible(recyclerView, noData);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (all == null || all.size() <= 0) {
                    Toast.makeText(getActivity(), "No data to play", Toast.LENGTH_SHORT).show();
                    return;
                }
                Main.musicList.clear();
                Main.musicList.addAll(all);
                Main.nowPlayingList = Main.musicList;
                Main.musicService.setList(Main.nowPlayingList);
                Main.musicService.toggleShuffle();
                Intent intent = new Intent(context, PlayingNowList.class);
                intent.putExtra("playlistname", playListName);
                startActivity(intent);
            }
        });

        return view;
    }

}
