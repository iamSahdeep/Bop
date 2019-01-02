package com.sahdeepsingh.Bop.fragments;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
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

    private OnListFragmentInteractionListener mListener;
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
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(int item, String type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mySongsRecyclerViewAdapter = new MySongsRecyclerViewAdapter(Main.songs.songs, mListener);
            mySongsRecyclerViewAdapter.setActionModeReceiver(this);
            mySongsRecyclerViewAdapter.setHasStableIds(true);
            //recyclerView properties for fast scrolling but doesnt work much
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(100);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setAdapter(mySongsRecyclerViewAdapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    ActionBar actionBar = getActivity().getActionBar();
                    if (actionBar != null)
                        if (dy > 0) {
                            // Scrolling up

                            actionBar.hide();

                        } else {
                            // Scrolling down
                            actionBar.show();
                        }
                }
            });
        }
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
