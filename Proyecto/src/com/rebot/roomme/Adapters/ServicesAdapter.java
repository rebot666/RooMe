package com.rebot.roomme.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;

import java.util.ArrayList;

/**
 * Created by Strike on 6/22/14.
 */
public class ServicesAdapter extends ArrayAdapter<ParseObject> {
    Roome app;
    Context context;
    int layoutResourceId;
    ParseObject data[];
    boolean check_item;

    public ServicesAdapter(Context context, int layoutResourceId, ArrayList<ParseObject> data, Roome app) {
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

            holder.container = (RelativeLayout) row.findViewById(R.id.relative_grid);

            /*Resources r = Resources.getSystem();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());
            holder.container.setLayoutParams(new GridView.LayoutParams((int) px, (int) px));*/

            holder.img_service = (ImageView) row.findViewById(R.id.img_service);
            holder.txt_name = (TextView) row.findViewById(R.id.txt_name);
            holder.more_info = (TextView) row.findViewById(R.id.txt_desc);

            row.setTag(holder);
        } else {
            holder = (GenericListHolder) row.getTag();
        }

        ParseObject service_dpto = (ParseObject) data[position];

        String description = service_dpto.getString("description");

        if(description != null){
            holder.more_info.setVisibility(View.VISIBLE);
            holder.more_info.setText(description);
        } else {
            holder.more_info.setVisibility(View.GONE);
        }

        ParseObject service = service_dpto.getParseObject("service");
        ParseFile imageProduct = service.getParseFile("image");

        if(imageProduct != null){
            ImageLoader.getInstance().displayImage(imageProduct.getUrl(), holder.img_service,
                    this.app.options, this.app.animateFirstListener);
        }

        holder.txt_name.setText(service.getString("name"));
        return row;
    }

    static class GenericListHolder
    {
        ImageView img_service;
        TextView txt_name, more_info;
        RelativeLayout container;
    }
}