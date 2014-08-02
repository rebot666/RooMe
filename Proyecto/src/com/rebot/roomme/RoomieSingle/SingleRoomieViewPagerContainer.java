package com.rebot.roomme.RoomieSingle;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.rebot.roomme.Adapters.FragmentAdapter;
import com.rebot.roomme.Adapters.RoomieSingleAdapter;
import com.rebot.roomme.MeProfile.PublicacionNva;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * Created by Toshiba on 31/07/2014.
 */
public class SingleRoomieViewPagerContainer extends SherlockFragmentActivity {
    private Roome app;
    private ViewPager mPager;
    private RoomieSingleAdapter mAdapter;
    private TitlePageIndicator mIndicator;
    private Boolean user;
    private Context context = this;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.single_roomie);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        app = (Roome) getApplication();

        mAdapter = new RoomieSingleAdapter(getSupportFragmentManager(), app.selfPublication, context);

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (TitlePageIndicator) findViewById(R.id.titles);
        mIndicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.Triangle);
        mIndicator.setViewPager(mPager);

        if(app.pestanaSingleRoomie == 0){
            mPager.setCurrentItem(0);
        }else{
            mPager.setCurrentItem(1);
        }


        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null){
            if(app.dptoSeleccionado != null){
                if(app.dptoSeleccionado.getParseUser("owner").getObjectId().equalsIgnoreCase(currentUser.getObjectId())){
                    user = true;
                    app.user = true;
                } else {
                    user = false;
                    app.user = false;
                }
            }
        } else {
            user = false;
            app.user = false;
        }
        invalidateOptionsMenu();
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

