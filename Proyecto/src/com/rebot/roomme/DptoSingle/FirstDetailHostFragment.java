package com.rebot.roomme.DptoSingle;

import android.app.Activity;
import com.rebot.roomme.ActivityHostFragment.ActivityHostFragment;

/**
 * Created by Strike on 6/6/14.
 */
public class FirstDetailHostFragment extends ActivityHostFragment {
    @Override
    protected Class<? extends Activity> getActivityClass() {
        return FirstDetailActivity.class;
    }
}
