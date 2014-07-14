package com.rebot.roomme.SearchFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.view.Window;
import com.parse.*;
import com.rebot.roomme.Adapters.LookMeAdpater;
import com.rebot.roomme.CBR;
import com.rebot.roomme.MeLookRoomie;
import com.rebot.roomme.Models.Users;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Strike on 7/8/14.
 */
public class SearchRoomme extends DialogFragment {
    public static final int MIN_COMP = 0;
    public static final int MAX_COMP = 100;

    private ArrayList<Users> roomies;
    private Context context;
    private Dialog dialog;
    private Roome app;

    private Button close, search, minus, plus;
    private RadioGroup genero;
    private TextView max, min;
    private int maxc, minc;
    private EditText edit;


    public SearchRoomme(Context ctx, Roome app){
        this.app = app;
        this.context = ctx;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.search_roomme_layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(R.color.translucent_black));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        this.close = (Button) dialog.findViewById(R.id.close);
        this.search = (Button) dialog.findViewById(R.id.btn_search);
        this.edit = (EditText) dialog.findViewById(R.id.editText);
        this.plus = (Button) dialog.findViewById(R.id.btn_plus);
        this.minus = (Button) dialog.findViewById(R.id.btn_minus);
        this.max = (TextView) dialog.findViewById(R.id.max);
        this.min = (TextView) dialog.findViewById(R.id.min);
        this.genero = (RadioGroup) dialog.findViewById(R.id.genero);

        this.minc = 0;
        this.maxc = 100;

        this.close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        this.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edad = edit.getText().toString();

                if(!edad.equalsIgnoreCase("") || edad != null){
                    app.age = Integer.parseInt(edad);
                }

                int gen = genero.getCheckedRadioButtonId();
                if(gen == R.id.masculino) {
                    app.genre = "M";
                } else if(gen == R.id.femenino){
                    app.genre = "F";
                } else {
                    app.genre = "B";
                }

                app.mincomp = minc;
                app.maxcomp = maxc;

                querySearchRoomie();
            }
        });

        this.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = edit.getText().toString();
                int edad = Integer.parseInt(value);
                edad+=1;
                value = String.format("%d", edad);
                edit.setText(value);
            }
        });

        this.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = edit.getText().toString();
                int edad = Integer.parseInt(value);

                if(edad <= 0){
                    edad=0;
                } else {
                    edad-=1;
                }

                value = String.format("%d", edad);
                edit.setText(value);
            }
        });

        RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(0, 100, context);
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                String min_val = String.format("%d", minValue);
                String max_val = String.format("%d", maxValue);

                minc = minValue;
                maxc = maxValue;

                min.setText("Mínimo: " + min_val + "%");
                max.setText("Máximo: " + max_val + "%");
            }
        });

        LinearLayout linear = (LinearLayout) dialog.findViewById(R.id.seekBar_layout);
        linear.addView(seekBar,2);

        return dialog;
    }

    public void querySearchRoomie() {
        ParseQuery<ParseUser> roomie = ParseUser.getQuery();

        if(app.age != 0){
            roomie.whereGreaterThanOrEqualTo("", app.age);
        }

        if(!app.genre.equalsIgnoreCase("B")){
            roomie.whereEqualTo("", app.genre);
        }

        roomie.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e == null){
                    if(parseUsers.size() > 0){
                        roomies.clear();
                        ParseUser me = ParseUser.getCurrentUser();
                        if(me != null){
                            for(ParseUser temp : parseUsers){
                                if(temp.getBoolean("esRoomie")){
                                    if(!me.getObjectId().equals(temp.getObjectId())){
                                        double porcentaje = CBR.calculaCBR(me, temp);

                                        if(porcentaje >= app.mincomp && porcentaje <= app.maxcomp){
                                            Users tempUser = new Users(temp, porcentaje);
                                            roomies.add(tempUser);
                                        }
                                    }
                                }
                            }

                            Collections.sort(roomies);
                        }else{
                            for(ParseUser temp : parseUsers){
                                if(temp.getBoolean("esRoomie")){
                                    double porcentaje = -1.0;
                                    Users tempUser = new Users(temp, porcentaje);
                                    roomies.add(tempUser);
                                }

                            }
                        }


                        app.list_roomie.setAdapter(new LookMeAdpater(context,
                                R.layout.lookme_list_item, roomies, app));
                        app.list_roomie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                app.roomieSeleccionado = roomies.get(position);
                                Intent intent = new Intent(context, MeLookRoomie.class);
                                context.startActivity(intent);
                            }
                        });
                        Crouton.makeText((Activity) context, "Resultados de búsqueda", Style.INFO).show();
                    }
                } else {
                    app.noInfo.setVisibility(View.VISIBLE);
                    Crouton.makeText((Activity) context, "Resultados de búsqueda", Style.INFO).show();
                }
            }
        });
    }
}
