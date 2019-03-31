package com.sahdeepsingh.Bop.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sahdeepsingh.Bop.Adapters.GenreRecyclerViewAdapter;
import com.sahdeepsingh.Bop.BopUtils.DataUtils;
import com.sahdeepsingh.Bop.BopUtils.RVUtils;
import com.sahdeepsingh.Bop.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FragmentGenre extends Fragment {

    GenreRecyclerViewAdapter genreRecyclerViewAdapter;
    EditText search;
    List<String> filtered = new ArrayList<>();
    LinearLayout noData;
    ArrayList<String> genres = DataUtils.getGenres();
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;


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
        noData = view.findViewById(R.id.noData);
        swipeRefreshLayout = view.findViewById(R.id.refreshGenres);
        // Set the adapter

        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        Collections.sort(genres);
        genreRecyclerViewAdapter = new GenreRecyclerViewAdapter(genres);
        recyclerView.setAdapter(genreRecyclerViewAdapter);
        RVUtils.makenoDataVisible(recyclerView, noData);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                genreRecyclerViewAdapter.updateData(genres);
                genreRecyclerViewAdapter.notifyDataSetChanged();
                RVUtils.makenoDataVisible(recyclerView, noData);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
                genreRecyclerViewAdapter.updateData(filtered);
                genreRecyclerViewAdapter.notifyDataSetChanged();
                RVUtils.makenoDataVisible(recyclerView, noData);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

}
