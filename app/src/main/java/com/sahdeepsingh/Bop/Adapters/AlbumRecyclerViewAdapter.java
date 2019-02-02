package com.sahdeepsingh.Bop.Adapters;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahdeepsingh.Bop.Activities.PlayingNowList;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumRecyclerViewAdapter extends RecyclerView.Adapter<AlbumRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;

    public AlbumRecyclerViewAdapter(List<String> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_album_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String selectedAlbum = mValues.get(position);
        holder.albumname.setText(mValues.get(position));
        List<Song> songsList = Main.songs.getSongsByAlbum(selectedAlbum);
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = Uri.EMPTY;
        if (!songsList.isEmpty()) {
            uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(songsList.get(0).getAlbumid()));
        }
        Picasso.get().load(uri).fit().centerCrop().error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher_foreground).into(holder.albumart);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Song> songsList = Main.songs.getSongsByAlbum(selectedAlbum);
                Context context = holder.mView.getContext();
                Main.musicList.clear();
                Main.musicList = songsList;
                Main.nowPlayingList = Main.musicList;
                Main.musicService.setList(Main.nowPlayingList);
                Intent intent = new Intent(context, PlayingNowList.class);
                intent.putExtra("playlistname", selectedAlbum);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView albumname;
        public final ImageView albumart;

        ViewHolder(View view) {
            super(view);
            mView = view;
            albumname = view.findViewById(R.id.AlbumName);
            albumart = view.findViewById(R.id.albumArt1);
        }

    }
}
