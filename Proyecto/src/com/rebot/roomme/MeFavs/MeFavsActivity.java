package com.rebot.roomme.MeFavs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.rebot.roomme.R;

/**
 * Created by Strike on 4/24/14.
 */
public class MeFavsActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.favsme_layout);
    }
}
