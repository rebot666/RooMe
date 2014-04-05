package com.rebot.roomme.MeProfile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Toshiba on 8/03/14.
 */
public class MeProfileActivity extends Activity {
    private Button btn_sign_facebook;
    private Roome app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profileme_layout);

        app = (Roome) getApplication();
        ParseFacebookUtils.initialize(getString(R.string.app_id));
        btn_sign_facebook = (Button) findViewById(R.id.btn_facebook);

        btn_sign_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_facebook();
            }
        });
    }


    public void login_facebook() {
        List<String> permissions = Arrays.asList("basic_info", "user_birthday", "user_location");
        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    //c = Crouton.makeText(LoginActivity.this, R.string.login_fail, Style.ALERT);
                    //c.show();
                } else if (user.isNew()) {
                    //Intent next = new Intent(LoginActivity.this, MainDrawerActivity.class);
                    //LoginActivity.this.startActivity(next);

                    //finish();
                } else {
                    //Intent next = new Intent(LoginActivity.this, MainDrawerActivity.class);
                    //LoginActivity.this.startActivity(next);

                    //finish();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        //Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }
}