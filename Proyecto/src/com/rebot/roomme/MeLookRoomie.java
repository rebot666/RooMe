package com.rebot.roomme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseUser;
import com.rebot.roomme.Models.Users;
import com.todddavies.components.progressbar.ProgressWheel;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by Toshiba on 3/05/14.
 */
public class MeLookRoomie extends SherlockActivity{
    private ImageView image;
    private ProgressWheel progressWheel;
    private Roome app;
    private Users user;
    private Handler progressHandler = new Handler();
    private int actualProgress;
    private int porcentaje;
    private TextView percentageText;

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.lookme_roomite_layout);

        image = (ImageView) findViewById(R.id.image);
        progressWheel = (ProgressWheel) findViewById(R.id.percentage);
        percentageText = (TextView) findViewById(R.id.percentage_text);

        app = (Roome) getApplication();
        user = app.roomieSeleccionado;

        ParseUser userParseInfo = user.getUser();
        JSONObject profile = userParseInfo.getJSONObject("profile");
        String idUser = "";
        try {
            idUser = profile.getString("facebookId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ImageLoader.getInstance().displayImage("http://graph.facebook.com/"+idUser+"/picture?type=large", image, app.options, app.animateFirstListener);
        porcentaje = (int)user.getPercentage();

        if(porcentaje != -1){
            porcentaje = (porcentaje * 360) / 100;
        }else{
            progressWheel.setVisibility(View.GONE);
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeLookRoomie.this, WebViewFacebook.class);
                MeLookRoomie.this.startActivity(intent);
            }
        });

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("###.##", otherSymbols);

        percentageText.setText(df.format(user.getPercentage()) + "%");

    }

    public void onResume(){
        super.onResume();
        progressWheel.setProgress(0);
        actualProgress = 0;
        progressHandler.postDelayed(progress_status, 0);
    }

    public void onPause(){
        super.onPause();
        progressHandler.removeCallbacks(progress_status);
    }

    private Runnable progress_status = new Runnable(){

        @Override
        public void run() {
            if(porcentaje != -1){
                if(actualProgress < porcentaje){
                    actualProgress++;
                    progressWheel.incrementProgress();
                    progressHandler.postDelayed(progress_status, 5);
                }else{
                    percentageText.setVisibility(View.VISIBLE);
                    progressHandler.removeCallbacks(progress_status);
                }
            }else{
                progressHandler.removeCallbacks(progress_status);
            }

        }
    };
}
