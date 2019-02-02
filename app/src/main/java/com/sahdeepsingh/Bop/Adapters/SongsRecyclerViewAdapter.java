package com.sahdeepsingh.Bop.Adapters;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sahdeepsingh.Bop.Activities.PlayingNowList;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SongsRecyclerViewAdapter extends RecyclerView.Adapter<SongsRecyclerViewAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private final List<Song> songs;
    private List<Song> selected = new ArrayList<>();
    private OnClickAction receiver;

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(songs.get(position).getTitle().charAt(0));
    }

    public interface OnClickAction {
        void onClickAction();
    }

    public SongsRecyclerViewAdapter(List<Song> items) {
        songs = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_songs_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Song localItem = songs.get(position);
        holder.songName.setText(localItem.getTitle());
        holder.songBy.setText(localItem.getArtist());
        holder.songName.setSelected(true);
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri,Long.parseLong(localItem.getAlbumid()));
        Picasso.get().load(uri).centerCrop().fit().error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher_foreground).into(holder.circleImageView);
        Bitmap bitmap = Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888);
        /*try {
            bitmap = MediaStore.Images.Media.getBitmap(holder.mView.getContext().getContentResolver(), uri);
        } catch (IOException e) {
            bitmap = Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888);
        }*/
        final Bitmap finalBitmap = bitmap;
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (selected.contains(localItem)) {
                    selected.remove(localItem);
                    unhighlightView(holder, finalBitmap);
                }else {
                    selected.add(localItem);
                    highlightView(holder);
                }
                try {
                    receiver.onClickAction();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });
        if (selected.contains(localItem))
            highlightView(holder);
        else
            unhighlightView(holder, bitmap);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected.isEmpty()){
                    Context context = holder.mView.getContext();
                    Main.musicList.clear();
                    Main.musicList.add(localItem);
                    Main.nowPlayingList = Main.musicList;
                    Main.musicService.setList(Main.nowPlayingList);
                    Intent intent = new Intent(context, PlayingNowList.class);
                    intent.putExtra("playlistname", "Single Song");
                    context.startActivity(intent);
                }else {
                    if (selected.contains(localItem)) {
                        selected.remove(localItem);
                        unhighlightView(holder, finalBitmap);
                    }else {
                        selected.add(localItem);
                        highlightView(holder);
                    }
                    try {
                        receiver.onClickAction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                }
        });

        if (selected.contains(localItem))
            highlightView(holder);
        else
            unhighlightView(holder, bitmap);
    }

    private void highlightView(ViewHolder holder) {
        holder.mView.setBackgroundColor(Color.parseColor("#C0C0C0"));
        holder.circleImageView.setImageResource(R.drawable.ic_check);
    }

    private void unhighlightView(ViewHolder holder, Bitmap draw) {
        holder.mView.setBackgroundColor(Color.TRANSPARENT);
        if (draw != null)
            holder.circleImageView.setImageBitmap(draw);
        else holder.circleImageView.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if ((songs != null) && (!songs.isEmpty()))
            return songs.size();

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
