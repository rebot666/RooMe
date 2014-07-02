package com.rebot.roomme.DptoSingle;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.parse.*;
import com.rebot.roomme.Adapters.ServicesAdapter;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import com.todddavies.components.progressbar.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Strike on 6/22/14.
 */
public class ServicesActivity extends FragmentActivity {
    private Roome app;
    private Context ctx = this;
    private GridView services_grid;
    private ArrayList<ParseObject> services;

    private LinearLayout no_connection;
    private RelativeLayout loading;
    private ProgressWheel pw_loader;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.services_layout);

        app = (Roome) getApplication();
        loading = (RelativeLayout) findViewById(R.id.loading_info);
        pw_loader = (ProgressWheel) findViewById(R.id.pw_spinner);
        services_grid = (GridView) findViewById(R.id.services_grid);
        no_connection = (LinearLayout) findViewById(R.id.no_connection);

        services = new ArrayList<ParseObject>();

        if(isOnline()){
            pw_loader.spin();
            getQuery();
        } else {
            loading.setVisibility(View.GONE);
            no_connection.setVisibility(View.VISIBLE);
        }
    }

    public void getQuery(){
        ParseQuery<ParseObject> services_query = ParseQuery.getQuery("Servicios_Dpto");
        services_query.whereEqualTo("dpto", app.dptoSeleccionado);
        services_query.include("service");

        services_query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null){
                    if(parseObjects.size() > 0){
                        services.clear();

                        for(ParseObject service : parseObjects){
                            services.add(service);
                        }

                        loading.setVisibility(View.GONE);
                        pw_loader.stopSpinning();

                        services_grid.setAdapter(new ServicesAdapter(ctx,
                                R.layout.service_adapter, services, app));
                    } else {
                        //Este departamento no cuenta con servicios extra
                    }
                } else {
                    //No se pudieron cargar los datos
                }
            }
        });
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