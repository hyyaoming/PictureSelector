package org.lym.image.select.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.lym.image.select.tools.FileTools;
import org.lym.image.select.R;
import org.lym.image.select.SelectorSpec;
import org.lym.image.select.adapter.PreviewAdapter;
import org.lym.image.select.bean.ImageItem;
import org.lym.image.select.tools.SelectImageHelper;
import org.lym.image.select.weight.FixViewPager;
import org.lym.image.select.weight.SuperCheckBox;
import org.lym.image.select.weight.photoview.OnPhotoTapListener;

import java.io.File;
import java.util.ArrayList;

import static org.lym.image.select.ui.SelectImageActivity.RESULT_IMAGES;

/**
 * 预览图片的Activity
 * <p>
 * author: ym.li
 * since: 2018/11/3
 */

public class PreviewImageActivity extends AppCompatActivity implements OnPhotoTapListener {
    private FixViewPager mPreViewPager;
    private SelectImageHelper mSelectImageHelper;
    private SuperCheckBox mPreViewCheckbox;
    private TextView mSelectImageTitle;
    private boolean mHideTitleBar;
    private Button mSelectCompleteBtn;
    private RelativeLayout mTitleBar;
    private RelativeLayout mBottomBar;
    private SelectorSpec mSelectorSpec;
    private File mCropImageFile;
    private static final int IMAGE_CROP_CODE = 1;

    public static void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, PreviewImageActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);
        setStatusBarColor();
        initData();
        initView();
        registerListener();
        initAdapter();
    }

    /**
     * 修改状态栏颜色
     */
    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_color));
        }
    }

    private void initData() {
        mSelectImageHelper = SelectImageHelper.getInstance();
        mSelectorSpec = SelectorSpec.getInstance();
    }

    private void initView() {
        mPreViewPager = findViewById(R.id.previewVp);
        mPreViewCheckbox = findViewById(R.id.preview_checkbox);
        mSelectImageTitle = findViewById(R.id.image_title);
        mSelectCompleteBtn = findViewById(R.id.btn_complete);
        mTitleBar = findViewById(R.id.preview_title);
        mBottomBar = findViewById(R.id.bottom_bar);
        resetCheckBox(mSelectImageHelper.selectPosition);
        resetImageTitle(mSelectImageHelper.selectPosition);
        resetCompleteBtn();
    }

    private void registerListener() {
        //选中当前图片按钮
        mPreViewCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectImageHelper.getSelectCount() >= mSelectorSpec.getMaxSelectImage() && mPreViewCheckbox.isChecked()) {
                    mPreViewCheckbox.setChecked(false);
                    Toast.makeText(PreviewImageActivity.this, getString(R.string.max_select_image, String.valueOf(mSelectorSpec.getMaxSelectImage())), Toast.LENGTH_LONG).show();
                } else {
                    mSelectImageHelper.notifyImageItem(mSelectorSpec.isOpenCamera() ? mPreViewPager.getCurrentItem() + 1 : mPreViewPager.getCurrentItem());
                    resetCompleteBtn();
                }
            }
        });
        //viewPager切换事件
        mPreViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                resetCheckBox(position);
                resetImageTitle(position);
            }
        });
        //完成选择图片按钮
        mSelectCompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectorSpec.isNeedCrop() && mSelectorSpec.singleImage()) {
                    mCropImageFile = FileTools.getCropImageFile(PreviewImageActivity.this);
                    FileTools.crop(PreviewImageActivity.this, mSelectImageHelper.getSelectImageItem().get(0).path, mCropImageFile, IMAGE_CROP_CODE, mSelectorSpec);
                } else {
                    complete(null);
                }
            }
        });
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CROP_CODE && resultCode == RESULT_OK) {
            complete(new ImageItem(mCropImageFile.getPath(), mCropImageFile.getName()));
        }
    }

    private void complete(ImageItem image) {
        Intent intent = new Intent();
        ArrayList<String> paths = new ArrayList<>();
        if (null == image) {
            for (ImageItem item : mSelectImageHelper.getSelectImageItem()) {
                paths.add(item.path);
            }
        } else {
            paths.add(image.path);
        }
        intent.putStringArrayListExtra(RESULT_IMAGES, paths);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void initAdapter() {
        PreviewAdapter mPreViewAdapter = new PreviewAdapter(mSelectImageHelper.getAllImageItem(), this);
        mPreViewAdapter.setOnPhotoTapListener(this);
        mPreViewPager.setAdapter(mPreViewAdapter);
        mPreViewPager.setCurrentItem(mSelectImageHelper.selectPosition, false);
    }

    private void resetCheckBox(int position) {
        ImageItem imageItem = mSelectImageHelper.getAllImageItem().get(position);
        mPreViewCheckbox.setChecked(mSelectImageHelper.contains(imageItem));
    }

    private void resetImageTitle(int position) {
        String selectCount = String.valueOf(position + 1);
        String maxImageCount = String.valueOf(mSelectImageHelper.getMaxImageCount());
        String imageCount = getString(R.string.preview_image_count, selectCount, maxImageCount);
        mSelectImageTitle.setText(imageCount);
    }

    private void resetCompleteBtn() {
        int selectImageCount = mSelectImageHelper.getSelectCount();
        if (selectImageCount > 0 && !mSelectorSpec.singleImage()) {
            String maxSelectImageCount = String.valueOf(mSelectorSpec.getMaxSelectImage());
            String selectImageWithMaxCount = getString(R.string.complete_with_select_image_count, String.valueOf(selectImageCount), maxSelectImageCount);
            mSelectCompleteBtn.setText(selectImageWithMaxCount);
            mSelectCompleteBtn.setEnabled(true);
        } else {
            mSelectCompleteBtn.setText(R.string.complete);
            mSelectCompleteBtn.setEnabled(selectImageCount > 0 && mSelectorSpec.singleImage());
        }
    }

    @Override
    public void onPhotoTap(ImageView view, float x, float y) {
        showOrHideBar();
    }

    /**
     * 显示头部和尾部栏
     */
    private void showOrHideBar() {
        if (!mHideTitleBar) {
            ObjectAnimator.ofFloat(mTitleBar, "translationY", 0, -mTitleBar.getHeight()).setDuration(300).start();
            ObjectAnimator.ofFloat(mBottomBar, "translationY", 0, mBottomBar.getHeight()).setDuration(300).start();
        } else {
            ObjectAnimator.ofFloat(mTitleBar, "translationY", mTitleBar.getTranslationY(), 0).setDuration(300).start();
            ObjectAnimator.ofFloat(mBottomBar, "translationY", mBottomBar.getTranslationY(), 0).setDuration(300).start();
        }
        mHideTitleBar = !mHideTitleBar;
    }
}
