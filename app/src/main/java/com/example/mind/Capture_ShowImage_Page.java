package com.example.mind;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mind.interfaces.PostProcess;
import com.example.mind.utilities.ExtractText;

public class Capture_ShowImage_Page extends AppCompatActivity {

    // To access Camera
    private ImageView imageView;
    private Button camera_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_show_image_page);

        // To Access Camera and Capture Photo
        imageView = findViewById(R.id.imageCaptured);
        camera_button = findViewById(R.id.capture_button);

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(open_camera, 100);
            }
        });
    }

    // To Access Camera and Capture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap photo = (Bitmap)data.getExtras().get("data");
        imageView.setImageBitmap(photo);

        ExtractText.Image(photo, new PostProcess() {
            @Override
            public void Success(Object... o) {
                String text = (String) o[0];

                Intent passTextData = new Intent(Capture_ShowImage_Page.this, EditTextOptionPage.class);
                passTextData.putExtra("extractedtextData", text);
                startActivity(passTextData);
            }

            @Override
            public void Failed(Exception e) {

            }
        });
    }
}