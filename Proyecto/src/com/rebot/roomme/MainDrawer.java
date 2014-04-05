package com.rebot.roomme;

import android.app.Activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.rebot.roomme.Adapters.MenuListAdapter;
import com.rebot.roomme.MeLook.MelookActivityHostFragment;
import com.rebot.roomme.MeProfile.MeProfileActivityHostFragment;

public class MainDrawer extends SherlockFragmentActivity {
    private Context context = this;
    private Roome app;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    String[] title;
    String[] subtitle;
    int[] icon;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    MenuListAdapter mMenuAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("");

        setContentView(R.layout.drawer_layout);


        app = (Roome) getApplication();

        // Get the Title
        mTitle = mDrawerTitle = "RooMe";

        // Generate title
        //title = new String[] {  getString(R.string.calendario),getString( R.string.servicios ),getString( R.string.datos ), getString(R.string.afiliados), getString(R.string.multimedia)};
        title = getResources().getStringArray(R.array.drawer_options);
        // Generate subtitle
        subtitle = new String[] { "", "", "", ""};

        // Generate icon
        icon = new int[] {R.drawable.ic_launcher,R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher};

        // Locate DrawerLayout in drawer_main.xml
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_element);

        // Locate ListView in drawer_main.xml
        mDrawerList = (ListView) findViewById(R.id.listview_drawer);

        // Set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Pass string arrays to MenuListAdapter
        mMenuAdapter = new MenuListAdapter(MainDrawer.this, title, subtitle, icon);

        // Set the MenuListAdapter to the ListView
        mDrawerList.setAdapter(mMenuAdapter);

        // Capture listview menu item click
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.drawer, R.string.drawer_open,R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                // TODO Auto-generated method stub
                getSupportActionBar().setTitle(mTitle);
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                // TODO Auto-generated method stub
                // Set the title on the action when drawer open
                getSupportActionBar().setTitle(mTitle);
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // ListView click listener in the navigation drawer
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        // Locate Position
        switch (position) {
            case 0:
                fragment= new MelookActivityHostFragment();
                break;
            case 1:

                //fragment= new ServicesActivityHostFragment();
                break;
            case 2:

                //fragment= new DataActivityHostFragment();
                break;
            case 3:
                fragment= new MeProfileActivityHostFragment();
                break;
        }


        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);

        setTitle(title[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        //getSupportActionBar().setTitle(mTitle);
        getSupportActionBar().setTitle(mTitle);
    }
}