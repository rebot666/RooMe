package com.rebot.roomme.MeProfile;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.*;
import com.rebot.roomme.Adapters.ServiceCheckAdapter;
import com.rebot.roomme.Models.Services;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import com.rebot.roomme.WorkaroundMapFragment;
import com.todddavies.components.progressbar.ProgressWheel;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Strike on 5/28/14.
 */
public class PublicacionNva extends SherlockFragmentActivity {
    private Roome app;
    private Context ctx = this;
    private Crouton crouton;
    private Button next, back;
    private ParseObject object;

    private ScrollView one;
    private LinearLayout linear_one;
    private EditText titulo, inmueble, precio, direccion;
    private ImageView img_dpto, photo1, photo2, photo3, photo4;
    private CheckBox ch_roomme;
    private RadioGroup radio_trans, radio_gender;

    private ScrollView two;
    private LinearLayout linear_two;
    private EditText descripcion, tamano, plantas;
    private EditText cuartos, banos, cocina;
    private EditText comedor, estacionamiento, adicional;
    private CheckBox amueblado;

    private GridView grid_services;
    //private ArrayList<ParseObject> services;
    private Button plus_service;

    private static final int SELECT_PICTURE = 1;
    private static final int PHOTO_1 = 2;
    private static final int PHOTO_2 = 3;
    private static final int PHOTO_3 = 4;
    private static final int PHOTO_4 = 5;

    private RelativeLayout thirdLayout;

    private RelativeLayout cargandoLayout;
    private LinearLayout noInfoLayout, mapLayout;
    private ProgressWheel loader;

    private GoogleMap googleMap;
    double latitude = 20.613785;
    double longitude = -100.388371;
    private LatLng locationDepartmentPoint;

    private ServiceCheckAdapter adapterGrid;

    private String textoNuevo;
    private ArrayList<Services> nuevosServicios;
    private Bitmap bitmap1, bitmap2;
    private ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInsatance){
        super.onCreate(savedInsatance);
        setContentView(R.layout.publication_new);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        app = (Roome) getApplication();
        next = (Button) findViewById(R.id.btn_siguiente);
        back = (Button) findViewById(R.id.btn_regresar);

        //First Layout
        one = (ScrollView) findViewById(R.id.scrollView);
        linear_one = (LinearLayout) findViewById(R.id.first_detail);
        titulo = (EditText) findViewById(R.id.title_publicacion);
        inmueble = (EditText) findViewById(R.id.txt_inmueble);
        precio = (EditText) findViewById(R.id.txt_precio);
        direccion = (EditText) findViewById(R.id.txt_direccion);

        img_dpto = (ImageView) findViewById(R.id.img_dpto);
        photo1 = (ImageView) findViewById(R.id.photo1);
        photo2 = (ImageView) findViewById(R.id.photo2);
        photo3 = (ImageView) findViewById(R.id.photo3);
        photo4 = (ImageView) findViewById(R.id.photo4);

        ch_roomme = (CheckBox) findViewById(R.id.busco_roomme);

        radio_trans = (RadioGroup) findViewById(R.id.rdgGrupo2);
        radio_gender = (RadioGroup) findViewById(R.id.rdgGrupo);


        //Second Layout
        two = (ScrollView) findViewById(R.id.scrollView2);
        linear_two = (LinearLayout) findViewById(R.id.second_detail);
        descripcion = (EditText) findViewById(R.id.txt_descripcion);
        tamano = (EditText) findViewById(R.id.txt_tamano);
        plantas = (EditText) findViewById(R.id.txt_plantas);
        cuartos = (EditText) findViewById(R.id.txt_cuartos);
        banos = (EditText) findViewById(R.id.txt_banos);
        cocina = (EditText) findViewById(R.id.txt_cocina);
        comedor = (EditText) findViewById(R.id.txt_comedor);
        estacionamiento = (EditText) findViewById(R.id.txt_estacionamiento);
        adicional = (EditText) findViewById(R.id.txt_adicional);

        amueblado = (CheckBox) findViewById(R.id.amueblado);

        //Third Layout
        thirdLayout = (RelativeLayout) findViewById(R.id.layout_three);
        grid_services = (GridView) findViewById(R.id.services_grid);
        app.services = new ArrayList<Services>();
        plus_service = (Button) findViewById(R.id.btn_plus);
        cargandoLayout = (RelativeLayout) findViewById(R.id.loading_info);
        noInfoLayout = (LinearLayout) findViewById(R.id.layout_no_info);
        loader = (ProgressWheel) findViewById(R.id.pw_spinner);

        mapLayout = (LinearLayout) findViewById(R.id.layoutMap);

        nuevosServicios = new ArrayList<Services>();

        bitmap1 = bitmap2 = null;

        if(app.dptoSeleccionado == null){
            loader.spin();
            getQueryServicios();
        } else {

        }

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Location location = getGPS();
        if(location != null){
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(15.5f)
                    //.bearing(330)
                    //.tilt(50)
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }else {

            //location.setLatitude(latitude);
            //location.setLongitude(longitude);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitude, longitude))
                    //.zoom(10.0f)
                    //.bearing(330)
                    //.tilt(50)
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        if(app.dptoSeleccionado != null){
            getQuery();
        }

        back.setVisibility(View.GONE);
        next.setVisibility(View.VISIBLE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linear_one.getVisibility() == View.VISIBLE){

                    if(validaDatos()){
                        guardaDatos();
                        one.setVisibility(View.GONE);
                        linear_one.setVisibility(View.GONE);
                        mapLayout.setVisibility(View.VISIBLE);
                        two.setVisibility(View.VISIBLE);
                        linear_two.setVisibility(View.VISIBLE);
                        back.setVisibility(View.VISIBLE);
                        if(app.dptoSeleccionado != null){
                            cargaDatos();
                        }
                    } else {
                        View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                        title.setText(getResources().getString(R.string.required_fields));
                        subtitle.setVisibility(View.GONE);
                        crouton = Crouton.make(PublicacionNva.this, view);
                        crouton.setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build());
                        crouton.show();
                    }
                } else if(linear_two.getVisibility() == View.VISIBLE){
                    if(validaDatos()){
                        two.setVisibility(View.GONE);
                        linear_two.setVisibility(View.GONE);
                        mapLayout.setVisibility(View.GONE);
                        next.setVisibility(View.GONE);

                        thirdLayout.setVisibility(View.VISIBLE);
                        grid_services.setVisibility(View.VISIBLE);
                        plus_service.setVisibility(View.VISIBLE);
                    } else {
                        View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                        title.setText(getResources().getString(R.string.required_fields));
                        subtitle.setVisibility(View.GONE);
                        crouton = Crouton.make(PublicacionNva.this, view);
                        crouton.setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build());
                        crouton.show();
                    }
                }else{
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linear_one.getVisibility() == View.VISIBLE){
                    back.setVisibility(View.GONE);
                    one.setVisibility(View.VISIBLE);
                    linear_one.setVisibility(View.VISIBLE);
                } else if(linear_two.getVisibility() == View.VISIBLE){
                    back.setVisibility(View.GONE);
                    one.setVisibility(View.VISIBLE);
                    linear_one.setVisibility(View.VISIBLE);
                    mapLayout.setVisibility(View.GONE);
                    two.setVisibility(View.GONE);
                    linear_two.setVisibility(View.GONE);
                    back.setVisibility(View.VISIBLE);
                }else{
                    one.setVisibility(View.GONE);
                    linear_one.setVisibility(View.GONE);
                    two.setVisibility(View.VISIBLE);
                    linear_two.setVisibility(View.VISIBLE);
                    mapLayout.setVisibility(View.VISIBLE);
                    back.setVisibility(View.VISIBLE);
                    linear_two.setVisibility(View.VISIBLE);
                    thirdLayout.setVisibility(View.GONE);
                    grid_services.setVisibility(View.GONE);
                    plus_service.setVisibility(View.GONE);
                    next.setVisibility(View.VISIBLE);

                }
            }
        });

        img_dpto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                String pickTitle = "Select or take a new Picture";
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                chooserIntent.putExtra
                        (
                                Intent.EXTRA_INITIAL_INTENTS,
                                new Intent[] { takePhotoIntent }
                        );

                startActivityForResult(chooserIntent, SELECT_PICTURE);
            }
        });

        photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                String pickTitle = "Select or take a new Picture";
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                chooserIntent.putExtra
                        (
                                Intent.EXTRA_INITIAL_INTENTS,
                                new Intent[] { takePhotoIntent }
                        );

                startActivityForResult(chooserIntent, PHOTO_1);
            }
        });

        photo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                String pickTitle = "Select or take a new Picture";
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                chooserIntent.putExtra
                        (
                                Intent.EXTRA_INITIAL_INTENTS,
                                new Intent[] { takePhotoIntent }
                        );

                startActivityForResult(chooserIntent, PHOTO_2);
            }
        });

        photo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                String pickTitle = "Select or take a new Picture";
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                chooserIntent.putExtra
                        (
                                Intent.EXTRA_INITIAL_INTENTS,
                                new Intent[] { takePhotoIntent }
                        );

                startActivityForResult(chooserIntent, PHOTO_3);
            }
        });

        photo4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                String pickTitle = "Select or take a new Picture";
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                chooserIntent.putExtra
                        (
                                Intent.EXTRA_INITIAL_INTENTS,
                                new Intent[] { takePhotoIntent }
                        );

                startActivityForResult(chooserIntent, PHOTO_4);
            }
        });

        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                two.requestDisallowInterceptTouchEvent(true);
            }

        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                locationDepartmentPoint = point;
                googleMap.clear();
                googleMap.addMarker(
                        new MarkerOptions()
                                .position(point)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_house))
                )
                ;

            }
        });

        plus_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // custom dialog
                final Dialog dialog = new Dialog(ctx);

                dialog.setContentView(R.layout.service_dialog_item);
                dialog.setTitle(getString(R.string.app_name));

                // set the custom dialog components - text, image and button
                final EditText text = (EditText) dialog.findViewById(R.id.nombre_servicio);

                Button dialogButton = (Button) dialog.findViewById(R.id.btn_aceptar);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!text.getText().equals("")){
                            textoNuevo = text.getText().toString();
                            ParseObject parseServicio = new ParseObject("Servicios");
                            parseServicio.put("name", textoNuevo);

                            JSONObject nuevoServ = new JSONObject();
                            try {
                                nuevoServ.put("nombre", textoNuevo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //nuevosServicios.add(nuevoServ);
                            //parseServicio.saveInBackground();
                            Services serv = new Services(parseServicio);
                            serv.setChecked(true);
                            app.services.add(serv);
                            adapterGrid.notifyDataSetChanged();

                            dialog.dismiss();
                        }else{
                            dialog.dismiss();
                        }
                                            }
                });

                dialog.show();




            }
        });

    }

    public void cargaDatos(){
        if(linear_one.getVisibility() == View.VISIBLE){
            String title = object.getString("title");
            if(title != null){
                titulo.setText(title);
            }

            ParseFile file = object.getParseFile("img_portada");
            if(file != null){
                ImageLoader.getInstance().displayImage(file.getUrl(),
                        img_dpto, app.options3, app.animateFirstListener);
            }else{
                ImageLoader.getInstance().displayImage("",
                        img_dpto, app.options3, app.animateFirstListener);
            }


            if(object.getBoolean("roommee")){
                ch_roomme.setChecked(true);
            } else {
                ch_roomme.setChecked(false);
            }

            String transacion = object.getString("transaccion");
            if(transacion != null){
                if(transacion.equalsIgnoreCase("Renta")){
                    radio_trans.check(0);
                } else {
                    radio_trans.check(1);
                }
            }

            String inm = object.getString("categoria");
            if(inm != null){
                inmueble.setText(inm);
            }

            String price = object.getNumber("price") + "";
            if(!price.equalsIgnoreCase("")){
                precio.setText(price);
            }

            String sex = object.getString("sex");
            if(sex.equalsIgnoreCase("B")){
                radio_gender.check(2);
            } else if(sex.equalsIgnoreCase("F")){
                radio_gender.check(0);
            } else if(sex.equalsIgnoreCase("M")){
                radio_gender.check(1);
            }

            ParseFile file1 = object.getParseFile("img_uno");
            if(file1 != null){
                photo1.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(file1.getUrl(),
                        photo1, app.options3, app.animateFirstListener);
            }

            ParseFile file2 = object.getParseFile("img_dos");
            if(file2 != null){
                photo2.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(file2.getUrl(),
                        photo2, app.options3, app.animateFirstListener);
            }

            ParseFile file3 = object.getParseFile("img_tres");
            if(file3 != null){
                photo3.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(file3.getUrl(),
                        photo3, app.options3, app.animateFirstListener);
            }

            ParseFile file4 = object.getParseFile("img_cuatro");
            if(file4 != null){
                photo4.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(file4.getUrl(),
                        photo4, app.options3, app.animateFirstListener);
            }

            String dir = object.getString("address");
            if(dir != null){
                direccion.setText(dir);
            }
            if(object.getParseGeoPoint("location") != null){
                locationDepartmentPoint = new LatLng(object.getParseGeoPoint("location").getLatitude(),
                        object.getParseGeoPoint("location").getLongitude());
                googleMap.clear();
                googleMap.addMarker(
                        new MarkerOptions()
                                .position(locationDepartmentPoint)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_house))
                )
                ;
            }
        }

        if(linear_two.getVisibility() == View.VISIBLE){
            String desc = object.getString("description");
            if(desc != null){
                descripcion.setText(desc);
            }

            double tam = object.getDouble("tamano");
            if(tam >= 0){
                tamano.setText(tam + "");
            }

            double plant = object.getDouble("no_plantas");
            if(plant >= 0){
                plantas.setText(plant + "");
            }

            double room = object.getDouble("no_rooms");
            if(room >= 0){
                cuartos.setText(room + "");
            }

            double bath = object.getDouble("no_banos");
            if(bath >= 0){
                banos.setText(bath + "");
            }

            double cook = object.getDouble("no_cocina");
            if(cook >= 0){
                cocina.setText(cook + "");
            }

            double coom = object.getDouble("comedor");
            if(coom >= 0){
                comedor.setText(coom + "");
            }

            if(object.getBoolean("muebles")){
                amueblado.setChecked(true);
            } else {
                amueblado.setChecked(false);
            }

            double park = object.getDouble("parking");
            if(park >= 0){
                estacionamiento.setText("" + park);
            }

            String add = object.getString("adicionales");
            if(add != null){
                adicional.setText(add);
            }
        }
    }

    public boolean validaDatos(){
        String title = titulo.getText().toString() + "";
        if(title.equalsIgnoreCase("")){
            return false;
        }

        String price = precio.getText().toString() + "";
        if(price.equalsIgnoreCase("")){
            return false;
        }

        String address = direccion.getText().toString() + "";
        if(address.equalsIgnoreCase("")){
            return false;
        }

        return true;
    }

    public void guardaDatos(){
        String title = titulo.getText().toString() + "";
        if(title.equalsIgnoreCase("")){

        }

        String price = precio.getText().toString() + "";
        if(price.equalsIgnoreCase("")){
            //return false;
        }

        String address = direccion.getText().toString() + "";
        if(address.equalsIgnoreCase("")){
            //return false;
        }
    }

    public void getQuery(){
        if(isOnline()){
            ParseQuery<ParseObject> dpto = ParseQuery.getQuery("Departamento");
            dpto.getInBackground(app.dptoSeleccionado.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if(e == null){
                        if(parseObject != null){
                            object = parseObject;
                            cargaDatos();
                        }
                    } else {
                        View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                        title.setVisibility(View.GONE);
                        subtitle.setText(getString(R.string.error_dpto));
                        crouton = Crouton.make(PublicacionNva.this, view);
                        crouton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                crouton.cancel();
                            }
                        });
                        crouton.show();
                    }
                }
            });
        }
    }

    public void getQueryServicios(){
        if(isOnline()){
            ParseQuery<ParseObject> service = ParseQuery.getQuery("Servicios");
            service.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if(e == null){
                        if(parseObjects.size() > 0){
                            app.services.clear();

                            for(ParseObject service : parseObjects){
                                app.services.add(new Services(service));
                            }

                            adapterGrid = new ServiceCheckAdapter(ctx,R.layout.ser_che_layout, app.services, app);
                            grid_services.setAdapter(adapterGrid);

                            cargandoLayout.setVisibility(View.GONE);
                            noInfoLayout.setVisibility(View.GONE);
                            loader.stopSpinning();
                        }else{

                            cargandoLayout.setVisibility(View.GONE);
                            noInfoLayout.setVisibility(View.GONE);
                            loader.stopSpinning();
                        }
                    } else {
                        View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                        title.setVisibility(View.GONE);
                        subtitle.setText(getString(R.string.error_services));
                        crouton = Crouton.make(PublicacionNva.this, view);
                        crouton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                crouton.cancel();
                            }
                        });
                        crouton.show();
                        cargandoLayout.setVisibility(View.GONE);
                        noInfoLayout.setVisibility(View.GONE);
                        loader.stopSpinning();
                    }
                }
            });
        }else{
            cargandoLayout.setVisibility(View.GONE);
            noInfoLayout.setVisibility(View.GONE);
            loader.stopSpinning();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null && data.getData() != null) {
            Uri _uri = data.getData();
            //User had pick an image.
            Cursor cursor = getContentResolver().query(_uri, new String[] {
                    android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            cursor.moveToFirst();

            //Link to the image
            final String imageFilePath = cursor.getString(0);
            Drawable d = Drawable.createFromPath(imageFilePath);


            /*
            do {
                bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
                if (baos.size() > 800)
                    quality = quality * 800 / baos.size();
            } while (baos.size() > 800);
            */

            //Bitmap bMap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);

            if(requestCode == SELECT_PICTURE){
                img_dpto.setImageDrawable(d);
                img_dpto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                bitmap1 = ((BitmapDrawable)d).getBitmap();

            } else if(requestCode == PHOTO_1){
                photo1.setImageDrawable(d);
                photo1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //bitmap2 = ((BitmapDrawable)d).getBitmap();
            } else if(requestCode == PHOTO_2){
                photo2.setImageDrawable(d);
                photo2.setScaleType(ImageView.ScaleType.CENTER_CROP);

            } else if(requestCode == PHOTO_3){
                photo3.setImageDrawable(d);
                photo3.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else if(requestCode == PHOTO_4){
                photo4.setImageDrawable(d);
                photo4.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            cursor.close();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            super.onBackPressed();
            return true;
        }

        if(item.getItemId() == R.id.save){
            //Guardar
            pd = ProgressDialog.show(PublicacionNva.this, getString(R.string.app_name) ,getString(R.string.save_draft),true,false,null);
            if(validaDatos()){
                prepararPublicacion(true);
            }else{
                pd.dismiss();
                View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                title.setText(getResources().getString(R.string.required_fields));
                subtitle.setVisibility(View.GONE);
                crouton = Crouton.make(PublicacionNva.this, view);
                crouton.setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build());
                crouton.show();
            }

        }

        if(item.getItemId() == R.id.publish){
            //Publicar
            pd = ProgressDialog.show(PublicacionNva.this, getString(R.string.app_name) ,getString(R.string.save_publish),true,false,null);
            if(validaDatos()) {
                prepararPublicacion(false);
            }else{
                pd.dismiss();
                View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                title.setText(getResources().getString(R.string.required_fields));
                subtitle.setVisibility(View.GONE);
                crouton = Crouton.make(PublicacionNva.this, view);
                crouton.setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build());
                crouton.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initilizeMap() {
        if (googleMap == null) {
            FragmentManager fmanager = getSupportFragmentManager();
            Fragment fragment = fmanager.findFragmentById(R.id.map);
            SupportMapFragment supportmapfragment = (SupportMapFragment)fragment;
            googleMap = supportmapfragment.getMap();
            //googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    private Location getGPS() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;

        for (int i=providers.size()-1; i>=0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        double[] gps = new double[2];
        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        return l;
    }

    public void prepararPublicacion(boolean isDraftLocal){
        if(isDraftLocal){

        }else{
            pd = ProgressDialog.show(PublicacionNva.this, "Roomme" ,"Subiendo publicación...",true,false,null);
        }

        ParseUser owner = ParseUser.getCurrentUser();
        if(owner != null){
            String title = "";
            String description = "";
            int price = 0;
            Bitmap img_portada = null;
            String address = "";
            ParseGeoPoint location = null;
            String country = "";
            String estado = "";
            String ciudad = "";
            double tamanoParse = 0;
            double no_rooms = 0;
            double no_plantas = 0;
            double no_banos = 0;
            double parking = 0;
            String sex = "B";                                 //M F B
            boolean destacado = false;
            String transaccion = "";                         //venta renta
            int offers = 0;
            boolean roommee = false;
            int no_photos = 1;                              //1
            String adicionales = "";
            boolean isSell = false;
            boolean isDraft = true;
            double comedorParse  = 0;
            boolean muebles = false;
            double no_cocina = 0;

            title = titulo.getText().toString();
            if(ch_roomme.isChecked()){ roommee = true;} else{roommee = false;}
            int index = radio_trans.indexOfChild(findViewById(radio_trans.getCheckedRadioButtonId()));
            if(index == 0){transaccion = "renta";}else{transaccion = "venta";}
            index = radio_gender.indexOfChild(findViewById(radio_gender.getCheckedRadioButtonId()));
            if(index == 0){sex = "F";}else if(index == 1){sex = "M";}else{sex = "B";}
            price = Integer.parseInt(precio.getText().toString());
            address =  direccion.getText().toString();

            description = descripcion.getText().toString();
            if(!tamano.getText().toString().equals("")){
                tamanoParse = Double.parseDouble(tamano.getText().toString());}
            else{
                tamanoParse = 0.0;
            }
            if(!plantas.getText().toString().equals("")) {
                no_plantas = Double.parseDouble(plantas.getText().toString());
            }else{
                no_plantas = 0.0;
            }
            if(!cuartos.getText().toString().equals("")){
                no_rooms = Double.parseDouble(cuartos.getText().toString());
            }else {
                no_rooms = 0.0;
            }
            if(!banos.getText().toString().equals("")) {
                no_banos = Double.parseDouble(banos.getText().toString());
            }else{
                no_banos = 0.0;
            }
            if(!cocina.getText().toString().equals("")) {
                no_cocina = Double.parseDouble(cocina.getText().toString());
            }else{
                no_cocina = 0.0;
            }
            if(!comedor.getText().toString().equals("")){
                comedorParse = Double.parseDouble(comedor.getText().toString());
            }else{
                comedorParse = 0.0;
            }
            if(amueblado.isChecked()){muebles = true;}else{muebles = false;}
            if(!estacionamiento.getText().toString().equals("")) {
                parking = Double.parseDouble(estacionamiento.getText().toString());
            }else{
                parking = 0.0;
            }
            adicionales = adicional.getText().toString();
            if(locationDepartmentPoint == null){
                location = new ParseGeoPoint(latitude,longitude);
            }else{
                location = new ParseGeoPoint(locationDepartmentPoint.latitude, locationDepartmentPoint.longitude);
            }

            ArrayList<JSONObject> servicesParse = new ArrayList<JSONObject>();
            for(Services tempService : app.services){
                if(tempService.isChecked()){
                    nuevosServicios.add(tempService);
                    JSONObject jsonServiceParse = new JSONObject();

                    try {
                        jsonServiceParse.put("id", tempService.getServiceParse().getObjectId());
                        jsonServiceParse.put("name", tempService.getServiceParse().getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    servicesParse.add(jsonServiceParse);

                }
            }

            ParseFile photoFile1, photoFile2;
            photoFile1 = photoFile2 = null;
            if(bitmap1 != null){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray1 = stream.toByteArray();
                photoFile1 = new ParseFile("image1.png", byteArray1);
            }
            if(bitmap2 != null){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap2.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray2 = stream.toByteArray();
                photoFile2 = new ParseFile("image1.png", byteArray2);
            }

            final boolean draft = isDraftLocal;

            if(object != null){
                object.put("owner", owner);
                object.put("title", title);
                object.put("roommee",roommee);
                object.put("transaccion", transaccion);
                object.put("sex",sex);
                object.put("price",price);
                object.put("address", address);
                object.put("description", description);
                object.put("tamano", tamanoParse);
                object.put("no_plantas", no_plantas);
                object.put("no_rooms", no_rooms);
                object.put("no_banos", no_banos);
                object.put("no_cocina", no_cocina);
                object.put("comedor", comedorParse);
                object.put("muebles", muebles);
                object.put("parking", parking);
                object.put("adicionales", adicionales);
                object.put("location", location);
                object.put("offers", 0);
                object.put("destacado", destacado);
                object.put("servicios", servicesParse);
                object.put("isSell", false);
                if(bitmap1 != null){object.put("img_portada", photoFile1);}
                if(bitmap2 != null){object.put("img_uno", photoFile2);}
                if(isDraftLocal){object.put("isDraft", true);}else{object.put("isDraft", false);}
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            pd.dismiss();
                            finalAnswer(draft, e);
                            finish();
                        }
                    }
                });
            }else{
                ParseObject publicacion = new ParseObject("Departamento");
                publicacion.put("owner", owner);
                publicacion.put("title", title);
                publicacion.put("roommee",roommee);
                publicacion.put("transaccion", transaccion);
                publicacion.put("sex",sex);
                publicacion.put("price",price);
                publicacion.put("address", address);
                publicacion.put("description", description);
                publicacion.put("tamano", tamanoParse);
                publicacion.put("no_plantas", no_plantas);
                publicacion.put("no_rooms", no_rooms);
                publicacion.put("no_banos", no_banos);
                publicacion.put("no_cocina", no_cocina);
                publicacion.put("comedor", comedorParse);
                publicacion.put("muebles", muebles);
                publicacion.put("parking", parking);
                publicacion.put("adicionales", adicionales);
                publicacion.put("location", location);
                publicacion.put("offers", 0);
                publicacion.put("destacado", destacado);
                publicacion.put("servicios", servicesParse);
                publicacion.put("isSell", false);                if(bitmap1 != null){publicacion.put("img_portada", photoFile1);}
                if(bitmap2 != null){publicacion.put("img_uno", photoFile2);}
                if(isDraftLocal){publicacion.put("isDraft", true);}else{publicacion.put("isDraft", false);}
                publicacion.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        pd.dismiss();
                        if(e == null){
                            finalAnswer(draft, e);
                            finish();
                        }
                    }
                });
            }

        }
    }

    public void finalAnswer(boolean draft, ParseException e){
        if(e == null){
            if(draft){
                View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                title.setText(getResources().getString(R.string.draft_correct));
                subtitle.setVisibility(View.GONE);
                crouton = Crouton.make(PublicacionNva.this, view);
                crouton.setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build());
                crouton.show();
            }else{
                View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                title.setText(getResources().getString(R.string.publish_correct));
                subtitle.setVisibility(View.GONE);
                crouton = Crouton.make(PublicacionNva.this, view);
                crouton.setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build());
                crouton.show();
            }

        }else{
            if(draft){
                View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                title.setText(getResources().getString(R.string.draft_incorrect));
                subtitle.setVisibility(View.GONE);
                crouton = Crouton.make(PublicacionNva.this, view);
                crouton.setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build());
                crouton.show();
            }else{
                View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                title.setText(getResources().getString(R.string.publish_incorrect));
                subtitle.setVisibility(View.GONE);
                crouton = Crouton.make(PublicacionNva.this, view);
                crouton.setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build());
                crouton.show();
            }
        }
    }
}
