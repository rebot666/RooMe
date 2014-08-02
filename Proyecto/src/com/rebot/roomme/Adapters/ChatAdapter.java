package com.rebot.roomme.Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Toshiba on 31/07/2014.
 */
public class ChatAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ParseObject> mMessages;
    private Roome app;



    public ChatAdapter(Context context, ArrayList<ParseObject> messages, Roome app) {
        super();
        this.mContext = context;
        this.mMessages = messages;
        this.app = app;
    }
    @Override
    public int getCount() {
        return mMessages.size();
    }
    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ParseObject message = (ParseObject) this.getItem(position);

        ViewHolder holder;
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_message_item, parent, false);
            holder.message = (TextView) convertView.findViewById(R.id.message_text);
            holder.author = (TextView)convertView.findViewById(R.id.name_message);
            holder.mensaje = (LinearLayout) convertView.findViewById(R.id.layout_mensaje);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.message.setText(message.getString("mensaje"));
        holder.author.setText(message.getString("autor"));


        try {
            if(message.getString("autor").equals(ParseUser.getCurrentUser().getJSONObject("profile").getString("name")))
            {
                holder.message.setGravity(Gravity.RIGHT);
                holder.author.setGravity(Gravity.RIGHT);
            }else{
                holder.message.setGravity(Gravity.LEFT);
                holder.author.setGravity(Gravity.LEFT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
    private static class ViewHolder
    {
        TextView message, author;
        LinearLayout mensaje;
    }

    @Override
    public long getItemId(int position) {
        //Unimplemented, because we aren't using Sqlite.
        return position;
    }

}

