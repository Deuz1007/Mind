package com.example.mind;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

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

    public static BackgroundMusicPlayer getInstance(Context context, int rawResourceId) {
        if (instance == null) {
            instance = new BackgroundMusicPlayer(context, rawResourceId);
        }
        return instance;
    }

    public BackgroundMusicPlayer(Context context, int rawResourceId) {
        mediaPlayer = MediaPlayer.create(context, rawResourceId);
        mediaPlayer.setLooping(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void start() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                isPlaying = true;
            }
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
            mediaPlayer = MediaPlayer.create(this, R.raw.bgm1); // Replace with your raw resource
            isPlaying = false;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    // Adjust the volume if needed
    public void setVolume(float leftVolume, float rightVolume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void fadeInVolume(int duration) {
        if (mediaPlayer != null && !isPlaying) {
            isPlaying = true;

            mediaPlayer.setVolume(0f, 0f);

            ValueAnimator volumeAnimator = ValueAnimator.ofFloat(0f, 1f);
            volumeAnimator.setDuration(duration);
            volumeAnimator.setInterpolator(new LinearInterpolator());

            volumeAnimator.addUpdateListener(animation -> {
                float volume = (float) animation.getAnimatedValue();
                mediaPlayer.setVolume(volume, volume);
            });

            volumeAnimator.start();
            mediaPlayer.start();
        }
    }

    public void fadeOutVolume(int duration) {
        if (mediaPlayer != null && isPlaying) {
            isPlaying = false;

            ValueAnimator volumeAnimator = ValueAnimator.ofFloat(1f, 0f);
            volumeAnimator.setDuration(duration);
            volumeAnimator.setInterpolator(new LinearInterpolator());

            volumeAnimator.addUpdateListener(animation -> {
                float volume = (float) animation.getAnimatedValue();
                mediaPlayer.setVolume(volume, volume);
            });

            volumeAnimator.start();

            // Delay stopping the music to allow the fade-out effect to complete
            mediaPlayer.setOnCompletionListener(mp -> stop());
        }
    }


}