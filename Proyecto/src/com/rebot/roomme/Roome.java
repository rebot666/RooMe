package com.rebot.roomme;

import android.app.Application;
import com.parse.Parse;

/**
 * Created by Toshiba on 8/03/14.
 */
public class Roome extends Application {

    public void onCreate() {
        Parse.initialize(this, "jbaHqREvwHAb7zukf78Ot7B9zb6AFsy5NHHUq2SA", "5k7ozpMi1BrlDy1wranTtV2Us5xeytE7M9rn8orL");
    }
}
