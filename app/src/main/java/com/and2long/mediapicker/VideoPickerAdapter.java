package com.and2long.mediapicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by and2long on 16-8-21.
 */
public class VideoPickerAdapter extends RecyclerView.Adapter<VideoPickerAdapter.MyViewHolder> {

    private Context context;
    private List<VideoInfo> videoInfos;
    private MediaMetadataRetriever mmr;
    private Bitmap bitmap;

    public VideoPickerAdapter(Context context, List<VideoInfo> videoInfos) {
        this.context = context;
        this.videoInfos = videoInfos;
        this.mmr = new MediaMetadataRetriever();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_videopicker, null));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        mmr.setDataSource(videoInfos.get(position).getPath());
        bitmap = mmr.getFrameAtTime();
        holder.image.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return videoInfos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv_imageprevire);
        }
    }
}
