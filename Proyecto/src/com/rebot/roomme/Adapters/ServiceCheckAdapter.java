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
import com.rebot.roomme.Models.Services;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;

import java.util.ArrayList;

/**
 * Created by Strike on 6/24/14.
 */
public class ServiceCheckAdapter extends BaseAdapter {
    Roome app;
    Context context;
    int layoutResourceId;
    ArrayList<Services> data;

    public ServiceCheckAdapter(Context context, int layoutResourceId, ArrayList<Services> data, Roome app) {
        //super(context, layoutResourceId, data.toArray(new ParseObject[data.size()]));
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.app = app;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
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
            holder.more_info = (EditText) row.findViewById(R.id.txt_desc);

            holder.checkBox = (CheckBox) row.findViewById(R.id.checkBox);

            row.setTag(holder);
        } else {
            holder = (GenericListHolder) row.getTag();
        }

        Services service = (Services) data.get(position);

        holder.txt_name.setText(service.getServiceParse().getString("name"));

        ParseFile imageProduct = service.getServiceParse().getParseFile("image");

        if(imageProduct != null){
            ImageLoader.getInstance().displayImage(imageProduct.getUrl(), holder.img_service,
                    this.app.options, this.app.animateFirstListener);
        }

        final int pos = position;
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    app.services.get(pos).setChecked(true);
                }else{
                    app.services.get(pos).setChecked(false);
                }
            }
        });

        holder.more_info.setVisibility(View.GONE);

        return row;
    }

    static class GenericListHolder
    {
        ImageView img_service;
        EditText more_info;
        TextView txt_name;
        CheckBox checkBox;
        RelativeLayout container;
    }
}
