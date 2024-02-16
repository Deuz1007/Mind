package com.mindapps.mind;

import android.content.Context;
import android.media.MediaPlayer;

public class SFXManager {
    public static SFXManager lobby;
    public static SFXManager button;
    public static SFXManager quiz;

    public MediaPlayer player;
    public Context context;
    public int resource;

    public SFXManager(Context context, int resource) {
        player = MediaPlayer.create(context, resource);
    }

    public void renew(Context context) {
        this.context = context;
        player = MediaPlayer.create(context, resource);
    }

    public void play() {
        if (player.isPlaying()) return;
        player.start();
    }

    public void stop() {
        if(!player.isPlaying()) return;
        player.stop();
    }
}
