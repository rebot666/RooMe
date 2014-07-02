package com.rebot.roomme.MeGo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.*;
import com.rebot.roomme.Adapters.ServiceCheckAdapter;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import com.todddavies.components.progressbar.ProgressWheel;
import de.keyboardsurfer.android.widget.crouton.Crouton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Strike on 4/24/14.
 */
public class MeGoActivity extends FragmentActivity {
    private Roome app;
    private Context ctx = this;
    private Crouton crouton;
    private Button next;
    private ParseObject object;

    private LinearLayout linear_one;
    private EditText titulo, inmueble, precio, direccion;
    private ImageView img_dpto, photo1, photo2, photo3, photo4;
    private CheckBox ch_roomme;
    private RadioGroup radio_trans, radio_gender;

    private LinearLayout linear_two;
    private EditText descripcion, tamano, plantas;
    private EditText cuartos, banos, cocina;
    private EditText comedor, estacionamiento, adicional;
    private CheckBox amueblado;

    private GridView grid_services;
    private ArrayList<ParseObject> services;

    private static final int SELECT_PICTURE = 1;
    private static final int PHOTO_1 = 2;
    private static final int PHOTO_2 = 3;
    private static final int PHOTO_3 = 4;
    private static final int PHOTO_4 = 5;

    @Override
    public void onCreate(Bundle savedInsatance){
        super.onCreate(savedInsatance);
        setContentView(R.layout.publication_new);

        app = (Roome) getApplication();
        next = (Button) findViewById(R.id.btn_siguiente);

        //First Layout
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

        amueblado = (CheckBox) findViewById(R.id.checkBox);

        //Third Layout
        grid_services = (GridView) findViewById(R.id.services_grid);
        services = new ArrayList<ParseObject>();

        if(app.dptoSeleccionado != null){
            getQuery();
        }

        next.setVisibility(View.VISIBLE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linear_one.getVisibility() == View.VISIBLE){
                    if(validaDatos()){
                        linear_one.setVisibility(View.GONE);
                        linear_two.setVisibility(View.VISIBLE);
                        if(app.dptoSeleccionado != null){
                            cargaDatos();
                        }
                    } else {
                        View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                        title.setVisibility(View.GONE);
                        subtitle.setText("Favor de verificar que los datos sean correctos");
                        crouton = Crouton.make(MeGoActivity.this, view);
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
                        linear_two.setVisibility(View.GONE);
                        grid_services.setVisibility(View.VISIBLE);
                        next.setVisibility(View.GONE);
                        getQueryServicios();
                    } else {
                        View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                        title.setVisibility(View.GONE);
                        subtitle.setText("Favor de verificar que los datos sean correctos");
                        crouton = Crouton.make(MeGoActivity.this, view);
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
                precio.setText("Precio: $" + precio);
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
            direccion.setText(object.getString("address"));
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
                estacionamiento.setText("Estacionamiento:  " + park);
            }

            String add = object.getString("adicionales");
            if(add != null){
                adicional.setText(add);
            }
        }
    }


    public boolean validaDatos(){
        return true;
    }

    public void getQuery(){
        if(isOnline()){
            ParseQuery<ParseObject> dpto = ParseQuery.getQuery("Departamento");
            dpto.getInBackground(app.dptoSeleccionado.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        if (parseObject != null) {
                            object = parseObject;
                            cargaDatos();
                        }
                    } else {
                        View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                        title.setVisibility(View.GONE);
                        subtitle.setText("Error el departamento no se pudo encontrar");
                        crouton = Crouton.make(MeGoActivity.this, view);
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

                            grid_services.setAdapter(new ServiceCheckAdapter(ctx,
                                    R.layout.ser_che_layout, services, app));
                        }
                    } else {
                        View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                        title.setVisibility(View.GONE);
                        subtitle.setText("Error los servicios no pudieron cargarse");
                        crouton = Crouton.make(MeGoActivity.this, view);
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
}

