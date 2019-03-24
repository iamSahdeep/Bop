package com.sahdeepsingh.Bop.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sahdeepsingh.Bop.Activities.PlayingNowList;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.ViewPlaylist;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.utils.utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistRecyclerViewAdapter extends RecyclerView.Adapter<PlaylistRecyclerViewAdapter.ViewHolder> {

    private List<String> playlists;
    private RecyclerView mRecyclerView;
    private int anyExpanded = -1;
    private ViewHolder expandedViewholder;

    public PlaylistRecyclerViewAdapter(List<String> items) {
        playlists = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_playlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.playlistname.setText(playlists.get(position));
        final String selectedPlaylist = playlists.get(position);
        List<Song> songsList = Main.songs.getSongsByPlaylist(selectedPlaylist);

        Picasso.get().load(utils.getUrifromAlbumID(songsList.get(0))).fit().centerCrop().error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher_foreground).into(holder.albumart);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Song> songsList = Main.songs.getSongsByPlaylist(selectedPlaylist);
                Context context = holder.mView.getContext();
                Main.musicList.clear();
                Main.musicList.addAll(songsList);
                Main.nowPlayingList = Main.musicList;
                Main.musicService.setList(Main.nowPlayingList);
                Intent intent = new Intent(context, PlayingNowList.class);
                intent.putExtra("playlistname", selectedPlaylist);
                context.startActivity(intent);
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPopUPMenu(holder, view, selectedPlaylist);
            }
        });

    }

    private void openPopUPMenu(ViewHolder holder, View view, String playlist) {

        PopupMenu popup = new PopupMenu(view.getContext(), holder.more);
        popup.inflate(R.menu.playlist_options);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.one:
                    deletePlaylist(holder, view, playlist);
                    return true;
                case R.id.two:
                    editName(holder, view, playlist);
                    return true;
                case R.id.three:
                    showPlaylist(holder, view, playlist);
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void showPlaylist(ViewHolder holder, View view, String selectedPlaylist) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, ViewPlaylist.newInstance(selectedPlaylist)).addToBackStack(null).commit();
    }


    private void editName(ViewHolder holder, View view, String selectedPlaylist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Change Name");

        final EditText input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                if (!m_Text.equals("") && !playlists.contains(m_Text)) {
                    Main.songs.renamePlaylist(view.getContext(), m_Text, Long.parseLong(Main.songs.getPlayListId(selectedPlaylist)));
                    Toast.makeText(view.getContext(), "Done! Changes might take time.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(view.getContext(), "Cant be Empty, OR already exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void deletePlaylist(ViewHolder holder, View view, String selectedPlaylist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Sure want to delete?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Main.songs.deletePlaylist(view.getContext(), selectedPlaylist);
                int newPosition = holder.getAdapterPosition();
                playlists.remove(newPosition);
                notifyItemRemoved(newPosition);
                notifyItemRangeChanged(newPosition, playlists.size());
                Toast.makeText(view.getContext(), "Done!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void UpdateData(List<String> items) {
        this.playlists = items;
    }

    @Override
    public int getItemCount() {
        if ((playlists != null) && (!playlists.isEmpty()))
            return playlists.size();

        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
        recyclerView.setNestedScrollingEnabled(false);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView playlistname;
        ImageView albumart;
        ImageButton more;
        LinearLayout expandable;
        RecyclerView recyclerView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            playlistname = view.findViewById(R.id.PlaylistName);
            albumart = view.findViewById(R.id.albumArt);
            more = view.findViewById(R.id.more);
            expandable = view.findViewById(R.id.playlistExpanded);
            recyclerView = view.findViewById(R.id.songPlaylistShow);
        }

    }
}
