package com.rebot.roomme;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import com.parse.GetCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.rebot.roomme.Models.Users;
import com.rebot.roomme.RoomieSingle.SingleRoomieViewPagerContainer;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Toshiba on 01/08/2014.
 */
public class NotificationReceiver extends BroadcastReceiver {
    private Roome app;
    private String message, sender, objectId;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        app = (Roome) context.getApplicationContext();
        if(ParseUser.getCurrentUser() != null){
            if(!app.active){
                try {

                    this.context = context;
                    JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

                    message = json.optString("message", "");
                    sender = json.optString("sender", "");
                    objectId  = json.optString("objectId", "");

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("objectId", objectId);
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, com.parse.ParseException e) {
                            if(e == null){
                                if(ParseUser.getCurrentUser() != null){
                                    app.roomieSeleccionado = new Users(parseUser, CBR.calculaCBR(ParseUser.getCurrentUser(),parseUser));
                                    app.pestanaSingleRoomie = 1;
                                    displayNotification();
                                }
                            }
                        }
                    });

                } catch (JSONException e) {
                }
            }
        }


    }

    public void displayNotification(){
        Intent intent1 = new Intent(context, SingleRoomieViewPagerContainer.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent1, 0);
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context)
        	                    .setSmallIcon(R.drawable.logo)
        	                    .setContentTitle(this.sender)
        	                    .setContentText(this.message);
        noti.setContentIntent(pIntent);
        noti.setAutoCancel(true);

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        noti.setSound(uri);
        //Vibration
        noti.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        android.app.NotificationManager mNotificationManager = (android.app.NotificationManager)
                                                                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(2, noti.getNotification());
    }
}
