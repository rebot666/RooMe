package com.rebot.roomme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.rebot.roomme.Models.Users;
import com.todddavies.components.progressbar.ProgressWheel;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Toshiba on 3/05/14.
 */
public class MeLookRoomie extends SherlockActivity{
    private ImageView image, wall_image;
    private ProgressWheel progressWheel;
    private Roome app;
    private Users user;
    private Handler progressHandler = new Handler();
    private int actualProgress;
    private int porcentaje;
    private TextView percentageText, txt_name, txt_edad, txt_localidad, txt_genero;
    private ImageView gender_icon;
    private String idUser;

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.lookme_roomite_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ParseFacebookUtils.initialize(getString(R.string.app_id));

        image = (ImageView) findViewById(R.id.image);
        wall_image = (ImageView) findViewById(R.id.wall_image);
        progressWheel = (ProgressWheel) findViewById(R.id.percentage);
        percentageText = (TextView) findViewById(R.id.percentage_text);
        txt_edad = (TextView) findViewById(R.id.edad);
        txt_name = (TextView) findViewById(R.id.name);
        txt_localidad = (TextView) findViewById(R.id.localidad);
        txt_genero = (TextView) findViewById(R.id.gender);
        gender_icon = (ImageView) findViewById(R.id.gender_icon);

        app = (Roome) getApplication();
        user = app.roomieSeleccionado;

        ParseUser userParseInfo = user.getUser();
        JSONObject profile = userParseInfo.getJSONObject("profile");
        idUser = "";
        try {
            idUser = profile.getString("facebookId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ImageLoader.getInstance().displayImage("http://graph.facebook.com/"+idUser+"/picture?type=large", image,
                app.options, app.animateFirstListener);

        if(userParseInfo.getString("urlFacebookCover") != null){
            ImageLoader.getInstance().displayImage(userParseInfo.getString("urlFacebookCover"), wall_image,
                    app.options2, app.animateFirstListener2);
        }else{
            wall_image.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.background_rounded));
        }

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
        txt_name.setText("Nombre: " + profile.optString("name", ""));

        String genero = profile.optString("gender","");
        if(genero.equalsIgnoreCase("male")){
            gender_icon.setImageResource(R.drawable.male_icon);
        } else {
            gender_icon.setImageResource(R.drawable.female_icon);
        }
        txt_genero.setText("Género: ");


        String localidad = "Localidad: " + profile.optString("location", "");
        if(!localidad.equalsIgnoreCase("Localidad: ")){
            txt_localidad.setText(localidad);
        }

        String edad = "Edad: " + Integer.toString(cumpleanos(profile.optString("birthday")));
        if(!edad.equalsIgnoreCase("Edad: ")){
            txt_edad.setText(edad);
        }else{
            txt_edad.setVisibility(View.GONE);
        }

    }

    public static int cumpleanos(String current){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date actual = new Date();

        try {
            Date birthdayCurrent = formatter.parse(current);

            Calendar cal = Calendar.getInstance();
            cal.setTime(birthdayCurrent);
            int yearCurrent = cal.get(Calendar.YEAR);
            cal.setTime(actual);
            int yearActual = cal.get(Calendar.YEAR);

            return yearActual-yearCurrent;

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return 0;
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

        @SuppressLint("ResourceAsColor")
        @Override
        public void run() {
            if(porcentaje != -1){
                if(actualProgress < porcentaje){
                    actualProgress++;

                    if(actualProgress == 90){
                        progressWheel.setBarColor(getResources().getColor(R.color.quarter));
                        progressWheel.refreshTheWheel();

                    }

                    if(actualProgress == 180){
                        progressWheel.setBarColor(getResources().getColor(R.color.fifty));
                        progressWheel.refreshTheWheel();
                    }

                    if(actualProgress == 270){
                        progressWheel.setBarColor(getResources().getColor(R.color.naranja_circulo_progress));
                        progressWheel.refreshTheWheel();
                    }

                    if(actualProgress == 360){
                        progressWheel.setBarColor(getResources().getColor(R.color.amarillo_r));
                        progressWheel.refreshTheWheel();
                    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
