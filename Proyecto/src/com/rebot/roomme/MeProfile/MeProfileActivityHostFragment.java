package com.rebot.roomme.MeProfile;

import android.app.Activity;
import com.rebot.roomme.ActivityHostFragment.ActivityHostFragment;
import com.rebot.roomme.MeLook.MelookActivity;

/**
 * Created by Toshiba on 8/03/14.
 */
public class MeProfileActivityHostFragment extends ActivityHostFragment {
    @Override
    protected Class<? extends Activity> getActivityClass() {
        return MeProfileActivity.class;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
