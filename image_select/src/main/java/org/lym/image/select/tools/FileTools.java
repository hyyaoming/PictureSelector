package org.lym.image.select.tools;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import org.lym.image.select.R;
import org.lym.image.select.SelectorSpec;

import java.io.File;
import java.util.List;

/**
 * 文件操作
 *
 * @author ym.li
 * @since 2018年11月5日17:50:44
 */
public class FileTools {

    private static final String TAG = FileTools.class.getSimpleName();

    /**
     * 创建根缓存目录
     *
     * @param context 上下文
     * @return 文件路径
     */
    public static String createRootPath(Context context) {
        String cacheRootPath = "";
        if (isSdCardAvailable()) {
            // /sdcard/Android/data/<application package>/cache
            cacheRootPath = context.getExternalCacheDir().getPath();
        } else {
            // /data/data/<application package>/cache
            cacheRootPath = context.getCacheDir().getPath();
        }
        return cacheRootPath;
    }

    /**
     * 当前sdCard是否挂载
     *
     * @return true or false
     */
    private static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 递归创建文件夹
     *
     * @param dirPath 目录
     * @return 创建失败返回""
     */
    private static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {
                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }

    /**
     * 递归创建文件夹
     *
     * @param file 创建文件
     * @return 创建失败返回""
     */
    public static String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {
                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取图片剪裁后保存的路径
     *
     * @param context   上下文
     * @param imageFile 图片文件
     * @return 图片路径
     */
    private static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                if (cursor != null) {
                    cursor.close();
                }
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 开启剪裁
     *
     * @param activity       activity
     * @param imagePath      图片路径
     * @param mCropImageFile 图片剪裁文件
     * @param selectorSpec   保存图片剪裁信息
     */
    public static void crop(Activity activity, String imagePath, File mCropImageFile, int requestCode, SelectorSpec selectorSpec) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(FileTools.getImageContentUri(activity, new File(imagePath)), "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", selectorSpec.getAspectX());
        intent.putExtra("aspectY", selectorSpec.getAspectY());
        intent.putExtra("outputX", selectorSpec.getOutputX());
        intent.putExtra("outputY", selectorSpec.getOutputY());
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCropImageFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, requestCode);
    }

    @NonNull
    public static File getCropImageFile(Context context) {
        return new File(FileTools.createRootPath(context) + "/" + System.currentTimeMillis() + ".jpg");
    }

    /**
     * 调用系统相机拍照
     *
     * @param activity     Activity
     * @param requestCode  请求码
     * @param selectorSpec 兼容7.0拍照配置
     * @param file         文件路径
     */
    public static void camera(Activity activity, int requestCode, SelectorSpec selectorSpec, File file) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
            FileTools.createFile(file);
            Uri uri = FileProvider.getUriForFile(activity,
                    selectorSpec.getAuthority(), file);
            List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); //Uri.fromFile(tempFile)
            activity.startActivityForResult(cameraIntent, requestCode);
        } else {
            Toast.makeText(activity, activity.getResources().getString(R.string.open_camera_failure), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 创建相机拍照文件存储路径
     *
     * @param context 上下文
     * @return 返回文件
     */
    @NonNull
    public static File getCameraFile(Context context) {
        return new File(createRootPath(context) + "/" + System.currentTimeMillis() + ".jpg");
    }
}
