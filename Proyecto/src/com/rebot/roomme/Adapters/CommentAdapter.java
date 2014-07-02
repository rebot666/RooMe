package com.rebot.roomme.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;

import java.util.ArrayList;

/**
 * Created by Strike on 6/24/14.
 */
public class CommentAdapter  extends ArrayAdapter<ParseObject> {
    Roome app;
    Context context;
    int layoutResourceId;
    ParseObject data[];
    Boolean count;

    public CommentAdapter(Context context, int layoutResourceId,
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
            holder.user = (ImageView) row.findViewById(R.id.img_depa);
            holder.comment = (TextView) row.findViewById(R.id.txt_price);
            holder.rating = (TextView) row.findViewById(R.id.txt_direccion);

            row.setTag(holder);
        } else {
            holder = (GenericListHolder) row.getTag();
        }

        ParseObject comment = data[position];

        holder.rating.setText("" + comment.getNumber("rating"));
        holder.comment.setText(comment.getString("comentario"));

        ParseFile img_portada = comment.getParseFile("img_portada");
        ImageLoader.getInstance().displayImage(img_portada.getUrl(),
                holder.user, app.options3, app.animateFirstListener);

        return row;
    }

    static class GenericListHolder
    {
        ImageView user;
        TextView comment, rating;
    }
}