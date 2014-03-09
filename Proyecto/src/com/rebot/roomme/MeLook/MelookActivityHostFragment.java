package com.rebot.roomme.MeLook;

import android.app.Activity;
import com.rebot.roomme.ActivityHostFragment.ActivityHostFragment;

/**
 * Created by Toshiba on 8/03/14.
 */
public class MelookActivityHostFragment extends ActivityHostFragment {
    @Override
    protected Class<? extends Activity> getActivityClass() {
        return MelookActivity.class;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
