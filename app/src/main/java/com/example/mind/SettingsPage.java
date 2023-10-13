package com.example.mind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class SettingsPage extends AppCompatActivity {

    private BackgroundMusicPlayer backgroundMusicPlayer; // For BGM

    SeekBar volumeSeekBar; // to control music volume
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        // BGM
        backgroundMusicPlayer = BackgroundMusicPlayer.getInstance(this, R.raw.bgm1);
        backgroundMusicPlayer.start();

        // Volume Control
        volumeSeekBar = findViewById(R.id.music);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Get Max Volume
        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        // Get Current Volume
        int currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeSeekBar.setMax(maxVol);
        volumeSeekBar.setProgress(currentVol);
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                //
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Go back to Home Screen
        Button goBackToHomeScreen = findViewById(R.id.go_back_btn);
        goBackToHomeScreen.setOnClickListener(view ->  {
                Intent intent = new Intent(SettingsPage.this, home_screen.class);
                startActivity(intent);
        });

        Button btn_guide = findViewById(R.id.support_btn);
        btn_guide.setOnClickListener(v -> startActivity(new Intent(this, Instructions_Popup.class)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}