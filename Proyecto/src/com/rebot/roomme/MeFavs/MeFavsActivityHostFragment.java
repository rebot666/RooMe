package com.rebot.roomme.MeFavs;

import android.app.Activity;
import com.rebot.roomme.ActivityHostFragment.ActivityHostFragment;

/**
 * Created by Strike on 4/24/14.
 */
public class MeFavsActivityHostFragment extends ActivityHostFragment {
    @Override
    protected Class<? extends Activity> getActivityClass() {
        return MeFavsActivity.class;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
