package org.lym.image.select.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.lym.image.select.R;
import org.lym.image.select.SelectorSpec;
import org.lym.image.select.adapter.PreviewAdapter;
import org.lym.image.select.bean.ImageItem;
import org.lym.image.select.bean.SelectImageHelper;
import org.lym.image.select.weight.FixViewPager;
import org.lym.image.select.weight.SuperCheckBox;
import org.lym.image.select.weight.photoview.OnPhotoTapListener;

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
    private Button mSelectCompleteBtn;
    private SelectorSpec mSelectorSpec;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);
        initData();
        initView();
        registerListener();
        initAdapter();
    }

    private void registerListener() {
        mPreViewCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectImageHelper.getSelectCount() < mSelectorSpec.getMaxSelectImage()) {
                    mSelectImageHelper.notifyImageItem(mPreViewPager.getCurrentItem());
                    resetCompleteBtn();
                } else {
                    Toast.makeText(PreviewImageActivity.this, getString(R.string.max_select_image, String.valueOf(mSelectorSpec.getMaxSelectImage())), Toast.LENGTH_LONG).show();
                }
            }
        });
        mPreViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                resetCheckBox(position);
                resetImageTitle(position);
            }
        });
    }

    private void initData() {
        mSelectImageHelper = SelectImageHelper.getInstance();
        mSelectorSpec = SelectorSpec.getInstance();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PreviewImageActivity.class);
        context.startActivity(intent);
    }

    private void initAdapter() {
        PreviewAdapter mPreViewAdapter = new PreviewAdapter(mSelectImageHelper.getAllImageItem(), this);
        mPreViewAdapter.setOnPhotoTapListener(this);
        mPreViewPager.setAdapter(mPreViewAdapter);
        mPreViewPager.setCurrentItem(mSelectImageHelper.selectPosition, false);
    }

    private void initView() {
        mPreViewPager = findViewById(R.id.previewVp);
        mPreViewCheckbox = findViewById(R.id.preview_checkbox);
        mSelectImageTitle = findViewById(R.id.image_title);
        mSelectCompleteBtn = findViewById(R.id.btn_complete);
        resetCheckBox(mSelectImageHelper.selectPosition);
        resetImageTitle(mSelectImageHelper.selectPosition);
        resetCompleteBtn();
    }

    private void resetCompleteBtn() {
        int selectImageCount = mSelectImageHelper.getSelectCount();
        if (selectImageCount > 0) {
            String maxSelectImageCount = String.valueOf(mSelectorSpec.getMaxSelectImage());
            String selectImageWithMaxCount = getString(R.string.complete_with_select_image_count, String.valueOf(selectImageCount), maxSelectImageCount);
            mSelectCompleteBtn.setText(selectImageWithMaxCount);
            mSelectCompleteBtn.setEnabled(true);
        } else {
            mSelectCompleteBtn.setText(R.string.complete);
            mSelectCompleteBtn.setEnabled(false);
        }
        mSelectCompleteBtn.setEnabled(selectImageCount > 0);
    }

    private void resetImageTitle(int position) {
        String selectCount = String.valueOf(position + 1);
        String maxImageCount = String.valueOf(mSelectImageHelper.getMaxImageCount());
        String imageCount = getString(R.string.preview_image_count, selectCount, maxImageCount);
        mSelectImageTitle.setText(imageCount);
    }

    private void resetCheckBox(int position) {
        ImageItem imageItem = mSelectImageHelper.getAllImageItem().get(position);
        mPreViewCheckbox.setChecked(mSelectImageHelper.contains(imageItem));
    }

    @Override
    public void onPhotoTap(ImageView view, float x, float y) {
        Toast.makeText(this, "imageView单击", Toast.LENGTH_LONG).show();
    }
}
