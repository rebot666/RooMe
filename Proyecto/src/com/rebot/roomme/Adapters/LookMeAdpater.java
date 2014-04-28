package com.rebot.roomme.Adapters;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseObject;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Strike on 4/27/14.
 */
public class LookMeAdpater extends ArrayAdapter<ParseObject> {
    Roome app;
    Context context;
    int layoutResourceId;
    ParseObject data[];

    public LookMeAdpater(Context context, int layoutResourceId, ArrayList<ParseObject> data, Roome app) {
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
            holder.male = (ImageView) row.findViewById(R.id.circle_male);
            holder.female = (ImageView) row.findViewById(R.id.circle_women);

            row.setTag(holder);
        } else {
            holder = (GenericListHolder) row.getTag();
        }

        final ParseObject objeto = data[position];
        JSONObject profile = objeto.getJSONObject("profile");

        try {
            if(profile.getString("gender").equals("male")){
                holder.female.setVisibility(View.INVISIBLE);
                holder.male.setVisibility(View.VISIBLE);

                ImageLoader.getInstance().displayImage("", holder.male, app.options, app.animateFirstListener);
            } else {
                holder.female.setVisibility(View.VISIBLE);
                holder.male.setVisibility(View.INVISIBLE);

                ImageLoader.getInstance().displayImage("", holder.female, app.options, app.animateFirstListener);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return row;
    }

    static class GenericListHolder
    {
        ImageView male;
        ImageView female;
    }
}