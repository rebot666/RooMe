package com.rebot.roomme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.parse.ParseUser;
import com.rebot.roomme.Adapters.FragmentAdapter;
import com.rebot.roomme.MeProfile.PublicacionNva;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * Created by Strike on 5/30/14.
 */
public class SingleDepartment extends SherlockFragmentActivity {
    private Roome app;
    private ViewPager mPager;
    private FragmentAdapter mAdapter;
    private TitlePageIndicator mIndicator;
    private Boolean user;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.single_dpto);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        app = (Roome) getApplication();

        mAdapter = new FragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (TitlePageIndicator) findViewById(R.id.titles);
        mIndicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.Triangle);
        mIndicator.setViewPager(mPager);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null){
            if(app.dptoSeleccionado.getParseUser("owner").getObjectId().equalsIgnoreCase(currentUser.getObjectId())){
                user = true;
            } else {
                user = false;
            }
        } else {
            user = false;
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(!user){
            MenuItem destacado = menu.getItem(0);
            if(app.dptoSeleccionado.getBoolean("destacado")){
                destacado.setIcon(android.R.drawable.btn_star_big_on);
            } else {
                destacado.setIcon(android.R.drawable.btn_star_big_off);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
        if(user){
            inflater.inflate(R.menu.share_edit, menu);
        } else {
            inflater.inflate(R.menu.share_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        if(item.getItemId() == R.id.share){
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Here is the share content body";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return true;
        }

        if(item.getItemId() == R.id.edit){
            Intent intent = new Intent(SingleDepartment.this, PublicacionNva.class);
            SingleDepartment.this.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}