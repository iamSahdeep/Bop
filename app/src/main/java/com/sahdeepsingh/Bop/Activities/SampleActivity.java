package com.sahdeepsingh.Bop.Activities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sahdeepsingh.Bop.R;

import java.io.FileInputStream;
import java.io.IOException;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        TextView name = findViewById(R.id.sampleSName);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Bundle bundle = getIntent().getExtras();

        assert bundle != null;
        Uri file = Uri.parse(bundle.getString("file"));
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            FileInputStream fis = new FileInputStream(file.getPath());
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });

    }
}
