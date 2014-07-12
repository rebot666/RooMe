package com.rebot.roomme.MeProfile;

import android.app.Dialog;
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
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import com.rebot.roomme.WorkaroundMapFragment;
import com.todddavies.components.progressbar.ProgressWheel;
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
    private ArrayList<ParseObject> services;
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
    private ArrayList<JSONObject> nuevosServicios;

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
        services = new ArrayList<ParseObject>();
        plus_service = (Button) findViewById(R.id.btn_plus);
        cargandoLayout = (RelativeLayout) findViewById(R.id.loading_info);
        noInfoLayout = (LinearLayout) findViewById(R.id.layout_no_info);
        loader = (ProgressWheel) findViewById(R.id.pw_spinner);

        mapLayout = (LinearLayout) findViewById(R.id.layoutMap);

        nuevosServicios = new ArrayList<JSONObject>();

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
                        title.setVisibility(View.GONE);
                        subtitle.setText("Favor de verificar que los datos sean correctos");
                        crouton = Crouton.make(PublicacionNva.this, view);
                        crouton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                crouton.cancel();
                            }
                        });
                        crouton.show();
                    }
                } else if(linear_two.getVisibility() == View.VISIBLE){
                    if(validaDatos()){
                        two.setVisibility(View.GONE);
                        linear_two.setVisibility(View.GONE);
                        mapLayout.setVisibility(View.GONE);
                        next.setVisibility(View.GONE);
                        if(app.dptoSeleccionado == null){
                            loader.spin();
                            getQueryServicios();
                        } else {

                        }
                        thirdLayout.setVisibility(View.VISIBLE);
                        grid_services.setVisibility(View.VISIBLE);
                        plus_service.setVisibility(View.VISIBLE);
                    } else {
                        View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                        title.setVisibility(View.GONE);
                        subtitle.setText("Favor de verificar que los datos sean correctos");
                        crouton = Crouton.make(PublicacionNva.this, view);
                        crouton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                crouton.cancel();
                            }
                        });
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
                            nuevosServicios.add(nuevoServ);
                            //parseServicio.saveInBackground();
                            services.add(parseServicio);
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
            ImageLoader.getInstance().displayImage(file.getUrl(),
                    img_dpto, app.options3, app.animateFirstListener);

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
                precio.setText("Precio: $" + price);
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
                        subtitle.setText("Error el departamento no se pudo encontrar");
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
                            services.clear();

                            for(ParseObject service : parseObjects){
                                services.add(service);
                            }

                             adapterGrid = new ServiceCheckAdapter(ctx,R.layout.ser_che_layout, services, app);
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
                        subtitle.setText("Error los servicios no pudieron cargarse");
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

            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int quality                = 80;

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


            } else if(requestCode == PHOTO_1){
                photo1.setImageDrawable(d);
                photo1.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
        }

        if(item.getItemId() == R.id.publish){
            //Publicar
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

}
