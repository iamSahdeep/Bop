package com.sahdeepsingh.Bop.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sahdeepsingh.Bop.Activities.PlayingNowList;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.utils.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FileFragment extends Fragment {

    // Stores names of traversed directories
    ArrayList<String> pathDirsList = new ArrayList<String>();

    // Check if the first level of the directory structure is the one showing
    // private Boolean firstLvl = true;

    private static final String LOGTAG = "F_PATH";

    private List<Item> fileList = new ArrayList<Item>();
    Button internal, external, up, playall;
    private File path = null;
    private String chosenFile;
    // private static final int DIALOG_LOAD_FILE = 1000;

    private boolean showHiddenFilesAndDirs = true;

    // Action constants
    private static int currentAction = -1;
    private static final int SELECT_DIRECTORY = 1;
    private static final int SELECT_FILE = 2;

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    ImageButton options;
    private List<File> songs = new ArrayList<>();
    TextView noData, curDir;


    public FileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_file, container, false);

        mRecyclerView = view.findViewById(R.id.filesRV);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new MyAdapter(fileList);
        mRecyclerView.setAdapter(mAdapter);
        initializeButtons(view);
        loadInternalDirectory();

        return view;
    }

    private void parseDirectoryPath() {
        pathDirsList.clear();
        String pathString = path.getAbsolutePath();
        String[] parts = pathString.split("/");
        int i = 0;
        while (i < parts.length) {
            pathDirsList.add(parts[i]);
            i++;
        }
    }

    private void initializeButtons(View view) {
        noData = view.findViewById(R.id.noDataFile);
        curDir = view.findViewById(R.id.currentDir);
        up = view.findViewById(R.id.upDirectory);
        playall = view.findViewById(R.id.playSongsFile);
        up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadDirectoryUp();
            }
        });

        internal = view.findViewById(R.id.buttonInternal);
        internal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInternalDirectory();
            }
        });
        external = view.findViewById(R.id.buttonExternal);
        external.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadExternalDirectory();
            }
        });
        playall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAllSongsinDirectory();
            }
        });
    }

    private void playAllSongsinDirectory() {
        Intent intent = new Intent(getActivity(), PlayingNowList.class);
        intent.putExtra("playlistname", "Files");
        List<Song> songsList = new ArrayList<>();
        Main.musicList.clear();
        for (File f :
                songs) {
            if (Main.songs.getSongbyFile(f) != null)
                songsList.add(Main.songs.getSongbyFile(f));
        }

        if (songsList.isEmpty()) {
            showToast("Lol, i know it sucks");
            return;
        }
        Main.musicList = (ArrayList<Song>) songsList;
        Main.nowPlayingList = Main.musicList;

        Main.musicService.setList(Main.nowPlayingList);
        getActivity().startActivity(intent);
    }

    private void loadExternalDirectory() {
        if (new File("/storage/").exists()) {
            path = new File("/storage/");
        } else if (new File("/data/").exists()) {
            path = new File("/data/");
        } else if (new File("/mnt/").exists()) {
            path = new File("/mnt/");
        } else if (new File("/removable/").exists()) {
            path = new File("/removable/");
        } else {
            path = new File("/");
            noData.setVisibility(View.VISIBLE);
        }
        loadDirectory();
    }

    private void loadInternalDirectory() {
        File tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        if (tempFile.isDirectory())
            this.path = tempFile;

        if (this.path == null) {
            if (Environment.getExternalStorageDirectory().isDirectory()
                    && Environment.getExternalStorageDirectory().canRead())
                path = Environment.getExternalStorageDirectory();
            else {
                path = new File("/");
                noData.setVisibility(View.VISIBLE);
            }
        }
        loadDirectory();
    }

    private void loadDirectory() {
        loadFileList();
        parseDirectoryPath();
        updateCurrentDirectoryTextView();
    }

    private void loadDirectoryUp() {
        if (pathDirsList.size() <= 1)
            return;
        String s = pathDirsList.remove(pathDirsList.size() - 1);
        path = new File(path.toString().substring(0,
                path.toString().lastIndexOf(s)));

        loadDirectory();
    }

    private void updateCurrentDirectoryTextView() {
        int i = 0;
        StringBuilder curDirString = new StringBuilder();
        while (i < pathDirsList.size()) {
            curDirString.append(pathDirsList.get(i)).append("/");
            i++;
        }
        curDir.setText(String.format("Current directory: %s", path));

    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private void loadFileList() {
        try {
            path.mkdirs();
        } catch (SecurityException e) {
            Log.e(LOGTAG, "unable to write on the sd card ");
            showToast(e.getMessage());
        }
        fileList.clear();
        songs.clear();

        if (path.exists() && path.canRead()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    return showHiddenFilesAndDirs
                            || sel.canRead();
                }
            };

            String[] fList = path.list(filter);

            for (int i = 0; i < fList.length; i++) {
                // Convert into file path
                File sel = new File(path, fList[i]);
                Log.d(LOGTAG,
                        "File:" + fList[i] + " readable:"
                                + (Boolean.valueOf(sel.canRead())).toString());
                Drawable drawable = utils.getThemedIcon(getActivity(), ContextCompat.getDrawable(getActivity(), R.drawable.ic_folder));
                boolean canRead = sel.canRead();
                // Set drawables
                if (sel.isDirectory()) {
                    if (canRead) {
                        drawable = utils.getThemedIcon(getActivity(), ContextCompat.getDrawable(getActivity(), R.drawable.ic_folder));
                    } else {
                        drawable = utils.getThemedIcon(getActivity(), ContextCompat.getDrawable(getActivity(), R.drawable.ic_cancel));
                    }
                } else if (sel.isFile()) {
                    if (URLConnection.guessContentTypeFromName(sel.getAbsolutePath()) != null && URLConnection.guessContentTypeFromName(sel.getAbsolutePath()).startsWith("audio")) {
                        drawable = utils.getThemedIcon(getActivity(), ContextCompat.getDrawable(getActivity(), R.drawable.ic_music));
                        songs.add(sel);
                    } else
                        drawable = utils.getThemedIcon(getActivity(), ContextCompat.getDrawable(getActivity(), R.drawable.ic_file));
                }
                fileList.add(i, new Item(fList[i], drawable, canRead));
            }
            if (!songs.isEmpty()) {
                playall.setVisibility(View.VISIBLE);
            } else {
                playall.setVisibility(View.GONE);
            }
            if (fileList.size() == 0) {
                noData.setVisibility(View.VISIBLE);
                // fileList.add(0, new Item("Directory is empty", -1, true));
            } else {
                noData.setVisibility(View.GONE);
                Collections.sort(fileList, new ItemFileNameComparator());
            }
        } else {
            showToast("path does not exist or cannot be read");
        }
        mAdapter.mDataset = fileList;
        mAdapter.notifyDataSetChanged();
    }

    private class Item {
        public String file;
        public Drawable icon;
        public boolean canRead;

        Item(String file, Drawable icon, boolean canRead) {
            this.file = file;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return file;
        }
    }

    private class ItemFileNameComparator implements Comparator<Item> {
        public int compare(Item lhs, Item rhs) {
            return lhs.file.toLowerCase().compareTo(rhs.file.toLowerCase());
        }
    }

    private void playSong(String absolutePath) {
        Intent intent = new Intent(getActivity(), PlayingNowList.class);
        intent.putExtra("file", absolutePath);
        Main.musicList.clear();
        if (Main.songs.getSongbyFile(new File(absolutePath)) == null) {
            Toast.makeText(getActivity(), "Selected Song is not in mediaStore yet, Cant play for now", Toast.LENGTH_SHORT).show();
            return;
        }

        Main.musicList.add(Main.songs.getSongbyFile(new File(absolutePath)));
        Main.nowPlayingList = Main.musicList;
        if (Main.nowPlayingList == null) {
            Toast.makeText(getActivity(), "Selected Song is not in mediaStore yet, Please wait", Toast.LENGTH_SHORT).show();
            return;
        }
        Main.musicService.setList(Main.nowPlayingList);
        getActivity().startActivity(intent);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<Item> mDataset;

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.file.setText(mDataset.get(position).file);
            holder.pic.setImageDrawable(mDataset.get(position).icon);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chosenFile = fileList.get(position).file;
                    File sel = new File(path + "/" + chosenFile);
                    if (sel.isDirectory()) {
                        if (sel.canRead()) {
                            pathDirsList.add(chosenFile);
                            path = new File(sel + "");
                            loadDirectory();

                        } else {
                            showToast("Path does not exist or cannot be read");
                        }
                    } else if (sel.isFile()) {
                        if (URLConnection.guessContentTypeFromName(sel.getAbsolutePath()) != null && URLConnection.guessContentTypeFromName(sel.getAbsolutePath()).startsWith("audio")) {
                            playSong(sel.getAbsolutePath());
                        } else
                            showToast("Selected file is not Audio/Music");
                    }
                }
            });
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        MyAdapter(List<Item> myDataset) {
            mDataset = myDataset;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                         int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_file_item, parent, false);
            return new MyViewHolder(view);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            View mView;
            TextView file;
            ImageView pic;

            MyViewHolder(View v) {
                super(v);
                mView = v;
                file = v.findViewById(R.id.DirectoryName);
                pic = v.findViewById(R.id.fileItemPic);
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}
