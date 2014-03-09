package com.rebot.roomme;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainDrawer extends Activity {

    @InjectView(R.id.title) TextView title;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.inject(this);
        title.setText("Hola Mundo");
    }
}
