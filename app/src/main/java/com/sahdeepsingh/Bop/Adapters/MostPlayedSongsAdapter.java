package com.sahdeepsingh.Bop.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MostPlayedSongsAdapter extends RecyclerView.Adapter<MostPlayedSongsAdapter.ViewHolder> {

    private List<Song> songs;
    private Context context;

    public MostPlayedSongsAdapter(Context context, List<Song> songs) {
        this.songs = songs;
        this.context = context;
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

        if (getItemCount() != 0) {
            holder.songName.setText(songs.get(position).getTitle());
            holder.songBy.setText(songs.get(position).getArtist());
            Picasso.get().load(Main.songs.getAlbumArt(songs.get(position))).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.circleImageView);
            holder.TimesPlayed.setText(Main.songs.getcountSongsPlayed(context, songs.get(position)));
        }
    }


    @Override
    public int getItemCount() {
        if (songs != null)
            return songs.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView songName;
        public final TextView TimesPlayed;
        public final TextView songBy;
        final CircleImageView circleImageView;

        ViewHolder(View view) {
            super(view);
            songName = view.findViewById(R.id.mostPlayedSongName);
            songBy = view.findViewById(R.id.mostPlayedArtistName);
            circleImageView = view.findViewById(R.id.mostplayedAlbumArt);
            TimesPlayed = view.findViewById(R.id.mostPlayedCount);
        }

    }
}
