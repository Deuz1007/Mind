package com.example.mind;

import android.content.Context;
import android.media.MediaPlayer;

public class BackgroundMusicPlayer {
    private static BackgroundMusicPlayer instance;
    private MediaPlayer mediaPlayer;

    private BackgroundMusicPlayer(Context context, int rawResourceId) {
        mediaPlayer = MediaPlayer.create(context, rawResourceId);
    }

    public static BackgroundMusicPlayer getInstance(Context context, int rawResourceId) {
        if (instance == null) {
            instance = new BackgroundMusicPlayer(context, rawResourceId);
        }
        return instance;
    }

    public void start() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
