package com.sahdeepsingh.Bop.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */

public class FragmentPlaylist extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private ArrayList<String> lol;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentPlaylist() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentPlaylist newInstance(int columnCount) {
        FragmentPlaylist fragment = new FragmentPlaylist();
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
        View view = inflater.inflate(R.layout.fragment_playlist_list, container, false);
        final FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.fab_Playall);
        floatingActionButton.setImageResource(R.drawable.ic_add_white);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistAdd();
            }
        });
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            lol = Main.songs.getPlaylistNames();
            Collections.sort(lol);
            MyPlaylistRecyclerViewAdapter myPlaylistRecyclerViewAdapter = new MyPlaylistRecyclerViewAdapter(lol, mListener);
            recyclerView.setAdapter(myPlaylistRecyclerViewAdapter);
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

    private void playlistAdd() {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        }/* else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener")
        }*/
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
}
