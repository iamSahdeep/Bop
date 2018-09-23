package com.sahdeepsingh.Bop.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.fragments.FragmentGenre.OnListFragmentInteractionListener;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


public class MyGenreRecyclerViewAdapter extends RecyclerView.Adapter<MyGenreRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;
    private OnListFragmentInteractionListener mListener;

    MyGenreRecyclerViewAdapter(List<String> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_genre, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.genre.setText(mValues.get(position));
        String selectedGenre = Main.songs.getGenres().get(position);
        Log.e("test", String.valueOf(selectedGenre));
        List<Song> songsList = Main.songs.getSongsByGenre(selectedGenre);
        for (int i = 0; i < songsList.size(); i++) {
            String path = Main.songs.getAlbumArt(songsList.get(i));
            if (path != null) {
                Picasso.get().load(new File(path)).fit().centerCrop().error(R.mipmap.ic_pause).into(holder.genreArt);
                break;
            } else if (i == songsList.size()-1)
                Picasso.get().load(R.mipmap.ic_cancel).fit().centerCrop().into(holder.genreArt);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = holder.mView.getContext();
                if (context instanceof FragmentGenre.OnListFragmentInteractionListener) {
                    mListener = (FragmentGenre.OnListFragmentInteractionListener) context;
                } else {
                    throw new RuntimeException(context.toString()
                            + " must implement OnListFragmentInteractionListener");
                }
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.getAdapterPosition(), "GenreList");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        final TextView genre;
        final ImageView genreArt;

        ViewHolder(View view) {
            super(view);
            mView = view;
            genre = view.findViewById(R.id.GenreName);
            genreArt = view.findViewById(R.id.albumArtGenre);
        }
    }
}
