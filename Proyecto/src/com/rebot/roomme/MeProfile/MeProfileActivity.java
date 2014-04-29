package com.rebot.roomme.MeProfile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.facebook.Session;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.plus.model.people.Person;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.rebot.roomme.R;
import com.todddavies.components.progressbar.ProgressWheel;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Strike on 4/4/14.
 */

public class MeProfileActivity extends FragmentActivity {
    private ParseUser currentUser;

    private LinearLayout linear_registro;
    private Button btn_sign_facebook, btn_sign_facebook2;

    private LinearLayout linear_profile;
    private ProfilePictureView img_profile;
    private TextView txt_name_profile;

    private LinearLayout whole_wrap;
    private Button btn_publish;
    private Button btn_borradores;
    private Button btn_nuevo;
    private Button btn_oferta;
    private ToggleButton esRoomie;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profileme_layout);

        //Inicialización de componentes de la interfaz
        this.linear_profile = (LinearLayout) findViewById(R.id.linear_profile);
        this.img_profile = (ProfilePictureView) findViewById(R.id.img_profile);
        this.txt_name_profile = (TextView) findViewById(R.id.name_profie);

        this.linear_registro = (LinearLayout) findViewById(R.id.linear_register);
        this.btn_sign_facebook = (Button) findViewById(R.id.btn_facebook);
        this.btn_sign_facebook2 = (Button) findViewById(R.id.btn_facebook_2);

        this.whole_wrap = (LinearLayout) findViewById(R.id.whole_wrap);
        this.btn_publish = (Button) findViewById(R.id.btn_publicaciones);
        this.btn_borradores = (Button) findViewById(R.id.btn_borradores);
        this.btn_nuevo = (Button) findViewById(R.id.btn_nuevo);
        this.btn_oferta = (Button) findViewById(R.id.btn_oferta);

        this.esRoomie = (ToggleButton) findViewById(R.id.toggle_roomie);

        ParseUser user = ParseUser.getCurrentUser();

        if(user !=null){
            if(user.getBoolean("esRoomie")){
                this.esRoomie.setChecked(true);
            }else{
                this.esRoomie.setChecked(false);
            }
        }

        this.esRoomie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esRoomie.setEnabled(false);
                ParseUser user = ParseUser.getCurrentUser();
                if(esRoomie.isChecked()){
                    user.put("esRoomie", true);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            esRoomie.setEnabled(true);
                        }
                    });
                }else{
                    user.put("esRoomie", false);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            esRoomie.setEnabled(true);
                        }
                    });
                }
            }
        });
        //Acciones de los botones
        this.btn_sign_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn = new Intent(MeProfileActivity.this, MeProfileLogin.class);
                startActivity(signIn);
            }
        });
        this.btn_sign_facebook2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Session session = Session.getActiveSession();
                session.closeAndClearTokenInformation();
                onResume();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        //Verificar si existe un usuario
        this.currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            //Esconder los componentes que no son necesarios
            this.linear_registro.setVisibility(View.GONE);

            //Mostrar los componentes necesarios
            this.linear_profile.setVisibility(View.VISIBLE);

            //Habilitar los componentes necesarios
            this.btn_publish.setEnabled(true);
            this.btn_borradores.setEnabled(true);
            this.btn_nuevo.setEnabled(true);
            this.btn_oferta.setEnabled(true);
            //this.btn_sign_facebook2.setVisibility(View.VISIBLE);

            //Actualizar la UI con la información del usuario
            updateUI();
        } else {
            //Esconder los campos necesarios
            this.linear_profile.setVisibility(View.GONE);

            //Mostrar los campos necesarios
            this.linear_registro.setVisibility(View.VISIBLE);

            //Deshabilitar los componentes necesarios
            //this.whole_wrap.setEnabled(false);
            this.btn_publish.setEnabled(false);
            this.btn_borradores.setEnabled(false);
            this.btn_nuevo.setEnabled(false);
            this.btn_oferta.setEnabled(false);
            this.btn_sign_facebook2.setVisibility(View.GONE);
        }
    }

    //Actualización del perfil del usuario
    public void updateUI() {
        this.currentUser = ParseUser.getCurrentUser();
        if (this.currentUser.get("profile") != null) {
            JSONObject userProfile = this.currentUser.getJSONObject("profile");
            try {
                //Imagen del perfil del usuario
                if (userProfile.getString("facebookId") != null) {
                    String facebookId = userProfile.get("facebookId").toString();
                    this.img_profile.setProfileId(facebookId);
                } else {
                    // Show the default, blank user profile picture
                    this.img_profile.setProfileId(null);
                }

                //Nombre del usuario
                if (userProfile.getString("name") != null) {
                    txt_name_profile.setText(userProfile.getString("name"));
                } else {
                    txt_name_profile.setText("");
                }
            } catch (JSONException e) {
                Log.d("JSON", e.toString());
            }
        }
    }
}
