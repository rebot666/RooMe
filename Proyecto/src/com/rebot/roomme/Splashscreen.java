package com.rebot.roomme;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;

/**
 * Created by Toshiba on 8/03/14.
 */
public class Splashscreen extends SherlockActivity {
    /**
     * Called when the activity is first created.
     */

    private boolean mIsBackButtonPressed;
    private static final int SPLASH_DURATION = 3000; // Tiempo del Splash
    private TextView slogan1, slogan2, title;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.splashscreen_layout);
        slogan1 = (TextView) findViewById(R.id.slogan1);
        slogan2 = (TextView) findViewById(R.id.slogan2);

        Typeface fontslogan = Typeface.createFromAsset(getAssets(), "fonts/abeezee_regular.ttf");
        slogan1.setTypeface(fontslogan);
        slogan2.setTypeface(fontslogan);

        Handler handler = new Handler();

        // run a thread after 2 seconds to start the home screen
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                // make sure we close the splash screen so the user won't come back when it presses back key

                finish();

                if (!mIsBackButtonPressed) {
                    // start the home screen if the back button wasn't pressed already
                    //Intent intent = new Intent(splashscreen.this, Main.class);

                    Intent intent = new Intent(Splashscreen.this, MainDrawer.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //finish();
                    Splashscreen.this.startActivity(intent);


                }

            }

        }, SPLASH_DURATION);
    }
}