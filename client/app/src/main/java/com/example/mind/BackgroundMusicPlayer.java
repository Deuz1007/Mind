package com.example.mind;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BackgroundMusicPlayer extends Service {

    private static BackgroundMusicPlayer instance;
    private MediaPlayer mediaPlayer;

    private boolean isPlaying = false;

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        BackgroundMusicPlayer getService() {
            return BackgroundMusicPlayer.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.bgm1);
        mediaPlayer.setLooping(true);
    }

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
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }

    public static void playBGM() {

    }

    public static void playButtonSFX() {

    }

    public boolean isPlaying() {
        return isPlaying;
    }

}
