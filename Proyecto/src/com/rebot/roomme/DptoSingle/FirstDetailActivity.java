package com.rebot.roomme.DptoSingle;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.plus.model.people.Person;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.*;
import com.rebot.roomme.CBR;
import com.rebot.roomme.MeLookRoomie;
import com.rebot.roomme.Models.Users;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import com.todddavies.components.progressbar.ProgressWheel;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Strike on 6/6/14.
 */
public class FirstDetailActivity extends FragmentActivity {
    private Roome app;
    private ImageView img_dpto, ribbon_roomee, img_gender;
    private ImageView photo1, photo2, photo3, photo4;

    private Button btn_one, btn_three;
    private TextView txt_trans, txt_inmueble, txt_precio;
    private TextView title_publicacion, txt_direccion, txt_fecha;

    private LinearLayout linear_gender;
    private LinearLayout linear_photos;
    private LinearLayout no_connection;
    private RelativeLayout loading_info;
    private ParseUser currentUser;
    private Crouton crouton;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.first_detail_dpto);

        app = (Roome) getApplication();

        img_dpto = (ImageView) findViewById(R.id.img_dpto);
        ribbon_roomee = (ImageView) findViewById(R.id.ribbon_roomme);
        img_gender = (ImageView) findViewById(R.id.img_genero);

        photo1 = (ImageView) findViewById(R.id.photo1);
        photo2 = (ImageView) findViewById(R.id.photo2);
        photo3 = (ImageView) findViewById(R.id.photo3);
        photo4 = (ImageView) findViewById(R.id.photo4);

        btn_one = (Button) findViewById(R.id.btn_one);
        btn_three = (Button) findViewById(R.id.btn_three);

        txt_trans = (TextView) findViewById(R.id.text_transaccion);
        txt_inmueble = (TextView) findViewById(R.id.txt_inmueble);
        txt_precio = (TextView) findViewById(R.id.txt_precio);
        title_publicacion = (TextView) findViewById(R.id.title_publicacion);
        txt_direccion = (TextView) findViewById(R.id.txt_direccion);
        txt_fecha = (TextView) findViewById(R.id.txt_fecha);

        no_connection = (LinearLayout) findViewById(R.id.no_connection);
        loading_info = (RelativeLayout) findViewById(R.id.loading_info);
        linear_gender = (LinearLayout) findViewById(R.id.linear_genero);
        linear_photos = (LinearLayout) findViewById(R.id.linear_photos);
        loading_info.setVisibility(View.GONE);
        //pw_loading.spin();

        if(app.user){
            btn_one.setVisibility(View.GONE);
        }else{
            validaFavorito();
            currentUser = ParseUser.getCurrentUser();
            btn_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isOnline()){
                        favorito();
                    }else{
                        View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                        title.setText(getResources().getString(R.string.no_connection));
                        subtitle.setVisibility(View.GONE);
                        crouton = Crouton.make(FirstDetailActivity.this, view);
                        crouton.setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build());
                        crouton.show();
                    }

                }
            });

        }

        //Carga de datos
        final ParseObject dpto = app.dptoSeleccionado;

        //Imagen de Portada
        ParseFile file = dpto.getParseFile("img_portada");
        if(file != null){
            ImageLoader.getInstance().displayImage(file.getUrl(),
                    img_dpto, app.options3, app.animateFirstListener);
        }else{
            img_dpto.setScaleType(ImageView.ScaleType.FIT_XY);
            ImageLoader.getInstance().displayImage("",
                    img_dpto, app.options3, app.animateFirstListener);
        }


        if(dpto.getBoolean("roommee")){
            ribbon_roomee.setVisibility(View.VISIBLE);
        } else {
            ribbon_roomee.setVisibility(View.GONE);
        }

        String sex = dpto.getString("sex");
        if(sex.equalsIgnoreCase("B")){
            linear_gender.setVisibility(View.VISIBLE);
            img_gender.setImageResource(R.drawable.both);
        } else if(sex.equalsIgnoreCase("F")){
            linear_gender.setVisibility(View.VISIBLE);
            img_gender.setImageResource(R.drawable.female);
        } else if(sex.equalsIgnoreCase("M")){
            linear_gender.setVisibility(View.VISIBLE);
            img_gender.setImageResource(R.drawable.male);
        } else {
            linear_gender.setVisibility(View.GONE);
        }

        //Imágenes extra
        int count = 0;
        ParseFile file1 = dpto.getParseFile("img_uno");
        if(file1 != null){
            photo1.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(file1.getUrl(),
                    photo1, app.options3, app.animateFirstListener);
        } else {
            photo1.setVisibility(View.GONE);
            count++;
        }

        ParseFile file2 = dpto.getParseFile("img_dos");
        if(file2 != null){
            photo2.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(file2.getUrl(),
                    photo2, app.options3, app.animateFirstListener);
        } else {
            photo2.setVisibility(View.GONE);
            count++;
        }

        ParseFile file3 = dpto.getParseFile("img_tres");
        if(file3 != null){
            photo3.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(file3.getUrl(),
                    photo3, app.options3, app.animateFirstListener);
        } else {
            //ribbon_roomee.setVisibility(View.GONE);
            photo3.setVisibility(View.GONE);
            count++;
        }

        ParseFile file4 = dpto.getParseFile("img_cuatro");
        if(file4 != null){
            photo4.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(file4.getUrl(),
                    photo4, app.options3, app.animateFirstListener);
        } else {
            photo4.setVisibility(View.GONE);
            count++;
        }

        /*if(count >= 4){
            linear_photos.setVisibility(View.GONE);
        } else {
            linear_photos.setVisibility(View.VISIBLE);
        }*/

        //TextViews
        String transacion = dpto.getString("transaccion");
        if(transacion != null){
            txt_trans.setVisibility(View.VISIBLE);
            txt_trans.setText(transacion);
        } else {
            txt_trans.setVisibility(View.GONE);
        }

        String inmueble = dpto.getString("categoria");
        if(inmueble != null){
            txt_inmueble.setVisibility(View.VISIBLE);
            txt_trans.setText(inmueble);
        } else {
            txt_inmueble.setVisibility(View.GONE);
        }


        String precio = dpto.getNumber("price") + "";
        if(precio.equalsIgnoreCase("")){
            txt_precio.setVisibility(View.GONE);
        } else {
            double precio1 = dpto.getNumber("price").floatValue();
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
            otherSymbols.setDecimalSeparator('.');
            otherSymbols.setGroupingSeparator(',');
            DecimalFormat df = new DecimalFormat("###,###.00");
            df.setDecimalFormatSymbols(otherSymbols);

            txt_precio.setVisibility(View.VISIBLE);
            txt_precio.setText(getString(R.string.lbl_precio) +" $" + df.format(precio1));
        }

        title_publicacion.setText(dpto.getString("title"));
        txt_direccion.setText(dpto.getString("address"));

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date today = dpto.getCreatedAt();
        String reportDate = df.format(today);
        txt_fecha.setText(getString(R.string.lbl_published_at)+ " " + reportDate);

        photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable photo = photo1.getDrawable();
                Drawable img = img_dpto.getDrawable();

                img_dpto.setImageDrawable(photo);
                photo1.setImageDrawable(img);
            }
        });

        photo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable photo = photo2.getDrawable();
                Drawable img = img_dpto.getDrawable();

                img_dpto.setImageDrawable(photo);
                photo2.setImageDrawable(img);
            }
        });

        photo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable photo = photo3.getDrawable();
                Drawable img = img_dpto.getDrawable();

                img_dpto.setImageDrawable(photo);
                photo3.setImageDrawable(img);
            }
        });

        photo4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable photo = photo4.getDrawable();
                Drawable img = img_dpto.getDrawable();

                img_dpto.setImageDrawable(photo);
                photo4.setImageDrawable(img);
            }
        });

        btn_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseGeoPoint geoPoint = dpto.getParseGeoPoint("location");
                String myLatitude = String.valueOf(geoPoint.getLatitude());
                String myLongitude = String.valueOf(geoPoint.getLongitude());
                String labelLocation = dpto.getString("address");

                String urlAddress = "http://maps.google.com/maps?q="+ myLatitude  +"," + myLongitude
                        +"("+ labelLocation + ")&iwloc=A&hl=es";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress));
                startActivity(intent);
            }
        });
    }

    public void validaFavorito(){
        if(ParseUser.getCurrentUser() != null){
            List<String> listObjectId = ParseUser.getCurrentUser().getList("favorites");
            if(listObjectId != null && listObjectId.size() > 0){
                boolean enFavoritos = false;
                for (String temp : listObjectId) {
                    if(app.dptoSeleccionado.getObjectId().equals(temp)){
                        enFavoritos = true;
                        break;
                    }
                }
                if(enFavoritos){
                    btn_one.setText(getString(R.string.btn_remove_favorites));
                }else{
                    btn_one.setText(getString(R.string.btn_add_favorites));
                }
            }else{
                btn_one.setText(getString(R.string.btn_add_favorites));
            }

        }
    }

    public void favorito(){
        if(currentUser != null){
            if(currentUser.getList("favorites") != null){
                List<String> favorites = currentUser.getList("favorites");
                if(favorites.size() <= 0){
                    favorites.add(app.dptoSeleccionado.getObjectId());
                    btn_one.setText(getString(R.string.btn_remove_favorites));
                }else{
                    boolean encontrado = false;
                    int indx = -1;
                    for (int i = 0; i <favorites.size() ; i++) {
                        if(app.dptoSeleccionado.getObjectId().equals(favorites.get(i))){
                            encontrado = true;
                            indx = i;
                            break;
                        }
                    }
                    if(encontrado){
                        favorites.remove(indx);
                        //likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.like_icon));
                        btn_one.setText(getString(R.string.btn_add_favorites));

                    }else{
                        favorites.add(app.dptoSeleccionado.getObjectId());
                        //likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.like_icon_fill));
                        btn_one.setText(getString(R.string.btn_remove_favorites));
                    }

                }

                currentUser.put("favorites", favorites);
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){

                        }
                    }
                });

            }else{
                ArrayList<String> favorites = new ArrayList<String>();
                favorites.add(app.dptoSeleccionado.getObjectId());
                //likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.like_icon_fill));
                btn_one.setText(getString(R.string.btn_add_favorites));

                currentUser.put("favorites", favorites);
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){

                        }
                    }
                });
            }
        }else{
            View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
            title.setText(getResources().getString(R.string.user_enrolled));
            subtitle.setVisibility(View.GONE);
            crouton = Crouton.make(FirstDetailActivity.this, view);
            crouton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    crouton.cancel();
                }
            });
            crouton.show();
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
