package com.rebot.roomme;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.rebot.roomme.MeLook.MelookActivity;
import com.rebot.roomme.Models.Users;
import com.todddavies.components.progressbar.ProgressWheel;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
    private LinearLayout nameLayout, ageLayout, localityLayout, cardBackgroundLayout;
    private Button btn_reporte;
    private Context context;
    private Crouton crouton;

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
        nameLayout = (LinearLayout) findViewById(R.id.name_layout);
        ageLayout = (LinearLayout) findViewById(R.id.age_layout);
        localityLayout = (LinearLayout) findViewById(R.id.locality_layout);
        cardBackgroundLayout = (LinearLayout) findViewById(R.id.card_background_layout);
        btn_reporte = (Button) findViewById(R.id.btn_reporte);

        app = (Roome) getApplication();
        user = app.roomieSeleccionado;

        YoYo.with(Techniques.FadeInDown)
                .duration(2000)
                .playOn(cardBackgroundLayout);

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

        porcentaje = (int)user.getPercentage();

        if(porcentaje != -1){
            porcentaje = (porcentaje * 360) / 100;
        }else{
            progressWheel.setVisibility(View.GONE);
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MeLookRoomie.this, WebViewFacebook.class);
                //MeLookRoomie.this.startActivity(intent);
                if(user.getUser().getJSONObject("profile") != null){
                    String uri = null;

                    try {
                        uri = "fb://profile/" + user.getUser().getJSONObject("profile").getString("facebookId");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }

            }
        });

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("###.##", otherSymbols);

        percentageText.setText(df.format(user.getPercentage()) + "%");
        txt_name.setText("" + profile.optString("name", ""));

        String genero = profile.optString("gender","");
        if(genero.equalsIgnoreCase("male")){
            gender_icon.setImageResource(R.drawable.male_icon);
        } else {
            gender_icon.setImageResource(R.drawable.female_icon);
        }
        txt_genero.setText("");


        String localidad = profile.optString("location", "");
        if(localidad.equals("")){
            localityLayout.setVisibility(View.GONE);
        }else{
            if(!localidad.equalsIgnoreCase("Localidad: ")){
                txt_localidad.setText(localidad);
            }else{
                localityLayout.setVisibility(View.GONE);
            }
        }


        String edad = "" + Integer.toString(cumpleanos(profile.optString("birthday"))) + " años";
        if(!edad.equalsIgnoreCase("Edad: ")){
            txt_edad.setText(edad);
        }else{
            txt_edad.setVisibility(View.GONE);
            ageLayout.setVisibility(View.GONE);
        }

        btn_reporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportUser();
            }
        });

        InternetConnection connection = new InternetConnection();
        connection.execute();
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

    private class InternetConnection extends AsyncTask<String,Void,String> {
        private ProgressDialog pd;
        private JSONObject images;

        @Override
        protected void onPreExecute(){
            //pd = ProgressDialog.show(Melo.this, getString(R.string.lblAuth) ,getString(R.string.lblSigning),true,false,null);
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";

            String urlCover = "http://graph.facebook.com/"+idUser+"?fields=cover";

            URL url = null;
            try {
                url = new URL(urlCover);
                URLConnection urlConnection = url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader readerin = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(readerin);

                String res = "";
                for (String line = null; (line = reader.readLine()) != null;) {
                    res +=line;
                }

                images = new JSONObject(res);
                //System.out.println("Salida->" + contactos.toString());
            } catch (Exception e){

            }

            return response;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void onPostExecute(String result){
            try {
                String cover = images.optJSONObject("cover").optString("source");
                ImageLoader.getInstance().displayImage(cover, wall_image,
                        app.options2, app.animateFirstListener2);
            } catch (Exception e){
                wall_image.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.background_rounded));
            }

        }
    }

    public void reportUser(){
        if(ParseUser.getCurrentUser() != null){
            ParseUser owner = user.getUser();
            owner.put("reported", true);
            owner.saveInBackground();

            View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
            title.setText(context.getString(R.string.lbl_report_message));
            subtitle.setVisibility(View.GONE);
            crouton = Crouton.make(MeLookRoomie.this, view);
            crouton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    crouton.cancel();
                }
            });
        } else {
            View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
            title.setText(getResources().getString(R.string.user_enrolled));
            subtitle.setVisibility(View.GONE);
            crouton = Crouton.make(MeLookRoomie.this, view);
            crouton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    crouton.cancel();
                }
            });
            crouton.show();
        }
    }
}