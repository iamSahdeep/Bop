package com.sahdeepsingh.Bop.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.Toast;

import com.sahdeepsingh.Bop.Adapters.MySongsRecyclerViewAdapter;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentSongs extends android.app.Fragment implements MySongsRecyclerViewAdapter.OnClickAction {

    MySongsRecyclerViewAdapter mySongsRecyclerViewAdapter;
    ActionMode actionMode;EditText name;


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
                    Toast.makeText(getActivity(), mySongsRecyclerViewAdapter.getSelected().size() + " selected", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.deselectAll:
                    deselectAll();
                    Toast.makeText(getActivity(), mySongsRecyclerViewAdapter.getSelected().size() + " selected", Toast.LENGTH_SHORT).show();
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
        View view = inflater.inflate(R.layout.fragment_songs_list, container, false);

        // Set the adapter
            Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mySongsRecyclerViewAdapter = new MySongsRecyclerViewAdapter(Main.songs.songs);
            mySongsRecyclerViewAdapter.setActionModeReceiver(this);
            mySongsRecyclerViewAdapter.setHasStableIds(true);
        //recyclerView properties for fast scrolling but doesn't work much
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(100);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setAdapter(mySongsRecyclerViewAdapter);
        return view;
    }

    //Playlist dialog for creating Playlist through action mode
    private void showPlaylistDialog() {
        final ListView listView;
        Button create, cancel;
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
                Main.songs.newPlaylist(getActivity().getApplication(), "external", name.getText().toString(), (ArrayList<Song>) mySongsRecyclerViewAdapter.getSelected());
                getActivity().recreate();
                Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                Main.hideProgressDialog();
                deselectAll();
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
        mySongsRecyclerViewAdapter.selectAll();
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
            actionMode.setTitle("Selected: " + mySongsRecyclerViewAdapter.getSelected().size());
        }
    }

    public void deselectAll() {
        mySongsRecyclerViewAdapter.clearSelected();
        if (actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }
    }

    public void onClickAction() {
        int selected = mySongsRecyclerViewAdapter.getSelected().size();
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
