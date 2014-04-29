package com.rebot.roomme.MeLook;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.parse.*;
import com.rebot.roomme.Adapters.LookMeAdpater;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import com.todddavies.components.progressbar.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toshiba on 8/03/14.
 */
public class MelookActivity extends FragmentActivity {
    private ArrayList<ParseObject> profiles;
    private Context context = this;
    private Roome app;

    private Button btn_map;
    private Button btn_list;

    private LinearLayout linear_map;
    private GoogleMap googleMap;


    private RelativeLayout loading_info;
    private LinearLayout no_info;
    private ProgressWheel loader;

    private LinearLayout linear_list;
    private ListView people;

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
        people = (ListView) findViewById(R.id.listView);

        no_info = (LinearLayout) findViewById(R.id.layout_no_info);

        profiles = new ArrayList<ParseObject>();

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

        ParseQuery<ParseUser>  query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if(e == null){
                    if(list.size() > 0){

                        ParseUser me = ParseUser.getCurrentUser();
                        if(me != null){
                            for(ParseUser temp : list){
                                if(temp.getBoolean("esRoomie")){
                                    if(!me.getObjectId().equals(temp.getObjectId())){
                                        profiles.add(temp);
                                    }

                                }
                            }
                        }else{
                            for(ParseUser temp : list){
                                if(temp.getBoolean("esRoomie")){
                                    profiles.add(temp);
                                }

                            }
                        }


                        people.setAdapter(new LookMeAdpater(context,
                                R.layout.lookme_list_item, profiles, app));
                        loading_info.setVisibility(View.GONE);
                        loader.stopSpinning();
                    }
                } else {
                    Log.d("", "");
                    loading_info.setVisibility(View.GONE);
                    no_info.setVisibility(View.VISIBLE);
                    loader.stopSpinning();
                }
            }
        });

        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_map.setVisibility(View.GONE);
                linear_list.setVisibility(View.VISIBLE);
            }
        });
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
}
