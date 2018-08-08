package com.sahdeepsingh.Bop.fragments;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FragmentSongs extends android.app.Fragment implements MySongsRecyclerViewAdapter.OnClickAction {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    MySongsRecyclerViewAdapter mySongsRecyclerViewAdapter;
    ActionMode actionMode;EditText name;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentSongs() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentSongs newInstance(int columnCount) {
        FragmentSongs fragment = new FragmentSongs();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs_list, container, false);

        final FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.fab_Playall);


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mySongsRecyclerViewAdapter = new MySongsRecyclerViewAdapter(Main.songs.songs, mListener);
            mySongsRecyclerViewAdapter.setActionModeReceiver(this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
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
                            floatingActionButton.hide();

                        } else {
                            // Scrolling down
                            actionBar.show();
                            floatingActionButton.show();
                        }
                }
            });
        }
        return view;
    }


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
                    Toast.makeText(getActivity(), mySongsRecyclerViewAdapter.getSelected().size() + " selected All", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.deselectAll:
                    deselectAll();
                    Toast.makeText(getActivity(), mySongsRecyclerViewAdapter.getSelected().size() + " Deselected All", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.addtoPlaylist:
                    showPlaylistDialog();
                    mode.finish();
                    return true;
                case R.id.Append:
                    Toast.makeText(getActivity(), mySongsRecyclerViewAdapter.getSelected().size() + " wtf", Toast.LENGTH_SHORT).show();
                    mode.finish();
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

    private void showPlaylistDialog() {
        final ListView listView;
        Button create, cancel;
        TextView playListname;
        ArrayList<String> allPlaylists = Main.songs.getPlaylistNames();
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        View view  = getActivity().getLayoutInflater().inflate(R.layout.newplaylistdialog, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),R.layout.item_newplaylistdialog , allPlaylists);
        listView =  view.findViewById(R.id.playlistListview);
        listView.setAdapter(arrayAdapter);
        playListname = view.findViewById(R.id.PlaylistName_new);
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
                Main.songs.newPlaylist(getActivity().getApplicationContext(),"external" , name.getText().toString() ,(ArrayList<Song>) mySongsRecyclerViewAdapter.getSelected());
                dialog.dismiss();
            }
        });
        cancel = view.findViewById(R.id.cancelPlaylist);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
