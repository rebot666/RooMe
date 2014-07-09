package com.rebot.roomme.DptoSingle;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.*;
import com.parse.*;
import com.rebot.roomme.Adapters.CommentAdapter;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Toshiba on 08/07/2014.
 */
public class ComentariosActivity extends FragmentActivity {
    private ListView comments;
    private Roome app;
    private Context context = this;
    private ArrayList<ParseObject> data;
    private CommentAdapter adapter;
    private LinearLayout  noInfoLayout;
    private LinearLayout loadingLayout, commentLayout;
    private EditText nuevoComentario;
    private Button enviarComentario;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.comentario_detail_dpto);

        app = (Roome) getApplication();

        comments = (ListView) findViewById(R.id.list_comments);
        loadingLayout = (LinearLayout) findViewById(R.id.cargando_layout);
        noInfoLayout = (LinearLayout) findViewById(R.id.layout_no_info);
        nuevoComentario = (EditText) findViewById(R.id.edit_comentario);
        enviarComentario = (Button) findViewById(R.id.btn_comment);
        commentLayout = (LinearLayout) findViewById(R.id.layout_comment);



        data = new ArrayList<ParseObject>();

        cargaTabla();

        if(app.user){
            commentLayout.setVisibility(View.GONE);
        }else{
            enviarComentario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isOnline()){
                        if(!nuevoComentario.getText().toString().equals("") && ParseUser.getCurrentUser() != null){

                            ParseObject comentario = new ParseObject("Comentarios");
                            ParseObject temp = ParseObject.createWithoutData("Departamento",app.dptoSeleccionado.getObjectId());

                            String nombre = "";
                            try {
                                nombre = ParseUser.getCurrentUser().getJSONObject("profile").getString("name");
                            } catch (JSONException e) {
                                nombre = "";
                            }
                            comentario.put("nombre", nombre);
                            comentario.put("rating", 5);
                            comentario.put("comentario", nuevoComentario.getText().toString());
                            comentario.put("dpto", temp);
                            comentario.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        nuevoComentario.setText("");
                                        cargaTabla();
                                    }else{

                                    }
                                }
                            });
                        }else{

                        }
                    }else{

                    }
                }
            });
        }


    }

    public void cargaTabla(){
        loadingLayout.setVisibility(View.VISIBLE);
        if(isOnline()){
            ParseQuery query = new ParseQuery("Comentarios");
            query.whereEqualTo("dpto", app.dptoSeleccionado);
            query.include("who");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if(e == null){
                        if(list.size() > 0) {
                            data = (ArrayList<ParseObject>) list;

                            adapter = new CommentAdapter(context, R.layout.comentario_detail_dpto_item, data, app);
                            comments.setAdapter(adapter);
                            loadingLayout.setVisibility(View.GONE);
                            noInfoLayout.setVisibility(View.GONE);
                            comments.setVisibility(View.VISIBLE);
                        }else{
                            loadingLayout.setVisibility(View.GONE);
                            noInfoLayout.setVisibility(View.VISIBLE);
                            comments.setVisibility(View.GONE);
                        }
                    }else{
                        loadingLayout.setVisibility(View.GONE);
                        noInfoLayout.setVisibility(View.VISIBLE);
                        comments.setVisibility(View.GONE);
                    }
                }
            });
        }else{
            loadingLayout.setVisibility(View.GONE);
            noInfoLayout.setVisibility(View.VISIBLE);
            comments.setVisibility(View.GONE);
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
