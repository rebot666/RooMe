package com.rebot.roomme.DptoSingle;

import android.app.Activity;
import com.rebot.roomme.ActivityHostFragment.ActivityHostFragment;

/**
 * Created by Strike on 6/22/14.
 */
public class ServicesHostFragment extends ActivityHostFragment {
    @Override
    protected Class<? extends Activity> getActivityClass() {
        return ServicesActivity.class;
    }
}
