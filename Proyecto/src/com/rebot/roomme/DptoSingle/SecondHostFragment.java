package com.rebot.roomme.DptoSingle;

import android.app.Activity;
import com.rebot.roomme.ActivityHostFragment.ActivityHostFragment;
import com.rebot.roomme.MeGo.MeGoActivity;

/**
 * Created by Strike on 6/6/14.
 */
public class SecondHostFragment extends ActivityHostFragment {
    @Override
    protected Class<? extends Activity> getActivityClass() {
        return SecondDetailActivity.class;
    }
}
