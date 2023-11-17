package com.example.mind;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mind.data.SocketIO;
import com.example.mind.dialogs.ErrorDialog;
import com.example.mind.interfaces.PostProcess;
import com.example.mind.utilities.ExtractText;
import android.media.MediaPlayer;

import java.io.File;

public class Capture_ShowImage_Page extends AppCompatActivity {

    // To access Camera
    private ImageView imageView;

    Uri imageUri;
    String imagePath;
    File imageFile;

    TextView notificationBar;
    ErrorDialog errorDialog;
    MediaPlayer buttonClickSound;

    final int CAMERA_CAPTURE_REQUEST_CODE = 111;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_show_image_page);

        // Initialize button click sound
        buttonClickSound = MediaPlayer.create(this, R.raw.btn_click3);

        notificationBar = findViewById(R.id.notification);
        errorDialog = new ErrorDialog(this);
        SocketIO.setNotificationBar(notificationBar, errorDialog);

        // To Access Camera and Capture Photo
        imageView = findViewById(R.id.imageCaptured);
        Button camera_button = findViewById(R.id.capture_button);

        camera_button.setOnClickListener(view -> {
            buttonClickSound.start();
            try {
                imageFile = File.createTempFile("MIND-Capture", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                imagePath = imageFile.getAbsolutePath();
                imageUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", imageFile);

                Intent open_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                open_camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(open_camera, CAMERA_CAPTURE_REQUEST_CODE);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SocketIO.setNotificationBar(notificationBar, errorDialog);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocketIO.setNotificationBar(notificationBar, errorDialog);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (imageFile != null) imageFile.delete();
    }

    // To Access Camera and Capture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_CAPTURE_REQUEST_CODE) {
            Bitmap photo = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(photo);

            try {
                ExtractText.Image(photo, new PostProcess() {
                    @Override
                    public void Success(Object... o) {
                        Intent intent = new Intent(Capture_ShowImage_Page.this, EditTextOptionPage.class);
                        intent.putExtra("extractedText", (String) o[0]);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void Failed(Exception e) {

                    }
                });
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}