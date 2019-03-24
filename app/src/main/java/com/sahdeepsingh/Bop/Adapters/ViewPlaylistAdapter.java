package com.sahdeepsingh.Bop.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.Visualizers.barVisuals;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.utils.utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPlaylistAdapter extends RecyclerView.Adapter<ViewPlaylistAdapter.ViewHolder> {

    String name;
    private List<Song> mValues;

    public ViewPlaylistAdapter(List<Song> items, String playlistname) {
        mValues = items;
        name = playlistname;
    }

    @NonNull
    @Override
    public ViewPlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_songs_playlist, parent, false);
        return new ViewPlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewPlaylistAdapter.ViewHolder holder, int position) {
        final Song localItem = mValues.get(position);
        holder.songName.setText(localItem.getTitle());
        holder.songBy.setText(localItem.getArtist());
        holder.songName.setSelected(true);
        Picasso.get().load(utils.getUrifromAlbumID(localItem)).centerCrop().fit().error(R.mipmap.ic_launcher).into(holder.circleImageView);

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
        holder.songOptions.setImageDrawable(utils.getThemedIcon(holder.mView.getContext(), holder.mView.getContext().getDrawable(R.drawable.ic_3dots)));
        holder.songOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), holder.songOptions);
                popup.inflate(R.menu.view_playlist_options);
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.one:
                            Main.songs.deletePlaylistTrack(view.getContext(), Long.parseLong(Main.songs.getPlayListId(name)), Main.songs.getSongsByPlaylist(name).get(holder.getAdapterPosition()).getId());
                            notifyItemRemoved(holder.getAdapterPosition());
                            notifyItemRangeChanged(holder.getAdapterPosition(), mValues.size());
                            return true;
                        case R.id.two:
                            utils.shareSong(view.getContext(), Main.songs.getSongsByPlaylist(name).get(holder.getAdapterPosition()));
                            return true;
                        case R.id.three:
                            utils.showSongDetails(view.getContext(), Main.songs.getSongsByPlaylist(name).get(holder.getAdapterPosition()));
                            return true;
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

    public void updateData(ArrayList<Song> songs) {
        mValues = songs;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView songName;
        public final TextView songBy;
        final View mView;
        final CircleImageView circleImageView;
        ImageView songOptions;
        barVisuals barVisuals;

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