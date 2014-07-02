package com.rebot.roomme.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.rebot.roomme.DptoSingle.ContactHostFragment;
import com.rebot.roomme.DptoSingle.FirstDetailHostFragment;
import com.rebot.roomme.DptoSingle.SecondHostFragment;
import com.rebot.roomme.DptoSingle.ServicesHostFragment;

/**
 * Created by Strike on 6/5/14.
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    private String[] titles;
    private int mCount;

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
        titles = new String[] {"Departamento","Detalles","Servicios","Contacto"};
        mCount = titles.length;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment myFragment = new FirstDetailHostFragment();
        switch(i){
            case 0:
                myFragment = new FirstDetailHostFragment();
                break;
            case 1:
                myFragment = new SecondHostFragment();
                break;
            case 2:
                myFragment = new ServicesHostFragment();
                break;
            case 3:
                myFragment = new ContactHostFragment();
                break;
        }
        return  myFragment;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
