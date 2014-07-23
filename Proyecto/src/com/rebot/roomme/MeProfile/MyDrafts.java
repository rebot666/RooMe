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
import com.rebot.roomme.Adapters.DepartmentAdapter;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import com.rebot.roomme.SingleDepartment;
import com.todddavies.components.progressbar.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Strike on 6/6/14.
 */
public class MyDrafts extends SherlockActivity {
    private Roome app;
    private ArrayList<ParseObject> my_drafts;
    private Context context = this;
    private ListView drafts;

    private LinearLayout no_connection;
    private RelativeLayout loading_info;
    private ProgressWheel loader;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.my_drafts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        app = (Roome) getApplication();

        my_drafts = new ArrayList<ParseObject>();

        drafts = (ListView) findViewById(R.id.drafts);
        loading_info = (RelativeLayout) findViewById(R.id.loading_info);
        loader = (ProgressWheel) findViewById(R.id.pw_spinner);

    }

    public void getQuery(ParseUser user){
        ParseQuery<ParseObject> own = ParseQuery.getQuery("Departamento");
        own.whereEqualTo("owner", user);
        own.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null){
                    if(parseObjects.size() != 0){
                        my_drafts.clear();

                        for(ParseObject temp : parseObjects){
                            if(temp.getBoolean("isDraft") && !temp.getBoolean("isSell")){
                                my_drafts.add(temp);
                            }
                        }

                        drafts.setAdapter(new DepartmentAdapter(context,
                                R.layout.lookme_list_department_item, my_drafts, app, false));

                        drafts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                app.dptoSeleccionado = my_drafts.get(position);
                                //Intent to formulario
                                Intent intent = new Intent(MyDrafts.this, PublicacionNva.class);
                                MyDrafts.this.startActivity(intent);
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

    @Override
    public void onResume(){
        super.onResume();
        if(isOnline()){
            loader.spin();
            ParseUser user = ParseUser.getCurrentUser();
            getQuery(user);
        }
    }
}
