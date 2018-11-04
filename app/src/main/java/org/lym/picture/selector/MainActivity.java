package org.lym.picture.selector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.lym.image.select.PictureSelector;
import org.lym.image.select.imageloader.GlideImageLoader;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_REQUEST_CODE = 100;
    private static final int IMAGES_CODE = 101;
    private ResultImageAdapter mResultAdapter;
    private boolean single;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        bindListener();
    }

    private void initView() {
        RecyclerView mImageRv = findViewById(R.id.result_image_rv);
        mImageRv.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        mImageRv.setLayoutManager(manager);
        mResultAdapter = new ResultImageAdapter(this, null);
        mImageRv.setAdapter(mResultAdapter);
    }

    private void bindListener() {
        //选择多张图片
        findViewById(R.id.btn_multiple_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                single = false;
                jumpToSelectImage();
            }
        });
        //选择单张图片
        findViewById(R.id.btn_single_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                single = true;
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
                    .setSpanCount(3)
                    .setMaxSelectImage(single ? 1 : 9)
                    .startForResult(IMAGES_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGES_CODE && resultCode == Activity.RESULT_OK) {
            if (null != data) {
                List<String> paths = PictureSelector.obtainPathResult(data);
                mResultAdapter.setNewData(paths);
            }
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
