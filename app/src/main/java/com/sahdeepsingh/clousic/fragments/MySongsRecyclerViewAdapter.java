package com.sahdeepsingh.clousic.fragments;

import android.app.Notification;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sahdeepsingh.clousic.R;
import com.sahdeepsingh.clousic.SongData.Song;
import com.sahdeepsingh.clousic.fragments.FragmentSongs.OnListFragmentInteractionListener;
import com.sahdeepsingh.clousic.fragments.dummy.DummyContent.DummyItem;
import com.sahdeepsingh.clousic.playerMain.Main;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySongsRecyclerViewAdapter extends RecyclerView.Adapter<MySongsRecyclerViewAdapter.ViewHolder> {

    private final List<Song> songs;

    private  OnListFragmentInteractionListener mListener;

    public MySongsRecyclerViewAdapter(List<Song> items, OnListFragmentInteractionListener listener) {
        songs = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_songs, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.songName.setText(songs.get(position).getTitle());
        holder.songBy.setText(songs.get(position).getArtist());

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
                if (mListener !=null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.getAdapterPosition());


                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if ((Main.musicList != null) && (! Main.musicList.isEmpty()))
        return Main.musicList.size();

        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView songName;
        public final TextView songBy;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            songName = (TextView) view.findViewById(R.id.songName);
            songBy = (TextView) view.findViewById(R.id.songBy);
        }

    }
}
