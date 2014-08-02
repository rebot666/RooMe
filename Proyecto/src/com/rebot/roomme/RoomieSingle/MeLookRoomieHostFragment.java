package com.rebot.roomme.RoomieSingle;

import android.app.Activity;
import com.rebot.roomme.ActivityHostFragment.ActivityHostFragment;
import com.rebot.roomme.DptoSingle.ServicesActivity;

/**
 * Created by Toshiba on 31/07/2014.
 */
public class MeLookRoomieHostFragment extends ActivityHostFragment {
    @Override
    protected Class<? extends Activity> getActivityClass() {
        return MeLookRoomie.class;
    }
}
