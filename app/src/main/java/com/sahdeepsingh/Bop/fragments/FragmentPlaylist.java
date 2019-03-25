package com.sahdeepsingh.Bop.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sahdeepsingh.Bop.Adapters.PlaylistRecyclerViewAdapter;
import com.sahdeepsingh.Bop.BopUtils.RVUtils;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;

import java.util.ArrayList;
import java.util.Collections;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FragmentPlaylist extends Fragment {

    PlaylistRecyclerViewAdapter playlistRecyclerViewAdapter;
    LinearLayout noData;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentPlaylist() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        noData = view.findViewById(R.id.noData);
        refreshLayout = view.findViewById(R.id.refreshPlaylists);
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        playlistRecyclerViewAdapter = new PlaylistRecyclerViewAdapter(getPlaylists());
        recyclerView.setAdapter(playlistRecyclerViewAdapter);
        RVUtils.makenoDataVisible(recyclerView, noData);
        ArrayList<String> filtered = new ArrayList<>(getPlaylists());
        EditText search = view.findViewById(R.id.searchPlaylist);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filtered.clear();
                charSequence = charSequence.toString().toLowerCase();
                if (charSequence.length() == 0) {
                    filtered.addAll(getPlaylists());
                } else
                    for (int j = 0; j < getPlaylists().size(); j++) {
                        String playlist = getPlaylists().get(j);
                        if (playlist.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filtered.add(getPlaylists().get(j));
                        }
                    }
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                Collections.sort(filtered, String::compareToIgnoreCase);
                playlistRecyclerViewAdapter.UpdateData(filtered);
                playlistRecyclerViewAdapter.notifyDataSetChanged();
                RVUtils.makenoDataVisible(recyclerView, noData);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPlaylists();
            }
        });

        return view;
    }

    private void refreshPlaylists() {
        Main.songs.updatePlaylists(getActivity(), "external");
        playlistRecyclerViewAdapter.UpdateData(getPlaylists());
        playlistRecyclerViewAdapter.notifyDataSetChanged();
        RVUtils.makenoDataVisible(recyclerView, noData);
        refreshLayout.setRefreshing(false);
    }

    private ArrayList<String> getPlaylists() {
        return Main.songs.getPlaylistNames();
    }

}
