package com.sahdeepsingh.Bop.SongData;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
                .inflate(R.layout.recycler_view_item, parent, false);
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
            Picasso.get().load(new File(path)).centerCrop().fit().error(R.mipmap.ic_pause).into(holder.circleImageView);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Nothing to do
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
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

}