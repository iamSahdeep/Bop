package com.sahdeepsingh.Bop.Adapters;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.Visualizers.barVisuals;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

// Adapter for Current Playlist in PlayerList

/**
 * Maps `Songs` inside `ArrayLists` into `TextView` fields.
 * <p>
 * We'll map the ArrayList from our MainActivity into
 * multiple Artist/Title fields inside our activity_main Layout.
 */
public class AdapterSong extends RecyclerView.Adapter<AdapterSong.ViewHolder> {

    private final List<Song> mValues;

    public AdapterSong(List<Song> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_songs_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Song localItem = mValues.get(position);
        holder.songName.setText(localItem.getTitle());
        holder.songBy.setText(localItem.getArtist());
        holder.songName.setSelected(true);
        String path = Main.songs.getAlbumArt(localItem);
        if (path != null) {
            Picasso.get().load(new File(path)).centerCrop().fit().error(R.mipmap.ic_launcher).into(holder.circleImageView);
        }
        if (Main.mainMenuHasNowPlayingItem) {
            if (Main.musicService.currentSong.getTitle().equals(localItem.getTitle())) {
                holder.barVisuals.setVisibility(View.VISIBLE);
                holder.barVisuals.setColor(ContextCompat.getColor(holder.barVisuals.getContext(), R.color.accent));
                holder.barVisuals.setDensity(1);
                holder.barVisuals.setPlayer(Main.musicService.getAudioSession());
            } else {
                holder.barVisuals.setVisibility(View.GONE);
            }
        } else {
            holder.barVisuals.setVisibility(View.GONE);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.musicService.setSong(holder.getAdapterPosition());
                Main.musicService.playSong();
            }
        });
        holder.songOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), holder.songOptions);
                popup.inflate(R.menu.song_options);
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.one:
                            if (Main.musicService.currentSongPosition == holder.getAdapterPosition()) {
                                Main.musicService.next(true);
                                Main.musicService.playSong();
                            }
                            Main.nowPlayingList.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            notifyItemRangeChanged(holder.getAdapterPosition(), mValues.size());
                            return true;
                        /*case R.id.two:
                            //handle menu2 click
                            return true;
                        case R.id.three:
                            //handle menu3 click
                            return true;*/
                        default:
                            return false;
                    }
                });
                popup.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        public final TextView songName;
        public final TextView songBy;
        barVisuals barVisuals;
        ImageView songOptions;
        final CircleImageView circleImageView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            songName = view.findViewById(R.id.songName);
            songBy = view.findViewById(R.id.songBy);
            circleImageView = view.findViewById(R.id.albumArt);
            barVisuals = view.findViewById(R.id.barvisuals);
            songOptions = view.findViewById(R.id.songOptions);
        }
    }
}