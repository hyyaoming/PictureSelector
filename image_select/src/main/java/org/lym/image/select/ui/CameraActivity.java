package org.lym.image.select.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.lym.image.select.tools.FileTools;
import org.lym.image.select.SelectorSpec;
import org.lym.image.select.bean.ImageItem;

import java.io.File;

/**
 * 调用系统拍照
 *
 * @author ym.li
 * @since 2018年12月2日12:03:00
 */
public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 5;
    private static final int IMAGE_CROP_CODE = 1;
    private File mCropImageFile;
    private File mTempPhotoFile;
    private SelectorSpec mSelectorSpec;

    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, CameraActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectorSpec = SelectorSpec.getInstance();
        mTempPhotoFile = FileTools.getCameraFile(this);
        FileTools.camera(this, REQUEST_CAMERA, mSelectorSpec, mTempPhotoFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CROP_CODE && resultCode == RESULT_OK) {
            complete(new ImageItem(mCropImageFile.getPath(), mCropImageFile.getName()));
        } else if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (mTempPhotoFile != null) {
                    if (mSelectorSpec.isNeedCrop()) {
                        mCropImageFile = FileTools.getCropImageFile(this);
                        FileTools.crop(this, mTempPhotoFile.getAbsolutePath(), mCropImageFile, IMAGE_CROP_CODE, mSelectorSpec);
                    } else {
                        complete(new ImageItem(mTempPhotoFile.getPath(), mTempPhotoFile.getName()));
                    }
                }
            } else {
                if (mTempPhotoFile != null && mTempPhotoFile.exists()) {
                    mTempPhotoFile.delete();
                }
                finish();
            }
        } else {
            finish();
        }
    }

    private void complete(ImageItem image) {
        Intent intent = new Intent();
        if (image != null) {
            intent.putExtra("result", image.path);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}
