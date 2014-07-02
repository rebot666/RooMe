package com.rebot.roomme.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.*;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Strike on 6/6/14.
 */
public class OffersAdapter extends ArrayAdapter<ParseObject> {
    Roome app;
    Context context;
    int layoutResourceId;
    ParseObject data[];

    public OffersAdapter(Context context, int layoutResourceId, ArrayList<ParseObject> data, Roome app) {
        super(context, layoutResourceId, data.toArray(new ParseObject[data.size()]));
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data.toArray(new ParseObject[data.size()]);
        this.app = app;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GenericListHolder holder = null;

        if(row == null)      {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new GenericListHolder();
            holder.img_user = (ImageView) row.findViewById(R.id.img_user);
            holder.txt_comments = (TextView) row.findViewById(R.id.txt_comments);
            holder.who_offers = (TextView) row.findViewById(R.id.txt_user_name);

            row.setTag(holder);
        } else {
            holder = (GenericListHolder) row.getTag();
        }

        ParseObject offer = data[position];

        String comments = offer.getString("comments");

        if(comments != null){
            holder.txt_comments.setVisibility(View.VISIBLE);
            holder.txt_comments.setText(offer.getString("comments"));
        } else {
            holder.txt_comments.setVisibility(View.GONE);
        }

        ParseUser who = offer.getParseUser("who");
        JSONObject profile = who.getJSONObject("profile");
        String idUser = profile.optString("facebookId");

        ImageLoader.getInstance().displayImage("http://graph.facebook.com/"+idUser+"/picture?type=large",
                            holder.img_user, app.options, app.animateFirstListener);

        String name = profile.optString("name");
        holder.who_offers.setText(name);

        return row;
    }

    static class GenericListHolder
    {
        ImageView img_user;
        TextView txt_comments, who_offers;
    }
}
