package com.rebot.roomme.MeProfile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.plus.model.people.Person;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import com.todddavies.components.progressbar.ProgressWheel;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import org.json.JSONObject;

/**
 * Created by Strike on 6/6/14.
 */
public class DetailOffert extends SherlockFragmentActivity {
    private Roome app;
    private Context context = this;
    private Crouton crouton;

    private RelativeLayout loading_info;
    private ProgressWheel loader;
    private LinearLayout no_connection, contact, contact2, layoutMensaje, layoutTel1, layoutTel2;

    private TextView txt_comments, who_offers, txt_email, txt_phone, txt_phone2;
    private ImageView img_w, img_user, img_w2;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.detail_offer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        app = (Roome) getApplication();

        loading_info = (RelativeLayout) findViewById(R.id.loading_info);
        loader = (ProgressWheel) findViewById(R.id.pw_spinner);
        no_connection = (LinearLayout) findViewById(R.id.layout_no_connection);
        contact = (LinearLayout) findViewById(R.id.linear_contact);
        contact2 = (LinearLayout) findViewById(R.id.linear_contact2);

        txt_comments = (TextView) findViewById(R.id.txt_comments);
        who_offers = (TextView) findViewById(R.id.txt_oferta);
        txt_email = (TextView) findViewById(R.id.txt_email);
        txt_phone = (TextView) findViewById(R.id.txt_phone);
        txt_phone2 = (TextView) findViewById(R.id.txt_phone2);
        img_w = (ImageView) findViewById(R.id.img_whats);
        img_w2 = (ImageView) findViewById(R.id.img_whats2);
        img_user = (ImageView) findViewById(R.id.img_user);
        layoutMensaje = (LinearLayout) findViewById(R.id.layout_mensaje);
        layoutTel1 = (LinearLayout) findViewById(R.id.tel_layout);
        layoutTel2 = (LinearLayout) findViewById(R.id.tel2_layout);

        conectaDatos();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed ;
    }

    public void conectaDatos(){
        if(isOnline()){
            no_connection.setVisibility(View.GONE);

            //Oferta
            ParseObject offer = app.ofertaSeleccionada;
            if(offer.getString("comments") == null || offer.getString("comments").equals("")){
                layoutMensaje.setVisibility(View.GONE);
            }else{
                txt_comments.setText(offer.getString("comments"));
            }


            String email = offer.getString("email");
            if(email != null){
                final String correo = email;
                txt_email.setVisibility(View.VISIBLE);

                SpannableString content = new SpannableString(email);
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                content.setSpan(new ForegroundColorSpan(Color.BLUE), 0,
                        content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                txt_email.setText(content);

                txt_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto",correo, null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, app.dptoSeleccionado.getString("title"));
                        startActivity(Intent.createChooser(emailIntent, "Send Email"));
                    }
                });
            } else {
                txt_email.setVisibility(View.GONE);
            }

            if(offer.getString("phone") == null || offer.getString("phone").equals("")){
                layoutTel1.setVisibility(View.GONE);
            }
            String phone = offer.getString("phone");
            if(phone != null){
                final String tel = phone;
                contact.setVisibility(View.VISIBLE);
                txt_phone.setVisibility(View.VISIBLE);

                SpannableString content = new SpannableString(phone);
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                content.setSpan(new ForegroundColorSpan(Color.BLUE), 0,
                        content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                txt_phone.setText(content);

                txt_phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + tel));
                        startActivity(intent);
                    }
                });

                if(offer.getBoolean("whatsap")){
                    img_w.setVisibility(View.VISIBLE);

                    img_w.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean installed  =   appInstalledOrNot("com.whatsapp");

                            if(installed){
                                Uri uri = Uri.parse("smsto:" + tel);
                                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                                i.putExtra("sms_body", "mnsj");
                                i.setPackage("com.whatsapp");
                                startActivity(i);
                            } else {
                                View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                                TextView title = (TextView) view.findViewById(R.id.title);
                                TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                                title.setText(getResources().getString(R.string.si_roomie));
                                subtitle.setVisibility(View.GONE);
                                crouton = Crouton.make(DetailOffert.this, view);
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

                } else {
                    img_w.setVisibility(View.GONE);
                }
            } else {
                contact.setVisibility(View.GONE);
            }

            if(offer.getString("phone2") == null || offer.getString("phone2").equals("")){
                layoutTel2.setVisibility(View.GONE);
            }
            String phone2 = offer.getString("phone2");
            if(phone2 != null){
                final String tel = phone;
                contact2.setVisibility(View.VISIBLE);
                txt_phone2.setVisibility(View.VISIBLE);

                SpannableString content = new SpannableString(phone2);
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                content.setSpan(new ForegroundColorSpan(Color.BLUE), 0,
                        content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                txt_phone2.setText(content);

                txt_phone2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + tel));
                        startActivity(intent);
                    }
                });

                if(offer.getBoolean("whats2")){
                    img_w2.setVisibility(View.VISIBLE);

                    img_w2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean installed  =   appInstalledOrNot("com.whatsapp");

                            if(installed){
                                Uri uri = Uri.parse("smsto:" + tel);
                                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                                i.putExtra("sms_body", "mnsj");
                                i.setPackage("com.whatsapp");
                                startActivity(i);
                            } else {
                                View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                                TextView title = (TextView) view.findViewById(R.id.title);
                                TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                                title.setText(getResources().getString(R.string.si_roomie));
                                subtitle.setVisibility(View.GONE);
                                crouton = Crouton.make(DetailOffert.this, view);
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

                } else {
                    img_w2.setVisibility(View.GONE);
                }
            } else {
                contact2.setVisibility(View.GONE);
            }

            ParseUser who = offer.getParseUser("who");
            JSONObject profile = who.getJSONObject("profile");
            String idUser = profile.optString("facebookId");

            ImageLoader.getInstance().displayImage("http://graph.facebook.com/"+idUser+"/picture?type=large",
                    img_user, app.options, app.animateFirstListener);

            String name = profile.optString("name");
            who_offers.setText(getString(R.string.you_got_an_offer) + " " +name);
        } else {
            no_connection.setVisibility(View.VISIBLE);
            no_connection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    conectaDatos();
                }
            });
        }
    }
}
