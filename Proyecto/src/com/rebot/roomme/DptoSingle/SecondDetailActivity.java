package com.rebot.roomme.DptoSingle;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Strike on 6/6/14.
 */
public class SecondDetailActivity extends FragmentActivity {
    private Roome app;
    private Context ctx = this;

    private TextView descripcion, tamano, plantas, cuartos, banos;
    private TextView cocina, comedor, amueblado, estacionamiento;
    private TextView adicional, user_since, modify;

    private LinearLayout linear_comment;
    private ListView comments;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.second_detail_dpto);

        app = (Roome) getApplication();

        descripcion = (TextView) findViewById(R.id.txt_descripcion);
        tamano = (TextView) findViewById(R.id.txt_tamano);
        plantas = (TextView) findViewById(R.id.txt_plantas);
        cuartos = (TextView) findViewById(R.id.txt_cuartos);
        banos = (TextView) findViewById(R.id.txt_banos);

        cocina = (TextView) findViewById(R.id.txt_cocina);
        comedor = (TextView) findViewById(R.id.txt_comedor);
        amueblado = (TextView) findViewById(R.id.txt_amueblado);
        estacionamiento = (TextView) findViewById(R.id.txt_estacionamiento);

        adicional = (TextView) findViewById(R.id.txt_adicional);
        user_since = (TextView) findViewById(R.id.txt_user_since);
        modify = (TextView) findViewById(R.id.txt_modif);

        linear_comment = (LinearLayout) findViewById(R.id.linear_comentarios);
        comments = (ListView) findViewById(R.id.list_comments);

        ParseObject dpto = app.dptoSeleccionado;

        descripcion.setText(dpto.getString("description"));

        double tam = dpto.getDouble("tamano");
        if(tam >= 0){
            tamano.setVisibility(View.VISIBLE);
            tamano.setText("Tamaño:\t" + tam);
        } else {
            tamano.setVisibility(View.GONE);
        }

        double plant = dpto.getDouble("no_plantas");
        if(plant >= 0){
            plantas.setVisibility(View.VISIBLE);
            plantas.setText("Plantas:\t" + plant);
        } else {
            plantas.setVisibility(View.GONE);
        }

        double room = dpto.getDouble("no_rooms");
        if(room >= 0){
            cuartos.setVisibility(View.VISIBLE);
            cuartos.setText("Cuartos:\t" + room);
        } else {
            cuartos.setVisibility(View.GONE);
        }

        double bath = dpto.getDouble("no_banos");
        if(bath >= 0){
            banos.setVisibility(View.VISIBLE);
            banos.setText("Baños:\t" + bath);
        } else {
            banos.setVisibility(View.GONE);
        }

        double cook = dpto.getDouble("no_cocina");
        if(cook >= 0){
            cocina.setVisibility(View.VISIBLE);
            cocina.setText("Cocina:\t" + cook);
        } else {
            cocina.setVisibility(View.GONE);
        }

        double coom = dpto.getDouble("comedor");
        if(coom >= 0){
            comedor.setVisibility(View.VISIBLE);
            comedor.setText("Comedor:\t" + coom);
        } else {
            comedor.setVisibility(View.GONE);
        }

        if(dpto.getBoolean("muebles")){
            amueblado.setVisibility(View.VISIBLE);
            amueblado.setText("Amueblado:\tSI");
        } else {
            amueblado.setText("Amueblado:\tNO");
        }

        double park = dpto.getDouble("parking");
        if(park >= 0){
            estacionamiento.setVisibility(View.VISIBLE);
            estacionamiento.setText("Estacionamiento:" + park);
        } else {
            estacionamiento.setVisibility(View.GONE);
        }

        String add = dpto.getString("adicionales");
        if(add != null){
            adicional.setText(add);
        } else {
            adicional.setText("Ninguna");
        }

        ParseUser user = dpto.getParseUser("owner");
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date today = user.getCreatedAt();
        String reportDate = df.format(today);
        user_since.setText(reportDate);

        Date updated = dpto.getUpdatedAt();
        reportDate = df.format(updated);
        modify.setText(reportDate);
    }
}