package com.rebot.roomme.RoomieSingle;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.parse.*;
import com.rebot.roomme.Adapters.ChatAdapter;
import com.rebot.roomme.Roome;
import com.rebot.roomme.R;
import com.todddavies.components.progressbar.ProgressWheel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Toshiba on 31/07/2014.
 */
public class RoomieChat extends FragmentActivity {
    private Context context = this;
    private Roome app;
    ArrayList<ParseObject> messages;
    ArrayList<ParseUser> userData;
    ChatAdapter adapter;
    //UsersAdapter adapterUsers;
    static Random rand = new Random();
    static String sender;
    private Handler chatHandler = new Handler();
    private ParseObject currentChat;
    private ParseUser meObject, otherPersonObject;
    private EditText messageText;
    private Button sendButton;
    private TextView empty;
    private ListView listView;
    private RelativeLayout loading_info;
    private ProgressWheel loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomie_chat_layout);


        app = (Roome) getApplication();

        meObject = ParseUser.getCurrentUser();
        otherPersonObject = app.roomieSeleccionado.getUser();
        currentChat = null;
        loading_info = (RelativeLayout) findViewById(R.id.loading_info);
        loader = (ProgressWheel) findViewById(R.id.pw_spinner);
        messageText = (EditText) findViewById(R.id.message);
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setClickable(false);
        empty = (TextView) findViewById(R.id.empty);
        listView = (ListView) findViewById(R.id.list_view);
        ParseAnalytics.trackAppOpened(getIntent());
        obtenerMensajesChat();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();//go back to the previous Activity

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void sendMessage(View v)
    {

        String newMessage = messageText.getText().toString().trim();
        if(newMessage.length() > 0)
        {
            messageText.setText("");
            try {
                addNewMessage(newMessage, ParseUser.getCurrentUser().getJSONObject("profile").getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    void addNewMessage(String message, String nickname)
    {
        if(currentChat != null){
            //El Chat ya existe y se agrega solo el nuevo mensaje
            ParseObject messageObject = new ParseObject("Mensajes");
            messageObject.put("mensaje", message);
            messageObject.put("chat", currentChat);
            messageObject.put("autor", nickname);
            messageObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        //chatHandler.postDelayed(mensajes_chat, 0);
                    }else{
                        //chatHandler.postDelayed(mensajes_chat, 0);

                    }
                }
            });
            try {
                sendNotification(message, otherPersonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            final String messageFinal = message;
            final String nicknameFinal = nickname;
            ParseObject chatNuevo = new ParseObject("Chats");
            ArrayList<String> users = new ArrayList<String>();
            users.add(meObject.getObjectId());
            users.add(otherPersonObject.getObjectId());
            chatNuevo.put("users", users);
            chatNuevo.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Chats");
                        ArrayList<String> users = new ArrayList<String>();
                        users.add(meObject.getObjectId());
                        users.add(otherPersonObject.getObjectId());
                        query.whereContainsAll("users",users);
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                if(e == null){
                                    if(parseObject != null){
                                        currentChat = parseObject;
                                        ParseObject messageObject = new ParseObject("Mensajes");
                                        messageObject.put("mensaje", messageFinal);
                                        messageObject.put("chat", currentChat);
                                        messageObject.put("autor", nicknameFinal);
                                        messageObject.saveInBackground();
                                        try {
                                            sendNotification(messageFinal, otherPersonObject);
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                    }else{

                                    }

                                }
                            }
                        });
                    }
                }
            });
        }



    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onPause(){
        super.onPause();
        chatHandler.removeCallbacks(mensajes_chat);
    }

    private Runnable mensajes_chat = new Runnable(){

        @Override
        public void run() {
            queryMensajes(5000);
        }
    };

    public void obtenerMensajesChat(){
        loader.spin();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Chats");
        ArrayList<String> users = new ArrayList<String>();
        users.add(meObject.getObjectId());
        users.add(otherPersonObject.getObjectId());
        query.whereContainsAll("users", users);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e == null){
                    if(parseObject != null){
                        currentChat = parseObject;
                        sendButton.setClickable(true);
                        queryMensajes(0);
                    }
                }else{
                    sendButton.setClickable(true);
                    loader.stopSpinning();
                    loading_info.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void queryMensajes(int delay){
        final int currentDelay = delay;
        ParseQuery query = new ParseQuery("Mensajes");
        query.whereEqualTo("chat", currentChat);
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {
                empty.setVisibility(View.VISIBLE);
                if(e == null){
                    if(messages != null && messages.size() > 0){
                        empty.setVisibility(View.GONE);
                        if(list.size() > messages.size()){
                            messages = (ArrayList<ParseObject>) list;
                            adapter = new ChatAdapter(context, messages, app);
                            listView.setAdapter(adapter);
                            listView.setSelection(messages.size()-1);
                            chatHandler.postDelayed(mensajes_chat, currentDelay);
                        }else{
                            chatHandler.postDelayed(mensajes_chat, currentDelay);
                        }
                    }else{
                        if(list.size() > 0){
                            empty.setVisibility(View.GONE);
                            messages = (ArrayList<ParseObject>) list;
                            adapter = new ChatAdapter(context, messages, app);
                            listView.setAdapter(adapter);
                            listView.setSelection(messages.size()-1);
                        }
                        chatHandler.postDelayed(mensajes_chat, currentDelay);
                    }
                }else{
                    chatHandler.postDelayed(mensajes_chat, currentDelay);
                }
                loader.stopSpinning();
                loading_info.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

            }
        });
    }

    public void sendNotification(String message, ParseUser senderMessage) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("message", message);
        data.put("sender", meObject.getJSONObject("profile").getString("name"));
        data.put("objectId", meObject.getObjectId());
        data.put("action", "com.rebot.roomme.UPDATE_STATUS");

        ParsePush push = new ParsePush();
        push.setChannel(senderMessage.getObjectId());
        push.setData(data);
        push.sendInBackground();
    }

    @Override
    public void onStart() {
        super.onStart();
        app.active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        app.active = false;
    }
}
