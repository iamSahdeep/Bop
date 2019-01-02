package com.sahdeepsingh.Bop.Adapters;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class MyGenreRecyclerViewAdapter extends RecyclerView.Adapter<MyGenreRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;
    private OnListFragmentInteractionListener mListener;

    public MyGenreRecyclerViewAdapter(List<String> items, OnListFragmentInteractionListener listener) {
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
        List<Song> songsList = Main.songs.getSongsByGenre(selectedGenre);
        for (int i = 0; i < songsList.size(); i++) {
            String path = Main.songs.getAlbumArt(songsList.get(i));
            if (path != null) {
                Picasso.get().load(new File(path)).fit().centerCrop().error(R.drawable.ic_pause).into(holder.genreArt);
                break;
            } /*else if (i == songsList.size()-1)
                Picasso.get().load(R.mipmap.ic_launcher).fit().centerCrop().into(holder.genreArt);*/
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = holder.mView.getContext();
                if (context instanceof OnListFragmentInteractionListener) {
                    mListener = (OnListFragmentInteractionListener) context;
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
