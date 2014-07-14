package com.rebot.roomme.MeLook;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.*;
import com.markupartist.android.widget.PullToRefreshListView;
import com.parse.*;
import com.rebot.roomme.Adapters.DepartmentAdapter;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import com.rebot.roomme.SingleDepartment;
import com.todddavies.components.progressbar.ProgressWheel;
import de.keyboardsurfer.android.widget.crouton.Crouton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Strike on 6/15/14.
 */
public class MelookDpto extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener{

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationClient mLocationClient;

    private ArrayList<ParseObject> dptos;
    private Context context = this;
    private Crouton crouton;
    private Roome app;

    private LinearLayout linear_map;
    private Button btn_map;
    private GoogleMap googleMap;
    private UiSettings mUiSettings;
    private LinearLayout linear_options;

    private LinearLayout linear_list;
    private RelativeLayout loading_info;
    private LinearLayout no_info;
    private ProgressWheel loader;

    //private PullToRefreshListView people;
    private ListView people;
    private LinearLayout no_connection;

    private SwipeRefreshLayout swipeLayout;
    private double latitud = 23.081897;
    private double longitud =  -102.427222;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.dpto_layout);

        app = (Roome) getApplication();

        mLocationClient = new LocationClient(this, this, this);

        btn_map = (Button) findViewById(R.id.showMap);
        linear_map = (LinearLayout) findViewById(R.id.linear_map);
        linear_list = (LinearLayout) findViewById(R.id.linear_list);
        linear_options = (LinearLayout) findViewById(R.id.tab_options);
        loading_info = (RelativeLayout) findViewById(R.id.loading_info);
        loader = (ProgressWheel) findViewById(R.id.pw_spinner);
        app.dpto = (ListView) findViewById(R.id.listView);
        app.noInfo = (LinearLayout) findViewById(R.id.layout_no_info);
        no_connection = (LinearLayout) findViewById(R.id.layout_no_connection);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setColorScheme(R.color.rojo,
                R.color.azul,
                R.color.amarillo,
                R.color.verde);

        dptos = new ArrayList<ParseObject>();

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn_title = btn_map.getText().toString();
                if (btn_title.equalsIgnoreCase("Mapa")) {
                    linear_list.setVisibility(View.GONE);
                    linear_map.setVisibility(View.VISIBLE);
                    try {
                        // Loading map
                        btn_map.setText("Lista");
                        initilizeMap();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    btn_map.setText("Mapa");
                    linear_list.setVisibility(View.VISIBLE);
                    linear_map.setVisibility(View.GONE);
                    queryLoadData_dptos();
                }
            }
        });

        if(isOnline()){
            loader.spin();
            queryLoadData_dptos();
        }else{
            loading_info.setVisibility(View.GONE);
            no_connection.setVisibility(View.VISIBLE);
        }

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dptos.clear();
                app.noInfo.setVisibility(View.GONE);
                queryLoadData_dptos();
            }
        });

        app.noInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dptos.clear();
                app.noInfo.setVisibility(View.GONE);
                queryLoadData_dptos();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
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

    public void setUpMap(){
        /*googleMap.setMyLocationEnabled(true);

        mUiSettings = googleMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);

        for(ParseObject dpto : dptos){
            ParseGeoPoint geoPoint = dpto.getParseGeoPoint("location");
            String title = dpto.getString("address");
            if(geoPoint != null){
                LatLng lng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

                googleMap.addMarker(
                        new MarkerOptions()
                                .position(lng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_house))
                                .title(title));
            }
        }*/
    }

    public void queryLoadData_dptos(){
        if(isOnline()){
            ParseUser currentUser = ParseUser.getCurrentUser();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Departamento");

            if(currentUser != null){
                query.whereNotEqualTo("owner", currentUser);
            }
            query.include("owner");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if(e == null){
                        if(list.size() > 0){
                            dptos.clear();
                            for(ParseObject temp : list){
                                if(!temp.getBoolean("isSell") && !temp.getBoolean("isDraft")){
                                    dptos.add(temp);
                                }
                            }

                            app.dpto.setAdapter(new DepartmentAdapter(context,
                                    R.layout.lookme_list_department_item, dptos, app, false));
                            loading_info.setVisibility(View.GONE);
                            loader.stopSpinning();

                            app.dpto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    app.dptoSeleccionado = dptos.get(position);
                                    Intent intent = new Intent(MelookDpto.this, SingleDepartment.class);
                                    MelookDpto.this.startActivity(intent);
                                }
                            });
                            //people.onRefreshComplete();
                            swipeLayout.setRefreshing(false);
                            setUpMap();
                        }
                    } else {
                        Log.d("", "");
                        loading_info.setVisibility(View.GONE);
                        no_info.setVisibility(View.VISIBLE);
                        loader.stopSpinning();
                        //people.onRefreshComplete();
                        swipeLayout.setRefreshing(false);
                    }
                }
            });
        } else {
            Log.d("", "");
            loading_info.setVisibility(View.GONE);
            app.noInfo.setVisibility(View.VISIBLE);
            loader.stopSpinning();
            //people.onRefreshComplete();
            swipeLayout.setRefreshing(false);
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
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mCurrentLocation;
        //TODO: Implementar validacion cuando no hay chip

        mCurrentLocation = mLocationClient.getLastLocation();

        if(mCurrentLocation != null){
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()))
                    .zoom(15.5f)
                    //.bearing(330)
                    .tilt(50)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }else{
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitud,
                            longitud))
                    .zoom(12.5f)
                    .bearing(330)
                    .tilt(50)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }




    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
        }
    }
}
