package com.sahdeepsingh.Bop.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.fragments.FragmentPlaylist.OnListFragmentInteractionListener;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPlaylistRecyclerViewAdapter extends RecyclerView.Adapter<MyPlaylistRecyclerViewAdapter.ViewHolder> {

    private final List<String> playlists;
    private OnListFragmentInteractionListener mListener;

    public MyPlaylistRecyclerViewAdapter(List<String> items, OnListFragmentInteractionListener listener) {
        playlists = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.playlistname.setText(playlists.get(position));
        String selectedPlaylist = Main.songs.getPlaylistNames().get(position);
        List<Song> songsList = Main.songs.getSongsByPlaylist(selectedPlaylist);
        for (int i = 0; i < songsList.size(); i++) {
            String path = Main.songs.getAlbumArt(songsList.get(i));
            if (path != null) {
                Picasso.get().load(new File(path)).fit().centerCrop().error(R.drawable.ic_pause_dark).into(holder.albumart);
            } else if (i == songsList.size()-1)
                Picasso.get().load(R.drawable.ic_cancel_dark).fit().centerCrop().into(holder.albumart);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.mView.getContext();
                if (context instanceof FragmentPlaylist.OnListFragmentInteractionListener) {
                    mListener = (FragmentPlaylist.OnListFragmentInteractionListener) context;
                } else {
                    throw new RuntimeException(context.toString()
                            + " must implement OnListFragmentInteractionListener");
                }
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.getAdapterPosition(), "playlist");
            }
        });
    }

    @Override
    public int getItemCount() {
        if ((Main.songs.playlists != null) && (!Main.songs.playlists.isEmpty()))
            return Main.songs.playlists.size();

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView playlistname;
        public final ImageView albumart;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            playlistname = view.findViewById(R.id.PlaylistName);
            albumart = view.findViewById(R.id.albumArt);
        }

    }
}
