package com.sahdeepsingh.Bop.SongData;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecentsListHorizontalAdapter extends RecyclerView.Adapter<RecentsListHorizontalAdapter.MyViewHolder> {

    Context ctx;
    private List<Song> recentslyPlayed;

    public RecentsListHorizontalAdapter(List<Song> recentslyPlayed, Context ctx) {
        this.recentslyPlayed = recentslyPlayed;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_layout2, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Song ut = recentslyPlayed.get(position);
        Picasso.get().load(Main.songs.getAlbumArt(ut)).placeholder(R.drawable.back).error(R.drawable.back).fit().centerCrop().into(holder.art);
        holder.title.setText(ut.getTitle());
        holder.artist.setText(ut.getArtist());

    }

    @Override
    public int getItemCount() {
        return recentslyPlayed.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView art;
        TextView title, artist;
        RelativeLayout bottomHolder;

        public MyViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.backImage);
            title = (TextView) view.findViewById(R.id.card_title);
            artist = (TextView) view.findViewById(R.id.card_artist);
            bottomHolder = (RelativeLayout) view.findViewById(R.id.bottomHolder);
        }
    }

}
