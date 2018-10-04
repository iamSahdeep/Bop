package com.sahdeepsingh.Bop.fragments;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.fragments.FragmentSongs.OnListFragmentInteractionListener;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MySongsRecyclerViewAdapter extends RecyclerView.Adapter<MySongsRecyclerViewAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private final List<Song> songs;
    private List<Song> selected = new ArrayList<>();
    private OnClickAction receiver;
    private OnListFragmentInteractionListener mListener;

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(position + 1);
    }

    public interface OnClickAction {
        void onClickAction();
    }

    MySongsRecyclerViewAdapter(List<Song> items, OnListFragmentInteractionListener listener) {
        songs = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_songs, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.songName.setText(songs.get(position).getTitle());
        holder.songBy.setText(songs.get(position).getArtist());
        holder.songName.setSelected(true);
        final Song song = songs.get(position);
        holder.circleImageView.setImageBitmap(Main.songs.getAlbumBitmap(song));
        /*String path = Main.songs.getAlbumArt(songs.get(position));
        if (path != null)
            Picasso.get().load(new File(path)).fit().centerCrop().error(R.mipmap.ic_pause).into(holder.circleImageView);
        else Picasso.get().load(R.mipmap.ic_cancel).fit().centerCrop().into(holder.circleImageView);*/
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
                receiver.onClickAction();
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
                    receiver.onClickAction();
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
        final CircleImageView circleImageView;

        ViewHolder(View view) {
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
    public void setActionModeReceiver(OnClickAction receiver) {
        this.receiver = receiver;
    }

    public List<Song> getSelected() {
        return selected;
    }
}
