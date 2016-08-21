package com.and2long.mediapicker;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by and2long on 16-8-21.
 */
public class App extends Application {

    public static ImageLoader imageLoader;
    @Override
    public void onCreate() {
        super.onCreate();
        imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        imageLoader.init(configuration);
    }
}
