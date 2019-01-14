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

import com.sahdeepsingh.Bop.Adapters.GenreRecyclerViewAdapter;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentGenre extends Fragment {

    GenreRecyclerViewAdapter mfilteredAdapter;
    EditText search;
    List<String> filtered = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_genre, container, false);

        // Set the adapter

            Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            ArrayList<String> genres = Main.songs.getGenres();
            Collections.sort(genres);
            GenreRecyclerViewAdapter myGenreRecyclerViewAdapter = new GenreRecyclerViewAdapter(genres);
            recyclerView.setAdapter(myGenreRecyclerViewAdapter);

        search = view.findViewById(R.id.searchGenre);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filtered.clear();
                charSequence = charSequence.toString().toLowerCase();
                if (charSequence.length() == 0) {
                    filtered.addAll(genres);
                } else
                    for (int j = 0; j < genres.size(); j++) {
                        String genre = genres.get(j);
                        if (genre.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filtered.add(genres.get(j));
                        }
                    }
                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                Collections.sort(filtered, String::compareToIgnoreCase);
                mfilteredAdapter = new GenreRecyclerViewAdapter(filtered);
                recyclerView.setAdapter(mfilteredAdapter);
                mfilteredAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }
}
