package com.sahdeepsingh.clousic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sahdeepsingh.clousic.R;
import com.sahdeepsingh.clousic.playerMain.Main;


public class BottomControls extends BottomSheetDialogFragment {

    ImageButton play , next , previous;
    boolean isplaying;

    public BottomControls() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =inflater.inflate(R.layout.fragment_bottom_controls, container, false);
        // Inflate the layout for this fragment
        play = rootView.findViewById(R.id.playing_play);
        next = rootView.findViewById(R.id.playing_skip_next);
        previous = rootView.findViewById(R.id.playing_skip_previous);
        isplaying = Main.musicService.isPlaying();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isplaying)
                    Main.musicService.unpausePlayer();
                else Main.musicService.pausePlayer();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.next(true);
                Main.musicService.playSong();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Main.musicService.previous(true);
                Main.musicService.playSong();
            }
        });
        return rootView;

    }


}
