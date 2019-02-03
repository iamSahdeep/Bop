package com.sahdeepsingh.Bop.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sahdeepsingh.Bop.Activities.PlayingNowList;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
import com.sahdeepsingh.Bop.playerMain.Main;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ListAdapter;
import android.widget.Toast;

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
    Button internal, external, up;
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
    }

    private void loadExternalDirectory() {
        if (new File("/storage/").exists()) {
            path = new File("/storage/");
        }else if (new File("/data/").exists()) {
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
                int drawableID = R.drawable.ic_folder;
                boolean canRead = sel.canRead();
                // Set drawables
                if (sel.isDirectory()) {
                    if (canRead) {
                        drawableID = R.drawable.ic_folder;
                    } else {
                        drawableID = R.drawable.ic_cancel;
                    }
                }
                else if (sel.isFile()){
                    if (URLConnection.guessContentTypeFromName(sel.getAbsolutePath()) != null && URLConnection.guessContentTypeFromName(sel.getAbsolutePath()).startsWith("audio"))
                        drawableID = R.drawable.ic_music;
                    else
                        drawableID = R.drawable.ic_file;
                }
                fileList.add(i, new Item(fList[i], drawableID, canRead));
            }
            if (fileList.size() == 0) {
                noData.setVisibility(View.VISIBLE);
                fileList.add(0, new Item("Directory is empty", -1, true));
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
        public int icon;
        public boolean canRead;

        Item(String file, Integer icon, boolean canRead) {
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


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<Item> mDataset;

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

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.file.setText(mDataset.get(position).file);
            holder.pic.setImageResource(mDataset.get(position).icon);
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
                    }else if (sel.isFile()){
                        if (URLConnection.guessContentTypeFromName(sel.getAbsolutePath()) != null && URLConnection.guessContentTypeFromName(sel.getAbsolutePath()).startsWith("audio")){
                            playSong(sel.getAbsolutePath());
                        }else
                            showToast("Selected file is not Audio/Music");
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    private void playSong(String absolutePath) {
        Intent intent = new Intent(getActivity(), PlayingNowList.class);
        intent.putExtra("file", absolutePath);
        Main.musicList.clear();
        if (Main.songs.getSongbyFile(new File(absolutePath)) == null){
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
}
