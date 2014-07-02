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
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.parse.*;
import com.rebot.roomme.Adapters.DepartmentAdapter;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import com.todddavies.components.progressbar.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Strike on 6/21/14.
 */
public class DepartmentOffers extends SherlockFragmentActivity {
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.offers);

        app = (Roome) getApplication();
        list_offers = (ListView) findViewById(R.id.offers);
        loader = (ProgressWheel) findViewById(R.id.pw_spinner);
        loading_info = (RelativeLayout) findViewById(R.id.loading_info);

        my_offers = new ArrayList<ParseObject>();

        if(isOnline()){
            loader.spin();
            ParseUser user = ParseUser.getCurrentUser();
            getQuery(user);
        }
    }

    public void getQuery(ParseUser user){
        ParseQuery<ParseObject> own = ParseQuery.getQuery("Departamento");
        own.whereEqualTo("owner", user);
        own.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null){
                    if(parseObjects.size() != 0){
                        my_offers.clear();

                        for(ParseObject temp : parseObjects){
                            if((!temp.getBoolean("isDraft")) && (temp.getNumber("offers").intValue() > 0)){
                                my_offers.add(temp);
                            }
                        }

                        list_offers.setAdapter(new DepartmentAdapter(context,
                                R.layout.lookme_list_department_item, my_offers, app, true));

                        list_offers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                app.dptoSeleccionado = my_offers.get(position);
                                Intent intent = new Intent(DepartmentOffers.this, Ofertas.class);
                                DepartmentOffers.this.startActivity(intent);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId() == android.R.id.home){
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
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
