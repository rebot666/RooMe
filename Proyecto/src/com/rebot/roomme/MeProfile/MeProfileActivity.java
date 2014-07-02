package com.rebot.roomme.MeProfile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Strike on 4/4/14.
 */

public class MeProfileActivity extends FragmentActivity {
    private Roome app;
    private ParseUser currentUser;
    private Crouton crouton;

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
    public ArrayList<String> interestList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profileme_layout);

        ParseFacebookUtils.initialize(getString(R.string.app_id));

        app = (Roome) getApplication();
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
                actualizarInfo();
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
                            View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                            title.setText(getResources().getString(R.string.whats));
                            subtitle.setVisibility(View.GONE);
                            crouton = Crouton.make(MeProfileActivity.this, view);
                            crouton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    crouton.cancel();
                                }
                            });
                            crouton.show();
                        }
                    });
                }else{
                    user.put("esRoomie", false);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            esRoomie.setEnabled(true);
                            View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                            title.setText(getResources().getString(R.string.no_roomie));
                            subtitle.setText(getResources().getString(R.string.comparator));
                            crouton = Crouton.make(MeProfileActivity.this, view);
                            crouton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    crouton.cancel();
                                }
                            });
                            crouton.show();
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

        this.btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeProfileActivity.this, MisPublicaciones.class);
                MeProfileActivity.this.startActivity(intent);
            }
        });

        this.btn_oferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeProfileActivity.this, DepartmentOffers.class);
                MeProfileActivity.this.startActivity(intent);
            }
        });

        this.btn_nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.dptoSeleccionado = null;
                Intent intent = new Intent(MeProfileActivity.this, PublicacionNva.class);
                MeProfileActivity.this.startActivity(intent);
            }
        });

        this.btn_borradores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeProfileActivity.this, MyDrafts.class);
                MeProfileActivity.this.startActivity(intent);
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

    public void actualizarInfo(){
        makeMeRequest();
    }

    private void makeMeRequest() {
        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            // Create a JSON object to hold the profile info
                            JSONObject userProfile = new JSONObject();
                            try {
                                // Populate the JSON object
                                userProfile.put("facebookId", user.getId());
                                userProfile.put("name", user.getName());

                                if (user.getLocation() != null) {
                                    userProfile.put("location", (String) user
                                            .getLocation().getProperty("name"));
                                }
                                if (user.getProperty("gender") != null) {
                                    userProfile.put("gender",
                                            (String) user.getProperty("gender"));
                                }
                                if (user.getBirthday() != null) {
                                    userProfile.put("birthday",
                                            user.getBirthday());
                                }
                                if (user.getProperty("relationship_status") != null) {
                                    userProfile
                                            .put("relationship_status",
                                                    (String) user
                                                            .getProperty("relationship_status"));
                                }

                                ParseUser current = ParseUser.getCurrentUser();
                                current.put("profile", userProfile);
                                current.saveInBackground();

                                requestBook();
                            } catch (JSONException e) {
                                Log.d("Error",
                                        "Error parsing returned user data.");
                            }

                        } else if (response.getError() != null) {
                            // handle error
                        }
                    }
                });
        request.executeAsync();
    }

    private void requestBook() {
        interestList = new ArrayList<String>();
        Request request = Request.newGraphPathRequest(ParseFacebookUtils.getSession(), "me/books",
                new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        if(response.getGraphObject() != null){
                            JSONObject userBooks = response.getGraphObject().getInnerJSONObject();

                            try {
                                JSONArray info = userBooks.getJSONArray("data");
                                for (int i = 0; i < info.length(); i++) {
                                    String book = (info.getJSONObject(i).getString("name"));
                                    interestList.add(book);
                                }

                                ParseUser current = ParseUser.getCurrentUser();
                                current.put("books", interestList);
                                current.saveInBackground();
                                requestMusic();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        request.executeAsync();
    }


    private void requestMusic() {
        interestList = new ArrayList<String>();
        Request request = Request.newGraphPathRequest(ParseFacebookUtils.getSession(), "me/music",
                new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        if(response.getGraphObject() != null){
                            JSONObject userBooks = response.getGraphObject().getInnerJSONObject();

                            try {
                                JSONArray info = userBooks.getJSONArray("data");
                                for (int i = 0; i < info.length(); i++) {
                                    String book = (info.getJSONObject(i).getString("name"));
                                    interestList.add(book);
                                }

                                ParseUser current = ParseUser.getCurrentUser();
                                current.put("music", interestList);
                                current.saveInBackground();
                                requestMovie();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        request.executeAsync();
    }

    private void requestMovie() {
        interestList = new ArrayList<String>();
        Request request = Request.newGraphPathRequest(ParseFacebookUtils.getSession(), "me/movies",
                new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        if(response.getGraphObject() != null){
                            JSONObject userBooks = response.getGraphObject().getInnerJSONObject();

                            try {
                                JSONArray info = userBooks.getJSONArray("data");
                                for (int i = 0; i < info.length(); i++) {
                                    String book = (info.getJSONObject(i).getString("name"));
                                    interestList.add(book);
                                }

                                ParseUser current = ParseUser.getCurrentUser();
                                current.put("movies", interestList);
                                current.saveInBackground();
                                //onBackPressed();
                                requestCover();


                                //NavUtils.navigateUpFromSameTask(MeProfileLogin.this);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        request.executeAsync();
    }

    private void requestCover() {
        Request request = Request.newGraphPathRequest(ParseFacebookUtils.getSession(), "me?fields=cover",
                new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        if(response.getGraphObject() != null){
                            JSONObject userBooks = response.getGraphObject().getInnerJSONObject();

                            String  urlInfo = userBooks.optString("source", "");
                            ParseUser current = ParseUser.getCurrentUser();
                            current.put("urlFacebookCover", urlInfo);
                            current.saveInBackground();


                        }
                    }
                });
        request.executeAsync();
    }
}
