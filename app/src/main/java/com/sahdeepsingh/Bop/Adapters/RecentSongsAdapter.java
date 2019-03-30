package com.sahdeepsingh.Bop.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sahdeepsingh.Bop.Activities.PlayingNowList;
import com.sahdeepsingh.Bop.BopUtils.ExtraUtils;
import com.sahdeepsingh.Bop.BopUtils.SongUtils;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecentSongsAdapter extends RecyclerView.Adapter<RecentSongsAdapter.ViewHolder> {

    private List<Long> songs;

    public RecentSongsAdapter(List<Long> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_songs_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (getItemCount() > 0) {
            holder.songName.setText(SongUtils.getSongById(songs.get(position)).getTitle());
            holder.songBy.setText(SongUtils.getSongById(songs.get(position)).getArtist());
            Picasso.get().load(ExtraUtils.getUrifromAlbumID(SongUtils.getSongById(songs.get(position)))).placeholder(R.mipmap.ic_launcher_foreground).placeholder(R.mipmap.ic_launcher).into(holder.circleImageView);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = holder.mView.getContext();
                    Main.musicList.clear();
                    Main.musicList.add(SongUtils.getSongById(songs.get(position)));
                    Main.nowPlayingList = Main.musicList;
                    Main.musicService.setList(Main.nowPlayingList);
                    Intent intent = new Intent(context, PlayingNowList.class);
                    intent.putExtra("playlistname", "Single Song");
                    context.startActivity(intent);

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (songs != null)
            return songs.size();
        else return 0;
    }

    public void updateData(List<Long> recentSongs) {
        this.songs = recentSongs;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView songName;
        public final TextView songBy;
        final CircleImageView circleImageView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            songName = view.findViewById(R.id.titleSongRecent);
            songBy = view.findViewById(R.id.artistSongRecent);
            circleImageView = view.findViewById(R.id.imageSongRecent);
        }

    }
}
