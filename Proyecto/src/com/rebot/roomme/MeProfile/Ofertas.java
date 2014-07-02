package com.rebot.roomme.MeProfile;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.parse.*;
import com.rebot.roomme.Adapters.OffersAdapter;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import com.todddavies.components.progressbar.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Strike on 5/28/14.
 */
public class Ofertas extends SherlockActivity{
    private Context context = this;
    private Roome app;
    private ListView list_offers;
    private ArrayList<ParseObject> my_offers;

    private LinearLayout no_connection;
    private RelativeLayout loading_info;
    private ProgressWheel loader;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.offers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        app = (Roome) getApplication();

        list_offers = (ListView) findViewById(R.id.offers);
        loading_info = (RelativeLayout) findViewById(R.id.loading_info);
        no_connection = (LinearLayout) findViewById(R.id.no_connection);
        loader = (ProgressWheel) findViewById(R.id.pw_spinner);

        my_offers = new ArrayList<ParseObject>();

        if(isOnline()){
            no_connection.setVisibility(View.GONE);
            loader.spin();

            ParseUser currentUser = ParseUser.getCurrentUser();
            if(currentUser != null){
                query_offers(currentUser);
            }
        } else {
            no_connection.setVisibility(View.VISIBLE);
        }
    }

    public void query_offers(ParseUser user){
        ParseQuery<ParseObject> own = ParseQuery.getQuery("Ofertas");
        own.whereEqualTo("owner", user);
        own.whereEqualTo("dpto", app.dptoSeleccionado);
        own.orderByAscending("createdAt");
        own.include("who");
        own.include("dpto");
        own.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null){
                    if(parseObjects.size() != 0){
                        my_offers.clear();

                        for(ParseObject temp : parseObjects){
                            my_offers.add(temp);
                        }

                        list_offers.setAdapter(new OffersAdapter(context,
                                R.layout.offer_list_item, my_offers, app));

                        list_offers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                app.ofertaSeleccionada = my_offers.get(position);
                                Intent intent = new Intent(Ofertas.this, DetailOffert.class);
                                Ofertas.this.startActivity(intent);
                            }
                        });

                        loading_info.setVisibility(View.GONE);
                        loader.stopSpinning();
                    }
                } else {
                    Log.e("hola", "");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
