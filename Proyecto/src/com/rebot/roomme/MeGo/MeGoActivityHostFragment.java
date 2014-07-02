package com.rebot.roomme.MeGo;

import android.app.Activity;
import com.rebot.roomme.ActivityHostFragment.ActivityHostFragment;
import com.rebot.roomme.MeProfile.PublicacionNva;

/**
 * Created by Strike on 4/24/14.
 */
public class MeGoActivityHostFragment extends ActivityHostFragment {
    @Override
    protected Class<? extends Activity> getActivityClass() {
        return MeGoActivity.class;
    }
}
