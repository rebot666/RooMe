package com.rebot.roomme.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.rebot.roomme.DptoSingle.*;
import com.rebot.roomme.R;

/**
 * Created by Strike on 6/5/14.
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    private String[] titles;
    private int mCount;
    private Context context;

    public FragmentAdapter(FragmentManager fm, boolean self, Context context) {
        super(fm);
        this.context = context;
        if(!self) {
            titles = context.getResources().getStringArray(R.array.title_options);
        }else{
            titles = context.getResources().getStringArray(R.array.my_publish_viewpager_options);
        }
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
            case 4:
                myFragment = new ComentariosHostFragment();
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
