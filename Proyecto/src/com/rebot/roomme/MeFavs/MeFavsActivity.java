package com.rebot.roomme.MeFavs;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.parse.*;
import com.rebot.roomme.Adapters.DepartmentAdapter;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import com.rebot.roomme.SingleDepartment;
import com.todddavies.components.progressbar.ProgressWheel;
import de.keyboardsurfer.android.widget.crouton.Crouton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Strike on 4/24/14.
 */
public class MeFavsActivity extends FragmentActivity {
    private Context context = this;
    private Crouton crouton;
    private Roome app;
    private ListView list_favs;
    private ArrayList my_favs;
    private ArrayList<ParseObject> show_dptos;
    private LinearLayout no_connection, no_info;
    private LinearLayout favorites;
    private RelativeLayout loading_info;
    private ProgressWheel loader;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.favsme_layout);

        loader = (ProgressWheel) findViewById(R.id.pw_spinner);

        app = (Roome) getApplication();
        no_connection = (LinearLayout) findViewById(R.id.layout_no_connection);
        favorites = (LinearLayout) findViewById(R.id.layout_favs);
        list_favs = (ListView) findViewById(R.id.list_favorites);

        my_favs = new ArrayList();
        show_dptos = new ArrayList<ParseObject>();
        loading_info = (RelativeLayout) findViewById(R.id.loading_info);
        no_info = (LinearLayout) findViewById(R.id.layout_no_info);
        no_connection = (LinearLayout) findViewById(R.id.layout_no_connection);

        conectarDatos();

        no_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conectarDatos();
            }
        });
    }

    public void getQuery(ParseUser user){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.include("owner");
        query.getInBackground(user.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if(parseUser != null){
                    my_favs = (ArrayList) parseUser.get("favorites");

                    if(my_favs == null){
                        my_favs = new ArrayList();
                    }

                    ParseQuery<ParseObject> favs = ParseQuery.getQuery("Departamento");
                    favs.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> parseObjects, ParseException e) {
                            if(e == null){
                                if(parseObjects.size() != 0){
                                    show_dptos.clear();

                                    for(int i = 0; i < parseObjects.size(); i++){
                                        if(my_favs.contains(parseObjects.get(i).getObjectId())){
                                            show_dptos.add(parseObjects.get(i));
                                        }
                                    }

                                    list_favs.setAdapter(new DepartmentAdapter(context,
                                            R.layout.lookme_list_department_item, show_dptos, app, false));

                                    list_favs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            app.dptoSeleccionado = show_dptos.get(position);
                                            Intent intent = new Intent(MeFavsActivity.this, SingleDepartment.class);
                                            MeFavsActivity.this.startActivity(intent);
                                        }
                                    });

                                    loading_info.setVisibility(View.GONE);
                                    loader.stopSpinning();
                                }
                                loading_info.setVisibility(View.GONE);
                                loader.stopSpinning();
                            } else {
                                Log.e("FAVORITOS", e.toString());
                            }
                        }
                    });
                }
            }
        });
    }

    public void loadFavorites(ParseUser currenUser){
        List<String> listObjectId = currenUser.getList("favorites");
        if(listObjectId != null){
            ParseQuery query = new ParseQuery("Departamento");
            query.include("owner");
            query.whereContainedIn("objectId", listObjectId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if(e == null){
                        show_dptos = (ArrayList<ParseObject>) list;
                        if(list.size() > 0){
                            list_favs.setAdapter(new DepartmentAdapter(context,R.layout.lookme_list_department_item, show_dptos, app, false));
                            list_favs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    app.dptoSeleccionado = show_dptos.get(position);
                                    Intent intent = new Intent(MeFavsActivity.this, SingleDepartment.class);
                                    MeFavsActivity.this.startActivity(intent);
                                }
                            });

                            loading_info.setVisibility(View.GONE);
                            no_info.setVisibility(View.GONE);
                            loader.stopSpinning();
                        }else{
                            loading_info.setVisibility(View.GONE);
                            loader.stopSpinning();
                            no_info.setVisibility(View.VISIBLE);
                        }

                    }else{
                        loading_info.setVisibility(View.GONE);
                        loader.stopSpinning();
                        no_info.setVisibility(View.VISIBLE);
                    }
                }
            });
        }else{
            loading_info.setVisibility(View.GONE);
            loader.stopSpinning();
            no_info.setVisibility(View.VISIBLE);
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

    public void conectarDatos(){
        no_connection.setVisibility(View.GONE);
        favorites.setVisibility(View.VISIBLE);
        if(!isOnline()){
            no_connection.setVisibility(View.VISIBLE);
            favorites.setVisibility(View.GONE);
        } else {
            no_connection.setVisibility(View.GONE);
            favorites.setVisibility(View.VISIBLE);
            ParseUser currentUser = ParseUser.getCurrentUser();
            if(currentUser != null){
                loader.spin();
                //getQuery(currentUser);
                loadFavorites(currentUser);
            } else {
                loading_info.setVisibility(View.GONE);
                loader.stopSpinning();
                no_info.setVisibility(View.VISIBLE);
                View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
                title.setText(getResources().getString(R.string.user_enrolled));
                subtitle.setVisibility(View.GONE);
                crouton = Crouton.make(MeFavsActivity.this, view);
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
}
