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
 * Created by Strike on 5/13/14.
 */
public class DepartmentAdapter extends ArrayAdapter<ParseObject> {
    Roome app;
    Context context;
    int layoutResourceId;
    ParseObject data[];
    Boolean count;

    public DepartmentAdapter(Context context, int layoutResourceId,
                             ArrayList<ParseObject> data, Roome app, Boolean count) {

        super(context, layoutResourceId, data.toArray(new ParseObject[data.size()]));
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data.toArray(new ParseObject[data.size()]);
        this.app = app;
        this.count = count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GenericListHolder holder = null;

        if(row == null)      {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new GenericListHolder();
            holder.department = (ImageView) row.findViewById(R.id.img_depa);
            holder.title_dpto = (TextView) row.findViewById(R.id.txt_title);
            holder.price_dpto = (TextView) row.findViewById(R.id.txt_price);
            holder.address_dpto = (TextView) row.findViewById(R.id.txt_direccion_lista);
            holder.txt_count = (TextView) row.findViewById(R.id.txt_count);
            holder.img_sex = (ImageView) row.findViewById(R.id.img_sex);
            holder.ribbon_destacado = (ImageView) row.findViewById(R.id.ribbon_destacado);
            holder.ribbon_roomme = (ImageView) row.findViewById(R.id.ribbon_roomme);

            row.setTag(holder);
        } else {
            holder = (GenericListHolder) row.getTag();
        }

        ParseObject dpto = data[position];

        holder.price_dpto.setText("$" + dpto.getNumber("price"));
        holder.address_dpto.setText(dpto.getString("address"));
        holder.title_dpto.setText(dpto.getString("title"));

        ParseFile img_portada = dpto.getParseFile("img_portada");
        ImageLoader.getInstance().displayImage(img_portada.getUrl(),
                holder.department, app.options3, app.animateFirstListener);

        String sex = dpto.getString("sex");
        if(sex.equalsIgnoreCase("B")){
            holder.img_sex.setImageResource(R.drawable.both);
        } else if(sex.equalsIgnoreCase("F")){
            holder.img_sex.setImageResource(R.drawable.female_icon);
        } else if(sex.equalsIgnoreCase("M")){
            holder.img_sex.setImageResource(R.drawable.male_icon);
        }

        if(!count){
            holder.txt_count.setVisibility(View.GONE);
            if(dpto.getBoolean("destacado")){
                holder.ribbon_destacado.setBackgroundResource(R.drawable.ribbon_d);
                holder.ribbon_destacado.setVisibility(View.VISIBLE);
            } else {
                holder.ribbon_destacado.setVisibility(View.GONE);
            }
        } else {
            holder.ribbon_destacado.setVisibility(View.VISIBLE);
            holder.ribbon_destacado.setBackgroundResource(R.drawable.count);
            holder.txt_count.setVisibility(View.VISIBLE);
            holder.txt_count.setText(dpto.getNumber("offers") + "");
        }

        if(!dpto.getBoolean("roommee")){
            holder.ribbon_roomme.setVisibility(View.GONE);
        } else {
            holder.ribbon_roomme.setVisibility(View.VISIBLE);
        }

        return row;
    }

    static class GenericListHolder
    {
        ImageView department, ribbon_roomme, img_sex, ribbon_destacado;
        TextView price_dpto, address_dpto, title_dpto, txt_count;
        Button favorite;
    }
}
