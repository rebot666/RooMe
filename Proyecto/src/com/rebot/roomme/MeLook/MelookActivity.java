package com.rebot.roomme.MeLook;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.markupartist.android.widget.PullToRefreshListView;
import com.parse.*;
import com.rebot.roomme.*;
import com.rebot.roomme.Adapters.LookMeAdpater;
import com.rebot.roomme.Models.Users;
import com.rebot.roomme.R;
import com.todddavies.components.progressbar.ProgressWheel;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Toshiba on 8/03/14.
 */
public class MelookActivity extends FragmentActivity {
    private ArrayList<Users> profiles;
    private Context context = this;
    private Crouton crouton;
    private Roome app;

    private Button btn_map;
    private Button btn_list;

    private LinearLayout linear_map;
    private GoogleMap googleMap;


    private RelativeLayout loading_info;
    private LinearLayout no_info;
    private ProgressWheel loader;

    private LinearLayout linear_list;
    private PullToRefreshListView people;

    private LinearLayout no_connection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lookme_layout);
        app = (Roome) getApplication();

        btn_map = (Button) findViewById(R.id.showMap);
        btn_list = (Button) findViewById(R.id.showList);

        linear_map = (LinearLayout) findViewById(R.id.linear_map);

        loading_info = (RelativeLayout) findViewById(R.id.loading_info);
        loader = (ProgressWheel) findViewById(R.id.pw_spinner);

        linear_list = (LinearLayout) findViewById(R.id.linear_list);
        people = (PullToRefreshListView) findViewById(R.id.listView);

        no_info = (LinearLayout) findViewById(R.id.layout_no_info);

        no_connection = (LinearLayout) findViewById(R.id.layout_no_connection);

        profiles = new ArrayList<Users>();

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_list.setVisibility(View.GONE);
                linear_map.setVisibility(View.VISIBLE);
                try {
                    // Loading map
                    initilizeMap();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        loader.spin();

        if(isOnline()){
            queryLoadData();
        }else{
            loading_info.setVisibility(View.GONE);
            no_connection.setVisibility(View.VISIBLE);
        }


        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_map.setVisibility(View.GONE);
                linear_list.setVisibility(View.VISIBLE);
            }
        });

        people.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                profiles.clear();
                queryLoadData();

            }
        });

        if (ParseUser.getCurrentUser() == null){
            //crouton = Crouton.makeText(MelookActivity.this, "Inicia sesión para ver compatibilidad", Style.ALERT);
            //crouton.show();
            View view = getLayoutInflater().inflate(R.layout.crouton_custom_view, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
            title.setVisibility(View.GONE);
            subtitle.setText(getResources().getString(R.string.no_session));
            crouton = Crouton.make(MelookActivity.this, view);
            crouton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    crouton.cancel();
                }
            });
            crouton.show();
        }
    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void queryLoadData(){
        ParseQuery<ParseUser>  query = ParseUser.getQuery();
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if(e == null){
                    if(list.size() > 0){
                        profiles.clear();
                        ParseUser me = ParseUser.getCurrentUser();
                        if(me != null){
                            for(ParseUser temp : list){
                                if(temp.getBoolean("esRoomie")){
                                    if(!me.getObjectId().equals(temp.getObjectId())){
                                        double porcentaje = CBR.calculaCBR(me, temp);
                                        Users tempUser = new Users(temp, porcentaje);
                                        profiles.add(tempUser);
                                    }
                                }
                            }

                            Collections.sort(profiles);
                        }else{
                            for(ParseUser temp : list){
                                if(temp.getBoolean("esRoomie")){
                                    double porcentaje = -1.0;
                                    Users tempUser = new Users(temp, porcentaje);
                                    profiles.add(tempUser);
                                }

                            }
                        }


                        people.setAdapter(new LookMeAdpater(context,
                                R.layout.lookme_list_item, profiles, app));
                        loading_info.setVisibility(View.GONE);
                        loader.stopSpinning();
                        people.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                app.roomieSeleccionado = profiles.get(position - 1);
                                Intent intent = new Intent(MelookActivity.this, MeLookRoomie.class);
                                MelookActivity.this.startActivity(intent);
                            }
                        });
                    }
                } else {
                    Log.d("", "");
                    loading_info.setVisibility(View.GONE);
                    no_info.setVisibility(View.VISIBLE);
                    loader.stopSpinning();
                }

                people.onRefreshComplete();
            }
        });

    }
}
