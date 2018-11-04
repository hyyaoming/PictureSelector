package org.lym.image.select.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import org.lym.image.select.R;
import org.lym.image.select.SelectorSpec;
import org.lym.image.select.adapter.FolderAdapter;
import org.lym.image.select.adapter.ImagesAdapter;
import org.lym.image.select.bean.ImageFolder;
import org.lym.image.select.bean.ImageItem;
import org.lym.image.select.bean.SelectImageHelper;
import org.lym.image.select.data.ImageDataSource;
import org.lym.image.select.weight.ImageGridDecoration;
import org.lym.image.select.weight.SuperCheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Doc  选择图片Activity
 *
 * @author ym.li
 * @version 2.9.0
 * @since 2018/11/2/002
 */
public class SelectImageActivity extends AppCompatActivity implements ImageDataSource.OnImagesLoadedListener, ImagesAdapter.OnItemClickListener, ImagesAdapter.OnImageSelectListener, View.OnClickListener, FolderAdapter.OnFolderSelectListener, SelectImageHelper.onImageSelectUpdateListener {
    public static final String RESULT_IMAGES = "result_images";
    private List<ImageFolder> mFolderList;
    private RecyclerView mImageRv;
    private RecyclerView mFolderRv;
    private ImagesAdapter mImagesAdapter;
    private FolderAdapter mFolderAdapter;
    private static final long FOLDER_ANIM_DURATION = 300;
    private SelectImageHelper mSelectImageItem;
    private View mMaskView;
    private Button mPreviewBtn;
    private Button mSelectFolderBtn;
    private Button mCompleteBtn;
    private SelectorSpec mSelectorSpec;
    private boolean mOpenFolderRv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        mSelectImageItem = SelectImageHelper.buildInstance();
        mSelectImageItem.setOnImageSelectUpdateListener(this);
        mSelectorSpec = SelectorSpec.getInstance();
        initView();
        initAdapter();
        new ImageDataSource(this, this);
    }

    private void initView() {
        //init imageRv
        mImageRv = findViewById(R.id.rv_image);
        mImageRv.setHasFixedSize(true);
        ((DefaultItemAnimator) mImageRv.getItemAnimator()).setSupportsChangeAnimations(false);
        int spacing = getResources().getDimensionPixelSize(R.dimen.image_grid_spacing);
        mImageRv.addItemDecoration(new ImageGridDecoration(mSelectorSpec.getSpanCount(), spacing, false));
        GridLayoutManager manager = new GridLayoutManager(this, mSelectorSpec.getSpanCount());
        manager.setOrientation(GridLayoutManager.VERTICAL);
        mImageRv.setLayoutManager(manager);
        //init maskView
        mMaskView = findViewById(R.id.masking);
        mMaskView.setOnClickListener(this);
        //init FolderRv
        mFolderRv = findViewById(R.id.rv_folder);
        mFolderRv.setHasFixedSize(true);
        ((DefaultItemAnimator) mFolderRv.getItemAnimator()).setSupportsChangeAnimations(false);
        LinearLayoutManager folderManage = new LinearLayoutManager(this);
        folderManage.setOrientation(LinearLayoutManager.VERTICAL);
        mFolderRv.setLayoutManager(folderManage);
        hideFolderRv();
        //init btn
        mPreviewBtn = findViewById(R.id.btn_preview);
        mSelectFolderBtn = findViewById(R.id.btn_image_folder);
        mCompleteBtn = findViewById(R.id.btn_complete);
        mCompleteBtn.setVisibility(mSelectorSpec.getMaxSelectImage() > 1 ? View.VISIBLE : View.GONE);
        //init listener
        mPreviewBtn.setOnClickListener(this);
        mSelectFolderBtn.setOnClickListener(this);
        mCompleteBtn.setOnClickListener(this);
    }

    private void hideFolderRv() {
        mFolderRv.post(new Runnable() {
            @Override
            public void run() {
                mFolderRv.setTranslationY(mFolderRv.getHeight());
                mFolderRv.setVisibility(View.GONE);
            }
        });
    }

    private void openFolderRv() {
        if (!mOpenFolderRv) {
            mMaskView.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mFolderRv, "translationY",
                    mFolderRv.getHeight(), 0).setDuration(FOLDER_ANIM_DURATION);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mFolderRv.setVisibility(View.VISIBLE);
                }
            });
            animator.start();
            mOpenFolderRv = !mOpenFolderRv;
        }
    }

    private void closeFolderRv() {
        if (mOpenFolderRv) {
            mMaskView.setVisibility(View.GONE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mFolderRv, "translationY",
                    0, mFolderRv.getHeight()).setDuration(FOLDER_ANIM_DURATION);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mFolderRv.setVisibility(View.GONE);
                }
            });
            animator.start();
            mOpenFolderRv = !mOpenFolderRv;
        }
    }

    private void initAdapter() {
        //init imageAdapter
        mImagesAdapter = new ImagesAdapter(null, mImageRv, mSelectImageItem);
        mImagesAdapter.setOnItemClickListener(this);
        mImagesAdapter.setOnImageSelectListener(this);
        mImageRv.setAdapter(mImagesAdapter);
        //init folderAdapter
        mFolderAdapter = new FolderAdapter(this, null);
        mFolderAdapter.setFolderSelectListener(this);
        mFolderRv.setAdapter(mFolderAdapter);
    }

    @Override
    public void onImagesLoaded(List<ImageFolder> imageFolders) {
        if (null != imageFolders && !imageFolders.isEmpty()) {
            mFolderList = imageFolders;
            mImagesAdapter.setNewData(mFolderList.get(0).images);
            updateFolderRv();
            resetButton();
        }
    }

    private void updateFolderRv() {
        mFolderAdapter.setNewData(mFolderList);
    }

    @Override
    public void onBackPressed() {
        if (mOpenFolderRv) {
            closeFolderRv();
        } else {
            super.onBackPressed();
        }
    }

    private void resetButton() {
        int selectImageCount = mSelectImageItem.getSelectCount();
        String previewCount = String.format(getString(R.string.preview_image_button_count), String.valueOf(selectImageCount));
        mPreviewBtn.setText(previewCount);
        mPreviewBtn.setEnabled(selectImageCount > 0);
        if (selectImageCount > 0) {
            mCompleteBtn.setEnabled(true);
            mCompleteBtn.setText(getString(R.string.complete_with_select_image_count, String.valueOf(selectImageCount), String.valueOf(mSelectorSpec.getMaxSelectImage())));
        } else {
            mCompleteBtn.setEnabled(false);
            mCompleteBtn.setText(getString(R.string.complete));
        }
    }

    @Override
    public void onItemClick(int position) {
        mSelectImageItem.selectPosition = position;
        mSelectImageItem.folderAllImage = true;
        mSelectImageItem.addAllImageItem(mImagesAdapter.getAllImageItem());
        PreviewImageActivity.start(this);
    }

    @Override
    public void onImageSelect(SuperCheckBox checkBox, int position) {
        updateImageItemSelect(position);
    }

    private void updateImageItemSelect(int position) {
        ImageItem imageItem = mImagesAdapter.getItem(position);
        mSelectImageItem.addImageItem(imageItem);
        mImagesAdapter.notifyItemChanged(position);
        resetButton();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_image_folder) {
            if (mOpenFolderRv) {
                closeFolderRv();
            } else {
                openFolderRv();
            }
        } else if (viewId == R.id.btn_preview) {
            mSelectImageItem.folderAllImage = false;
            mSelectImageItem.resetPosition();
            PreviewImageActivity.start(this);
        } else if (viewId == R.id.masking) {
            closeFolderRv();
        } else if (viewId == R.id.btn_complete) {
            Intent intent = new Intent();
            ArrayList<String> paths = new ArrayList<>();
            for (ImageItem item : mSelectImageItem.getSelectImageItem()) {
                paths.add(item.path);
            }
            intent.putStringArrayListExtra(RESULT_IMAGES, paths);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void OnFolderSelect(int position) {
        //关闭当前目录弹窗
        closeFolderRv();
        //改变选中的目录
        mFolderAdapter.updateSelectItem(position);
        //切换不同目录下的图集
        mImagesAdapter.setNewData(mFolderList.get(position).images);
        //更换图片目录
        ImageFolder folder = mFolderAdapter.getItem(position);
        mSelectFolderBtn.setText(folder.name);
    }

    @Override
    public void notify(int position) {
        updateImageItemSelect(position);
    }
}
