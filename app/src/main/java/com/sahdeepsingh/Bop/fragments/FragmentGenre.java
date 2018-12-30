package com.sahdeepsingh.Bop.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahdeepsingh.Bop.Adapters.MyGenreRecyclerViewAdapter;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentGenre extends Fragment {

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentGenre() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            ArrayList<String> genres = Main.songs.getGenres();
            Collections.sort(genres);
            MyGenreRecyclerViewAdapter myGenreRecyclerViewAdapter = new MyGenreRecyclerViewAdapter(genres, mListener);
            recyclerView.setAdapter(myGenreRecyclerViewAdapter);
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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
        void onListFragmentInteraction(int position, String type);
    }
}
