package com.rebot.roomme;

import android.app.Application;
import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.widget.*;
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
import com.parse.ParseObject;
import com.rebot.roomme.Models.Users;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Toshiba on 8/03/14.
 */
public class Roome extends Application {
    private static Context context;
    public DisplayImageOptions options, options2, options3;
    public ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    public ImageLoadingListener animateFirstListener2 = new AnimateFirstDisplayListener2();
    public Users roomieSeleccionado;
    public ParseObject dptoSeleccionado, ofertaSeleccionada;
    public boolean user;
    public ListView dpto, list_roomie;
    public LinearLayout noInfo;

    //Búsqueda Variables
    //Departamento
    public int rooms, maxprice, minprice;
    public boolean photos;
    public String trans;

    //Roomies
    public int maxcomp, mincomp;
    public String genre;
    public int age;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
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

        options2 = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.roomme_title)
                .showImageForEmptyUri(R.drawable.roomme_title)
                .showImageOnFail(R.drawable.roomme_title)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .displayer(new RoundedBitmapDisplayer(7)) //if want rounded image
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .build();

        options3 = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.roomme_title)
                .showImageForEmptyUri(R.drawable.roomme_title)
                .showImageOnFail(R.drawable.roomme_title)
                .cacheInMemory(true)
                .cacheOnDisc(true)
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

    private static class AnimateFirstDisplayListener2 extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                Bitmap newImage = applyReflection(loadedImage);
                Bitmap roundImage = roundCorner(newImage, 15);
                imageView.setImageBitmap(roundImage);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                //imageView.setBackground(context.getResources().getDrawable(R.drawable.background_rounded));

                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    public static Bitmap applyReflection(Bitmap originalImage) {
        // gap space between original and reflected
        final int reflectionGap = 4;
        // get image size
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // this will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        // create a Bitmap with the flip matrix applied to it.
        // we only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height/2, width, height/2, matrix, false);

        // create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/2), Bitmap.Config.ARGB_8888);

        // create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        // draw in the gap
        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        // draw in the reflection
        canvas.drawBitmap(reflectionImage,0, height + reflectionGap, null);

        // create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
                Shader.TileMode.CLAMP);
        // set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

        return bitmapWithReflection;
    }

    public static Bitmap roundCorner(Bitmap src, float round) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // set canvas for painting
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        // config paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        // config rectangle for embedding
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        // draw rect to canvas
        canvas.drawRoundRect(rectF, round, round, paint);

        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(src, rect, rect, paint);

        // return final image
        return result;
    }


}
