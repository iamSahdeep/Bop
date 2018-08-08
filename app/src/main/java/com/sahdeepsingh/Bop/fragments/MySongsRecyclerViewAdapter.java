package com.sahdeepsingh.Bop.fragments;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.fragments.FragmentSongs.OnListFragmentInteractionListener;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * {@link RecyclerView.Adapter} that can display  and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySongsRecyclerViewAdapter extends RecyclerView.Adapter<MySongsRecyclerViewAdapter.ViewHolder> {

    private final List<Song> songs;
    private List<Song> selected = new ArrayList<>();

    private OnListFragmentInteractionListener mListener;

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
        holder.songName.setSelected(true);
        final Song song = songs.get(position);
        String path = Main.songs.getAlbumArt(songs.get(position));
        if (path != null)
            Picasso.get().load(new File(path)).fit().centerCrop().error(R.drawable.ic_pause_dark).into(holder.circleImageView);
        else  Picasso.get().load(R.drawable.ic_cancel_dark).fit().centerCrop().into(holder.circleImageView);
        //holder.circleImageView.setImageBitmap(Main.songs.getAlbumArt(songs.get(position)));
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (selected.contains(song)){
                    selected.remove(song);
                    unhighlightView(holder);
                }else {
                    selected.add(song);
                    highlightView(holder);
                }
                return true;
            }
        });
        if (selected.contains(song))
            highlightView(holder);
        else
            unhighlightView(holder);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected.isEmpty()){
                    Context context = holder.mView.getContext();
                    if (context instanceof OnListFragmentInteractionListener) {
                        mListener = (OnListFragmentInteractionListener) context;
                    } else {
                        throw new RuntimeException(context.toString()
                                + " must implement OnListFragmentInteractionListener");
                    }
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.getAdapterPosition(), "singleSong");
                }else {
                    if (selected.contains(song)){
                        selected.remove(song);
                        unhighlightView(holder);
                    }else {
                        selected.add(song);
                        highlightView(holder);
                    }
                }
                }
        });

        if (selected.contains(song))
            highlightView(holder);
        else
            unhighlightView(holder);
    }

    private void highlightView(ViewHolder holder) {
        holder.mView.setBackgroundColor(Color.GRAY);
    }

    private void unhighlightView(ViewHolder holder) {
        holder.mView.setBackgroundColor(Color.WHITE);
    }


    @Override
    public int getItemCount() {
        if ((Main.songs.songs != null) && (!Main.songs.songs.isEmpty()))
            return Main.songs.songs.size();

        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView songName;
        public final TextView songBy;
        public final CircleImageView circleImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            songName = view.findViewById(R.id.songName);
            songBy = view.findViewById(R.id.songBy);
            circleImageView = view.findViewById(R.id.albumArt);
        }

    }

    public void addAll(List<Song> items) {
        clearAll(false);
        this.selected = items;
        notifyDataSetChanged();
    }

    public void clearAll(boolean isNotify) {
       // items.clear();
        selected.clear();
        if (isNotify) notifyDataSetChanged();
    }

    public void clearSelected() {
        selected.clear();
        notifyDataSetChanged();
    }

    public void selectAll() {
        selected.clear();
        selected.addAll(songs);
        notifyDataSetChanged();
    }

    public List<Song> getSelected() {
        return selected;
    }
}
