package com.rebot.roomme.DptoSingle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Strike on 6/6/14.
 */
public class ContactoActivity extends FragmentActivity {
    private Context ctx = this;
    private Roome app;
    private ParseObject dpto;

    private EditText txt_comments, txt_phone, txt_email, txt_phone_2;
    private CheckBox checkBox, checkBox_2;
    private TextView txt_alterno, name_owner;
    private Button btn_enviar, btn_reporte;
    private Crouton crouton;
    private LinearLayout linear_alterno;
    private ImageView img_owner;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.third_detail_dpto);

        app = (Roome) getApplication();
        dpto = app.dptoSeleccionado;

        txt_comments = (EditText) findViewById(R.id.txt_comment);
        txt_phone = (EditText) findViewById(R.id.txt_tel);
        txt_phone_2 = (EditText) findViewById(R.id.txt_tel2);
        txt_email = (EditText) findViewById(R.id.txt_email);
        checkBox = (CheckBox) findViewById(R.id.ch_whats);
        checkBox_2 = (CheckBox) findViewById(R.id.ch_whats2);
        btn_enviar = (Button) findViewById(R.id.btn_enviar);
        txt_alterno = (TextView) findViewById(R.id.text_alterno);
        linear_alterno = (LinearLayout) findViewById(R.id.linear_tel2);
        btn_reporte = (Button) findViewById(R.id.btn_reporte);
        img_owner = (ImageView) findViewById(R.id.img_user);
        name_owner = (TextView) findViewById(R.id.txt_user_name);

        ParseUser owner = dpto.getParseUser("owner");
        JSONObject profile = owner.getJSONObject("profile");
        String idUser = profile.optString("facebookId");;
        String name = profile.optString("name");

        if((!idUser.equalsIgnoreCase("")) || (idUser != null)){
            ImageLoader.getInstance().displayImage("http://graph.facebook.com/"+idUser+"/picture?type=large",
                    img_owner, app.options, app.animateFirstListener);
        }

        if((!name.equalsIgnoreCase("")) || (name != null)){
            name_owner.setText("!Mandale una oferta a " + name+ "!");
        }

        final ParseUser currentUser = ParseUser.getCurrentUser();

        if(app.user){
            btn_enviar.setVisibility(View.GONE);
        }else{
            btn_enviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentUser != null){
                        String comment = txt_comments.getText().toString();
                        String phone = txt_phone.getText().toString();
                        String email = txt_email.getText().toString();
                        Boolean whats = checkBox.isChecked();

                        if(email != null || email.equalsIgnoreCase("")){
                            ParseObject offer = new ParseObject("Ofertas");
                            offer.put("dpto", dpto.getObjectId());
                            offer.put("owner", dpto.get("owner"));
                            offer.put("who", currentUser.getObjectId());

                            if(comment == null){
                                comment = "";
                            }
                            offer.put("comments", comment);

                            if(phone == null){
                                phone = "";
                            }
                            offer.put("phone", phone);
                            offer.put("whatsap", whats);

                            if(linear_alterno.getVisibility() == View.VISIBLE){
                                offer.put("phone2", txt_phone_2.getText());
                                offer.put("whats2", checkBox_2.isChecked());
                            }

                            offer.saveInBackground();
                        } else {
                            View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                            title.setText("Favor de llenar los campos requeridos");
                            subtitle.setVisibility(View.GONE);
                            crouton = Crouton.make(ContactoActivity.this, view);
                            crouton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    crouton.cancel();
                                }
                            });
                            crouton.show();
                        }
                    } else {
                        View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                        title.setText(getResources().getString(R.string.user_enrolled));
                        subtitle.setVisibility(View.GONE);
                        crouton = Crouton.make(ContactoActivity.this, view);
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


        txt_alterno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linear_alterno.getVisibility() == View.GONE){
                    txt_alterno.setText(getResources().getString(R.string.hide_tel_alterno));
                    linear_alterno.setVisibility(View.VISIBLE);
                } else {
                    txt_alterno.setText(getResources().getString(R.string.tel_alterno));
                    linear_alterno.setVisibility(View.GONE);
                }
            }
        });

        btn_reporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser != null){
                    ParseUser owner = dpto.getParseUser("owner");
                    owner.put("reported", true);
                    owner.saveInBackground();

                    View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                    title.setText("Este usuario estará bloqueado");
                    subtitle.setVisibility(View.GONE);
                    crouton = Crouton.make(ContactoActivity.this, view);
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
                    crouton = Crouton.make(ContactoActivity.this, view);
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
