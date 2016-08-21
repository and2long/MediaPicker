package com.and2long.mediapicker;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by and2long on 16-8-21.
 */
public class VideoPickerActivity extends AppCompatActivity {
    private List<VideoInfo> videoInfos = new ArrayList<VideoInfo>();
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videopicker);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_videos);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        final VideoPickerAdapter adapter = new VideoPickerAdapter(this, videoInfos);
        mRecyclerView.setAdapter(adapter);
        new Thread(){
            @Override
            public void run() {
                super.run();
                videoInfos = getVideoFile(VideoPickerActivity.this.videoInfos, Environment.getExternalStorageDirectory());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();
    }



    /**
     * 获取视频文件
     *
     * @param list
     * @param file
     * @return
     */
    private List<VideoInfo> getVideoFile(final List<VideoInfo> list, File file) {

        file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {

                String name = file.getName();

                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".mp4")) {
                        VideoInfo video = new VideoInfo();
                        video.setPath(file.getAbsolutePath());
                        list.add(video);
                        return true;
                    }
                    //判断是不是目录
                } else if (file.isDirectory()) {
                    getVideoFile(list, file);
                }
                return false;
            }
        });

        return list;
    }

    /**
     * 10M=10485760 b,小于10m的过滤掉
     * 过滤视频文件
     *
     * @param videoInfos
     * @return
     */
    private List<VideoInfo> filterVideo(List<VideoInfo> videoInfos) {
        List<VideoInfo> newVideos = new ArrayList();
        for (VideoInfo videoInfo : videoInfos) {
            File f = new File(videoInfo.getPath());
            if (f.exists() && f.isFile() && f.length() > 10485760) {
                newVideos.add(videoInfo);
            }
        }
        return newVideos;
    }

    /**
     * 按视频时长过滤
     * @param videoInfos
     * @return
     */
    private List<VideoInfo> filterVideoDuration(List<VideoInfo> videoInfos) {
        List<VideoInfo> newVideos = new ArrayList();
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        for (VideoInfo videoInfo : videoInfos) {
            File f = new File(videoInfo.getPath());
            if (f.exists() && f.isFile()) {
                mmr.setDataSource(f.getPath());
                String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                //Log.e("视频时长", duration);
                if (Integer.parseInt(duration) > 3000) {
                    newVideos.add(videoInfo);
                }
            }
        }
        return newVideos;
    }
}
