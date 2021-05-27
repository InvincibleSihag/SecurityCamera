package com.invincible.securitycamera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.net.URI;

public class VideoPlayer extends AppCompatActivity {
    String videoDirectory;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.video_player);
        VideoView videoView = findViewById(R.id.videoView);
        videoDirectory = getIntent().getStringExtra("DIRECT");
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        Uri uri = Uri.parse("http://192.168.240.12:5000/api/v1/resources/files/videoFile/?video="+videoDirectory);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
    }
}
