package com.and2long.mediapicker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.VideoView;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView mImageView;
    private VideoView mVideoView;
    private Intent intent;
    private static int PHOTO_REQUEST_GALLERY = 1;           //相册
    private static int PHOTO_REQUEST_CAREMA = 2;            //相机
    private static int PHOTO_REQUEST_CUT = 3;               //裁剪
    private static int VIDEO_REQUEST = 4;                   //视频
    private static String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_photopicker).setOnClickListener(this);
        findViewById(R.id.btn_videopicker).setOnClickListener(this);
        mImageView = (ImageView) findViewById(R.id.iv_imageview);
        mVideoView = (VideoView) findViewById(R.id.vv_videoview);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO_REQUEST_GALLERY) {
                //从相册返回数据。
                crop(data.getData());
            } else if (requestCode == PHOTO_REQUEST_CAREMA) {
                //从相机返回的数据
                crop(Uri.fromFile(tempFile));
            } else if (requestCode == PHOTO_REQUEST_CUT) {
                // 从剪切图片返回的数据
                if (data != null) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    mImageView.setImageBitmap(bitmap);
                }
                try {
                    // 将临时文件删除
                    tempFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == VIDEO_REQUEST) {
                Uri uri = data.getData();
                System.out.println("uri:"+uri.toString());
                showVideoView();
                mVideoView.setVideoURI(uri);
                mVideoView.start();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_photopicker:
                showPopupWindown();
                break;
            case R.id.btn_videopicker:
                intent = new Intent();
                intent.setType("video/*.mp4");
                startActivityForResult(intent, VIDEO_REQUEST);
                break;
            case R.id.btn_fromgallery:
                popupWindow.dismiss();
                gallery();
                break;
            case R.id.btn_fromcamera:
                popupWindow.dismiss();
                camera();
                break;
            case R.id.btn_cancel:
                popupWindow.dismiss();
                break;
        }
    }


    /**
     * 显示PopupWindown
     */
    private void showPopupWindown() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popupwindow_photopicker, null);

        popupWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置popupWindow弹出窗体可点击
        popupWindow.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(Color.WHITE);
        popupWindow.setBackgroundDrawable(dw);
        // 设置popupWindow的显示和消失动画
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 在底部显示
        popupWindow.showAtLocation(MainActivity.this.findViewById(R.id.btn_photopicker),
                Gravity.BOTTOM, 0, 0);
        //设置点击事件
        view.findViewById(R.id.btn_fromgallery).setOnClickListener(this);
        view.findViewById(R.id.btn_fromcamera).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);

    }

    /**
     * 显示图片。
     */
    private void showImageView() {
        mImageView.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.GONE);
    }

    /**
     * 显示视频。
     */
    private void showVideoView() {
        mImageView.setVisibility(View.GONE);
        mVideoView.setVisibility(View.VISIBLE);
    }

    /**
     * 从相册获取
     */
    public void gallery() {
        intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /**
    * 从相机获取
    */
    public void camera() {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    PHOTO_FILE_NAME);
            // 从文件中创建uri
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /**
     * 剪切图片
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例 1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 判断sdcard是否被挂载
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
