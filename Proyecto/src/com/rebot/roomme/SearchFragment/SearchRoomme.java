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
import com.rebot.roomme.Adapters.CountryAdapter;
import com.rebot.roomme.Adapters.LookMeAdpater;
import com.rebot.roomme.CBR;
import com.rebot.roomme.MeLookRoomie;
import com.rebot.roomme.Models.Users;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Strike on 7/8/14.
 */
public class SearchRoomme extends DialogFragment {
    public static final int MIN_COMP = 0;
    public static final int MAX_COMP = 100;
    private boolean scrolling = false;

    private ArrayList<String> countries, cities;
    private ArrayList<Users> roomies;
    private WheelView country, state;
    private Context context;
    private Dialog dialog;
    private Roome app;

    private Button close, search, minus, plus;
    private RadioGroup genero;
    private TextView max, min;
    private int maxc, minc;
    private EditText edit;

    public SearchRoomme(){

    }

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

        roomies = new ArrayList<Users>();
        countries = new ArrayList<String>();
        cities = new ArrayList<String>();

        this.minc = 0;
        this.maxc = 100;

        country = (WheelView) dialog.findViewById(R.id.country);
        state = (WheelView) dialog.findViewById(R.id.state);

        getCountries();

        country.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    updateCities(newValue);
                }
            }
        });

        country.addScrollingListener( new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
                scrolling = true;
            }
            public void onScrollingFinished(WheelView wheel) {
                scrolling = false;
                updateCities(country.getCurrentItem());
            }
        });

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
                    app.genre = "male";
                } else if(gen == R.id.femenino){
                    app.genre = "female";
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

        RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(MIN_COMP, MAX_COMP, context);
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
        roomie.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e == null){
                    dialog.dismiss();
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

                                            JSONObject profile = temp.getJSONObject("profile");

                                            if(!app.genre.equalsIgnoreCase("B")){
                                                if(profile.optString("gender").equalsIgnoreCase(app.genre)) {
                                                    if(app.age != 0){
                                                        int edad = cumpleanos(profile.optString("birthday"));
                                                        if(edad >= app.age){
                                                            roomies.add(tempUser);
                                                        }
                                                    } else {
                                                        roomies.add(tempUser);
                                                    }
                                                }
                                            } else if(app.age != 0){
                                                int edad = cumpleanos(profile.optString("birthday"));
                                                if(edad >= app.age){
                                                    roomies.add(tempUser);
                                                }
                                            } else {
                                                roomies.add(tempUser);
                                            }
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

                                    JSONObject profile = temp.getJSONObject("profile");

                                    if(!app.genre.equalsIgnoreCase("B")){
                                        if(profile.optString("gender").equalsIgnoreCase(app.genre)) {
                                            if(app.age != 0){
                                                int edad = cumpleanos(profile.optString("birthday"));
                                                if(edad >= app.age){
                                                    roomies.add(tempUser);
                                                }
                                            } else {
                                                roomies.add(tempUser);
                                            }
                                        }
                                    } else if(app.age != 0){
                                        int edad = cumpleanos(profile.optString("birthday"));
                                        if(edad >= app.age){
                                            roomies.add(tempUser);
                                        }
                                    } else {
                                        roomies.add(tempUser);
                                    }
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

    public void getCountries(){
        ParseQuery<ParseObject> get_countries = ParseQuery.getQuery("Paises");
        get_countries.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null){
                    countries.clear();
                    countries.add("Todos");
                    if(parseObjects.size() != 0){
                        for(ParseObject object : parseObjects){
                            if(!countries.contains(object.getString("pais"))){
                                countries.add(object.getString("pais"));
                            }
                        }

                        country.setVisibleItems(parseObjects.size());
                        country.setViewAdapter(new CountryAdapter(context, countries));

                        country.setCurrentItem(0);

                        updateCities(0);
                    }
                }
            }
        });
    }

    public void updateCities(int country){
        if(country != 0){
            ParseQuery<ParseObject> get_states = ParseQuery.getQuery("Paises");
            get_states.whereEqualTo("pais", countries.get(country));
            get_states.orderByAscending("state");
            get_states.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if(e == null){
                        cities.clear();
                        cities.add("Todos");
                        if(parseObjects.size() != 0){
                            for(ParseObject object : parseObjects){
                                cities.add(object.getString("state"));
                            }

                            state.setVisibleItems(parseObjects.size());
                            state.setViewAdapter(new CountryAdapter(context, cities));

                            state.setCurrentItem(0);
                        }
                    }
                }
            });
        } else {
            cities.clear();
            cities.add("Todos");
            state.setVisibleItems(1);
            state.setViewAdapter(new CountryAdapter(context, cities));
            state.setCurrentItem(0);
        }
    }

    public static int cumpleanos(String current){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date actual = new Date();

        try {
            Date birthdayCurrent = formatter.parse(current);

            Calendar cal = Calendar.getInstance();
            cal.setTime(birthdayCurrent);
            int yearCurrent = cal.get(Calendar.YEAR);
            cal.setTime(actual);
            int yearActual = cal.get(Calendar.YEAR);

            return yearActual-yearCurrent;

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
