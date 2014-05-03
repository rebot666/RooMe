package com.rebot.roomme;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.rebot.roomme.Models.Users;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Toshiba on 8/03/14.
 */
public class Roome extends Application {
    private Context context = this;
    public DisplayImageOptions options;
    public ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    public Users roomieSeleccionado;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "jbaHqREvwHAb7zukf78Ot7B9zb6AFsy5NHHUq2SA", "5k7ozpMi1BrlDy1wranTtV2Us5xeytE7M9rn8orL");

        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/abeezee_regular.ttf");
        //FontsOverride.setDefaultFont(this, "MONOSPACE", "MyFontAsset2.ttf");
        //FontsOverride.setDefaultFont(this, "SANS_SERIF", "MyFontAsset3.ttf");

        File cacheDir = StorageUtils.getCacheDirectory(context);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileCount(100)
                .build();

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.roomme_title)
                .showImageForEmptyUri(R.drawable.roomme_title)
                .showImageOnFail(R.drawable.roomme_title)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .displayer(new RoundedBitmapDisplayer(100)) //if want rounded image
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .build();


        ImageLoader.getInstance().init(config);
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }


}
