package com.rebot.roomme.Adapters;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.rebot.roomme.CBR;
import com.rebot.roomme.Models.Users;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import com.todddavies.components.progressbar.ProgressWheel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Strike on 4/27/14.
 */
public class LookMeAdpater extends ArrayAdapter<Users> {
    Roome app;
    Context context;
    int layoutResourceId;
    Users data[];

    public LookMeAdpater(Context context, int layoutResourceId, ArrayList<Users> data, Roome app) {
        super(context, layoutResourceId, data.toArray(new Users[data.size()]));
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data.toArray(new Users[data.size()]);
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
            holder.layout_female = (View) row.findViewById(R.id.branch_left);
            holder.layout_male = (View) row.findViewById(R.id.branch_right);
            holder.name_female = (TextView) row.findViewById(R.id.name_female);
            holder.name_male = (TextView) row.findViewById(R.id.name_male);
            holder.percentage_female = (ProgressWheel) row.findViewById(R.id.percentage_female);
            holder.percentage_male = (ProgressWheel) row.findViewById(R.id.percentage_male);
            holder.layoutImageFemale = (RelativeLayout) row.findViewById(R.id.layout_image_female);
            holder.layoutImageMale = (RelativeLayout) row.findViewById(R.id.layout_image_male);
            holder.backgroundLayout = (LinearLayout) row.findViewById(R.id.layout_background);

            row.setTag(holder);
        } else {
            holder = (GenericListHolder) row.getTag();
        }


        ParseUser me = ParseUser.getCurrentUser();
        final Users objectoUsersActual = data[position];
        ParseObject objeto = objectoUsersActual.getUser();
        JSONObject profile = objeto.getJSONObject("profile");
        String idUser = "";
        double porcentaje = -1.0;

        if(me != null){
            porcentaje  = CBR.calculaCBR(me, objeto);
            porcentaje = (porcentaje * 360) / 100;
        }else{
            holder.percentage_female.setVisibility(View.GONE);
            holder.percentage_male.setVisibility(View.GONE);
        }

        try {
            idUser = profile.getString("facebookId");
            if(profile.getString("gender").equals("male")){
                YoYo.with(Techniques.SlideInRight)
                        .duration(500)
                        .playOn(holder.backgroundLayout);
                holder.layout_female.setVisibility(View.GONE);
                holder.female.setVisibility(View.GONE);
                holder.layout_male.setVisibility(View.VISIBLE);
                holder.male.setVisibility(View.VISIBLE);
                holder.layoutImageMale.setVisibility(View.VISIBLE);
                holder.layoutImageFemale.setVisibility(View.GONE);
                holder.name_male.setVisibility(View.VISIBLE);
                holder.name_female.setVisibility(View.GONE);
                holder.name_male.setText(profile.getString("name"));
                if(porcentaje != -1.0){
                    if(porcentaje == 360.0){
                        holder.percentage_male.setBarColor(context.getResources().getColor(R.color.amarillo_r));

                    }else{
                        holder.percentage_female.setBarColor(context.getResources().getColor(R.color.naranja_circulo_progress));
                    }
                    holder.percentage_male.setProgress((int)porcentaje);

                }else{

                }

                ImageLoader.getInstance().displayImage("http://graph.facebook.com/"+idUser+"/picture?type=large",
                        holder.male, app.options, app.animateFirstListener);

            } else {
                YoYo.with(Techniques.SlideInLeft)
                        .duration(500)
                        .playOn(holder.backgroundLayout);
                holder.layout_female.setVisibility(View.VISIBLE);
                holder.female.setVisibility(View.VISIBLE);
                holder.layout_male.setVisibility(View.GONE);
                holder.male.setVisibility(View.GONE);
                holder.layoutImageMale.setVisibility(View.GONE);
                holder.layoutImageFemale.setVisibility(View.VISIBLE);
                holder.name_male.setVisibility(View.GONE);
                holder.name_female.setVisibility(View.VISIBLE);
                holder.name_female.setText(profile.getString("name"));
                if(porcentaje != -1.0){
                    if(porcentaje == 360.0){
                        holder.percentage_female.setBarColor(context.getResources().getColor(R.color.amarillo_r));

                    }else{
                        holder.percentage_female.setBarColor(context.getResources().getColor(R.color.naranja_circulo_progress));
                    }
                    holder.percentage_female.setProgress((int)porcentaje);
                }else{

                }

                ImageLoader.getInstance().displayImage("http://graph.facebook.com/"+idUser+"/picture?type=large",
                        holder.female, app.options, app.animateFirstListener);

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
        View layout_female, layout_male;
        TextView name_female, name_male;
        ProgressWheel percentage_female;
        ProgressWheel percentage_male;
        RelativeLayout layoutImageFemale, layoutImageMale;
        LinearLayout backgroundLayout;
    }
}