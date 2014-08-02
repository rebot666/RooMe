package com.rebot.roomme.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.parse.ParseUser;
import com.rebot.roomme.DptoSingle.*;
import com.rebot.roomme.R;
import com.rebot.roomme.RoomieSingle.MeLookRoomieHostFragment;
import com.rebot.roomme.RoomieSingle.RoomieChatHostFragment;

/**
 * Created by Toshiba on 31/07/2014.
 */
public class RoomieSingleAdapter extends FragmentPagerAdapter {
    private String[] titles;
    private int mCount;
    private Context context;

    public RoomieSingleAdapter(FragmentManager fm, boolean self, Context context) {
        super(fm);
        this.context = context;
        if(ParseUser.getCurrentUser() != null){
            titles = context.getResources().getStringArray(R.array.roomie_single_view_pager_title);
        }else{
            titles = context.getResources().getStringArray(R.array.roomie_single_view_pager_title_no_account);
        }

        mCount = titles.length;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment myFragment = new MeLookRoomieHostFragment();
        switch(i){
            case 0:
                myFragment = new MeLookRoomieHostFragment();
                break;
            case 1:
                myFragment = new RoomieChatHostFragment();
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

