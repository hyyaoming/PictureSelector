package org.lym.picture.selector;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.lym.image.select.PictureSelector;
import org.lym.image.select.imageloader.GlideImageLoader;

public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_REQUEST_CODE = 100;
    private static final int IMAGES_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindListener();
    }

    private void bindListener() {
        //选择多张图片
        findViewById(R.id.btn_multiple_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToSelectImage();
            }
        });
        //选择单张图片
        findViewById(R.id.btn_single_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToSelectImage();
            }
        });
    }

    private void jumpToSelectImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        } else {
            PictureSelector.with(this)
                    .selectSpec()
                    .setImageLoader(new GlideImageLoader())
                    .setSpanCount(4)
                    .setMaxSelectImage(1)
                    .startForResult(IMAGES_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            jumpToSelectImage();
        }
    }
}
