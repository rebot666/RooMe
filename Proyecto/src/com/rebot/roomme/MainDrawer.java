package com.rebot.roomme;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.rebot.roomme.Adapters.MenuListAdapter;
import com.rebot.roomme.MeFavs.MeFavsActivityHostFragment;
import com.rebot.roomme.MeGo.MeGoActivityHostFragment;
import com.rebot.roomme.MeLook.MelookActivityHostFragment;
import com.rebot.roomme.MeLook.MelookDptoHostFrament;
import com.rebot.roomme.MeProfile.MeProfileActivityHostFragment;
import com.rebot.roomme.SearchFragment.SearchDpto;
import com.rebot.roomme.SearchFragment.SearchRoomme;

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
    private boolean menushow;
    private boolean menunew;

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
        icon = new int[] {R.drawable.icon_search,R.drawable.icon_star, R.drawable.icon_user, R.drawable.icon_nuevo};

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

        // Capture listview menu_item item click
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.icon_drawer, R.string.drawer_open,R.string.drawer_close) {

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
            menushow = true;
            menunew = false;
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

        if (item.getItemId() == R.id.change){
            if(mDrawerList.getCheckedItemPosition() == 0){
                selectItem(4);
            } else {
                selectItem(0);
            }
        }

        if (item.getItemId() == R.id.search){
            FragmentManager fm = this.getSupportFragmentManager();
            DialogFragment dialog;

            if(mDrawerList.getCheckedItemPosition() == 4){
                dialog = new SearchDpto(MainDrawer.this, this.app); // creating new object
            } else {
                dialog = new SearchRoomme(MainDrawer.this, this.app); // creating new object
            }

            dialog.show(fm, "dialog");
            mDrawerLayout.closeDrawer(mDrawerList);
        }

        if(item.getItemId() == R.id.save){
            //Guardar
            Intent intent = new Intent("my-event");
            // add data
            intent.putExtra("publish", false);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        }

        if(item.getItemId() == R.id.publish){
            //Publicar
            Intent intent = new Intent("my-event");
            // add data
            intent.putExtra("publish", true);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
                menushow = true;
                menunew = false;
                break;
            case 1:
                fragment = new MeFavsActivityHostFragment();
                menushow = false;
                menunew = false;
                break;
            case 2:
                fragment = new MeProfileActivityHostFragment();
                menushow = false;
                menunew = false;
                break;
            case 3:
                app.dptoSeleccionado = null;
                fragment = new MeGoActivityHostFragment();
                menushow = false;
                menunew = true;
                break;
            case 4:
                fragment = new MelookDptoHostFrament();
                menushow = true;
                menunew = false;
                break;
        }

        supportInvalidateOptionsMenu();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);

        if(position != 4){
            setTitle(title[position]);
        }

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
    public boolean onPrepareOptionsMenu(Menu menu){
        if(menushow){
            MenuItem item = menu.getItem(0);
            if(mDrawerList.getCheckedItemPosition() == 4){
                item.setIcon(R.drawable.icon_social);
            } else {
                item.setIcon(R.drawable.icon_color);
            }

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(menushow){
            MenuInflater inflater = getSupportMenuInflater();
            inflater.inflate(R.menu.menu_item, menu);
            return super.onCreateOptionsMenu(menu);
        }

        if(menunew){
            MenuInflater inflater = getSupportMenuInflater();
            inflater.inflate(R.menu.menu_save, menu);
            return super.onCreateOptionsMenu(menu);
        }
        return super.onCreateOptionsMenu(menu);
    }
}