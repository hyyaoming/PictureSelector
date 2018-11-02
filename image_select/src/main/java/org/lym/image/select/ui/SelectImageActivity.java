package org.lym.image.select.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.lym.image.select.R;
import org.lym.image.select.SelectorSpec;
import org.lym.image.select.adapter.ImagesAdapter;
import org.lym.image.select.bean.ImageFolder;
import org.lym.image.select.bean.ImageItem;
import org.lym.image.select.data.ImageDataSource;
import org.lym.image.select.weight.ImageGridDecoration;
import org.lym.image.select.weight.SuperCheckBox;

import java.util.List;

/**
 * Doc  选择图片Activity
 *
 * @author ym.li
 * @version 2.9.0
 * @since 2018/11/2/002
 */
public class SelectImageActivity extends AppCompatActivity implements ImageDataSource.OnImagesLoadedListener, ImagesAdapter.OnItemClickListener, ImagesAdapter.OnImageSelectListener, View.OnClickListener {
    private ImageDataSource mImageDataSource;
    private RecyclerView mRecyclerView;
    private ImagesAdapter mImagesAdapter;
    private Button mPreviewBtn;
    private Button mSelectFolderBtn;
    private Button mCompleteBtn;
    private SelectorSpec mSelectorSpec;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        mSelectorSpec = SelectorSpec.getInstance();
        initView();
        initAdapter();
        mImageDataSource = new ImageDataSource(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageDataSource.onDestroy();
    }

    private void initView() {
        //init rv
        mRecyclerView = findViewById(R.id.rv_image);
        mRecyclerView.setHasFixedSize(true);
        ((DefaultItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        int spacing = getResources().getDimensionPixelSize(R.dimen.image_grid_spacing);
        mRecyclerView.addItemDecoration(new ImageGridDecoration(mSelectorSpec.getSpanCount(), spacing, false));
        GridLayoutManager manager = new GridLayoutManager(this, mSelectorSpec.getSpanCount());
        manager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        //init btn
        mPreviewBtn = findViewById(R.id.btn_preview);
        mSelectFolderBtn = findViewById(R.id.btn_image_folder);
        mCompleteBtn = findViewById(R.id.btn_complete);
        mCompleteBtn.setVisibility(mSelectorSpec.getMaxSelectImage() > 1 ? View.VISIBLE : View.GONE);
        //init listener
        mPreviewBtn.setOnClickListener(this);
        mSelectFolderBtn.setOnClickListener(this);
    }

    private void initAdapter() {
        mImagesAdapter = new ImagesAdapter(null, mRecyclerView);
        mImagesAdapter.setOnItemClickListener(this);
        mImagesAdapter.setOnImageSelectListener(this);
        mRecyclerView.setAdapter(mImagesAdapter);
    }

    @Override
    public void onImagesLoaded(List<ImageFolder> imageFolders) {
        if (null != imageFolders && !imageFolders.isEmpty()) {
            mImagesAdapter.setNewData(imageFolders.get(0).images);
            resetButton();
        }
    }

    private void resetButton() {
        int selectImageCount = mImagesAdapter.getSelectImageCount();
        String previewCount = String.format(getString(R.string.preview_image_count), String.valueOf(selectImageCount));
        mPreviewBtn.setText(previewCount);
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
    }

    @Override
    public void onImageSelect(SuperCheckBox checkBox, int position) {
        imageItemSelect(position);
    }

    private void imageItemSelect(int position) {
        ImageItem imageItem = mImagesAdapter.getItem(position);
        imageItem.checked = !imageItem.checked;
        mImagesAdapter.notifyItemChanged(position);
        resetButton();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_image_folder) {
            Toast.makeText(this, "选择相册目录", Toast.LENGTH_LONG).show();
        } else if (viewId == R.id.btn_preview) {
            Toast.makeText(this, "预览图片", Toast.LENGTH_LONG).show();
        }
    }
}
