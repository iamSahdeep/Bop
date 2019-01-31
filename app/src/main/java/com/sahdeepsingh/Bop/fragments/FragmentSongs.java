package com.sahdeepsingh.Bop.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sahdeepsingh.Bop.Activities.PlayingNowList;
import com.sahdeepsingh.Bop.Adapters.SongsRecyclerViewAdapter;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.utils.RVUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentSongs extends android.app.Fragment implements SongsRecyclerViewAdapter.OnClickAction {

    SongsRecyclerViewAdapter songsRecyclerViewAdapter;
    FloatingActionButton floatingActionButton;
    ActionMode actionMode;
    EditText name, search;
    List<Song> filtered = new ArrayList<>(Main.songs.songs);
    LinearLayout noData;



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentSongs() {
    }


    //Action mode for selecting songs and creating playlist.
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.selected, menu);
            Drawable drawable1 = menu.getItem(0).getIcon();
            Drawable drawable2 = menu.getItem(1).getIcon();
            drawable1.mutate();
            drawable2.mutate();
            if (!Main.settings.get("modes", "Day").equals("Day")) {
                drawable1.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                drawable2.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            } else {
                drawable1.setColorFilter(getResources().getColor(R.color.md_grey_900), PorterDuff.Mode.SRC_IN);
                drawable2.setColorFilter(getResources().getColor(R.color.md_grey_900), PorterDuff.Mode.SRC_IN);
            }

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.selectAll:
                    selectAll();
                    Toast.makeText(getActivity(), songsRecyclerViewAdapter.getSelected().size() + " selected", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.deselectAll:
                    deselectAll();
                    Toast.makeText(getActivity(), songsRecyclerViewAdapter.getSelected().size() + " selected", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.addtoPlaylist:
                    showPlaylistDialog();
                    mode.finish();
                    return true;
                case R.id.Append:
                    Toast.makeText(getActivity(), " Not created this method yet", Toast.LENGTH_SHORT).show();
                    // mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            deselectAll();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        search = view.findViewById(R.id.searchSongs);
        noData = view.findViewById(R.id.noData);
        floatingActionButton = view.findViewById(R.id.fabplayAll);


        // Set the adapter
            Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
        songsRecyclerViewAdapter = new SongsRecyclerViewAdapter(Main.songs.songs);
        songsRecyclerViewAdapter.setActionModeReceiver(this);
        songsRecyclerViewAdapter.setHasStableIds(true);
        //recyclerView properties for fast scrolling but doesn't work much
            recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
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
                    filtered.addAll(Main.songs.songs);
                } else
                    for (int j = 0; j < Main.songs.songs.size(); j++) {
                        final Song song = Main.songs.songs.get(j);
                        String name = song.getTitle();
                        if (name.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filtered.add(Main.songs.songs.get(j));
                        }
                    }
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                Collections.sort(filtered, (u1, t1) -> u1.getTitle().compareToIgnoreCase(t1.getTitle()));
                songsRecyclerViewAdapter = new SongsRecyclerViewAdapter(filtered);
                recyclerView.setAdapter(songsRecyclerViewAdapter);
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
                if (Main.songs.songs == null || Main.songs.songs.size() <= 0) {
                    Toast.makeText(getActivity(), "No songs to play", Toast.LENGTH_SHORT).show();
                    return;
                }
                Main.musicList.clear();
                Main.musicList = Main.songs.songs;
                Main.nowPlayingList = Main.musicList;
                Main.musicService.setList(Main.nowPlayingList);
                Main.musicService.toggleShuffle();
                Intent intent = new Intent(context, PlayingNowList.class);
                intent.putExtra("playlistname", "All Songs");
                startActivity(intent);
            }
        });

        return view;
    }

    //Playlist dialog for creating Playlist through action mode
    private void showPlaylistDialog() {
        final ListView listView;
        Button create, cancel;
        ArrayList<Song> songArrayList = new ArrayList<>(songsRecyclerViewAdapter.getSelected());
        ArrayList<String> allPlaylists = Main.songs.getPlaylistNames();
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        @SuppressLint("InflateParams") View view = getActivity().getLayoutInflater().inflate(R.layout.newplaylistdialog, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),R.layout.item_newplaylistdialog , allPlaylists);
        listView =  view.findViewById(R.id.playlistListview);
        listView.setAdapter(arrayAdapter);
        name = view.findViewById(R.id.newPlaylistName);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String namenew = listView.getItemAtPosition(i).toString();
                name.setText(namenew);
            }
        });
        create = view.findViewById(R.id.createPlaylist);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().isEmpty()){
                    name.setError("cant be empty");
                    return;
                }
                Main.showProgressDialog(getActivity());
                Main.songs.newPlaylist(getActivity().getApplication(), "external", name.getText().toString(), songArrayList);
                Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                Main.hideProgressDialog();
                // deselectAll();
            }
        });
        cancel = view.findViewById(R.id.cancelPlaylist);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                deselectAll();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    public void selectAll() {
        songsRecyclerViewAdapter.selectAll();
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
            actionMode.setTitle("Selected: " + songsRecyclerViewAdapter.getSelected().size());
        }
    }

    public void deselectAll() {
        songsRecyclerViewAdapter.clearSelected();
        if (actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }
    }

    public void onClickAction() {
        int selected = songsRecyclerViewAdapter.getSelected().size();
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
            assert actionMode != null;
            actionMode.setTitle("Selected: " + selected);
        } else {
            if (selected == 0) {
                actionMode.finish();
            } else {
                actionMode.setTitle("Selected: " + selected);
            }
        }
    }

}
