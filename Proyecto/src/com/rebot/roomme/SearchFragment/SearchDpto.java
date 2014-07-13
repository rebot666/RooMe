package com.rebot.roomme.SearchFragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.view.Window;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.rebot.roomme.R;
import com.rebot.roomme.Roome;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Strike on 7/8/14.
 */
public class SearchDpto extends DialogFragment {
    public static final int MIN_PRICE = 1000;
    public static final int MAX_PRICE = 1000000;

    private ArrayList<ParseObject> dptos;
    private Context context;
    private Dialog dialog;
    private Roome app;

    private Button close, search, minus, plus;
    private RadioGroup transaction;
    private EditText editText;
    private TextView max, min;
    private CheckBox checkBox;
    private int maxp, minp;

    public SearchDpto(Context ctx, Roome app){
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
        dialog.setContentView(R.layout.search_dpto_layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(R.color.translucent_black));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        this.close = (Button) dialog.findViewById(R.id.close);
        this.search = (Button) dialog.findViewById(R.id.btn_search);
        this.editText = (EditText) dialog.findViewById(R.id.editText);
        this.checkBox = (CheckBox) dialog.findViewById(R.id.ch_fotos);
        this.plus = (Button) dialog.findViewById(R.id.btn_plus);
        this.minus = (Button) dialog.findViewById(R.id.btn_minus);
        this.max = (TextView) dialog.findViewById(R.id.max);
        this.min = (TextView) dialog.findViewById(R.id.min);
        this.transaction = (RadioGroup) dialog.findViewById(R.id.trans);

        minp = MIN_PRICE;
        maxp = MAX_PRICE;

        this.close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        this.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String room = editText.getText().toString();

                if(!room.equalsIgnoreCase("") || room != null){
                    app.rooms = Integer.parseInt(room);
                }

                boolean photo = checkBox.isChecked();
                if(photo) {
                    app.photos = true;
                } else {
                    app.photos = false;
                }

                int tipoTrans = transaction.getCheckedRadioButtonId();
                if(tipoTrans == R.id.renta){
                    app.trans = "renta";
                } else if(tipoTrans == R.id.venta){
                    app.trans = "venta";
                } else {
                    app.trans = "B";
                }

                app.minprice = minp;
                app.maxprice = maxp;

                querySearchDpto();
            }
        });

        this.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = editText.getText().toString();
                int edad = Integer.parseInt(value);
                edad+=1;
                value = String.format("%d", edad);
                editText.setText(value);
            }
        });

        this.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = editText.getText().toString();
                int edad = Integer.parseInt(value);

                if(edad <= 0){
                    edad=0;
                } else {
                    edad-=1;
                }

                value = String.format("%d", edad);
                editText.setText(value);
            }
        });

        // create RangeSeekBar as Integer range between 20 and 75
        RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(MIN_PRICE, MAX_PRICE, context);
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                // handle changed range values
                minp = minValue;
                maxp = maxValue;

                min.setText("Min: " + formatPrice(minp));
                max.setText("Max: " + formatPrice(maxp));
            }
        });

        // add RangeSeekBar to pre-defined layout
        LinearLayout linear = (LinearLayout) dialog.findViewById(R.id.seekBar_layout);
        linear.addView(seekBar, 2);
        return dialog;
    }

    public String formatPrice(int value){
        String price = "";
        String precio = String.format("%d", value);

        if(value <= 999) {
            price = String.format("$%d", value);
        } else if(value > 999 && value <= 9999) {
            price = String.format("$%c,%s", precio.charAt(0), precio.substring(1,precio.length()));
        } else if(value > 9999 && value <= 99999) {
            price = String.format("$%s,%s", precio.substring(0,1), precio.substring(2, precio.length()));
        } else if(value > 99999 && value <= 999999) {
            price = String.format("$%s,%s", precio.substring(0,2), precio.substring(3, precio.length()));
        } else if(value > 999999 && value <= 9999999) {
            price = String.format("$%c,%s,%s", precio.charAt(0), precio.substring(1,4),
                    precio.substring(4, precio.length()));
        } else if(value > 9999999 && value <= 99999999) {
            price = String.format("$%s, %s, %s", precio.substring(0,1), precio.substring(2,4),
                    precio.substring(5, precio.length()));
        }

        return price;
    }

    public void querySearchDpto(){
        ParseQuery<ParseObject> dpto = ParseQuery.getQuery("Departamentos");

        if(app.rooms != 0){
            dpto.whereGreaterThanOrEqualTo("", app.rooms);
        }

        if(!app.trans.equalsIgnoreCase("B")){
            dpto.whereEqualTo("", app.trans);
        }

        dpto.whereGreaterThanOrEqualTo("", app.minprice);
        dpto.whereLessThanOrEqualTo("", app.maxprice);

        dpto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null){
                    if(parseObjects.size() != 0){
                        dptos.clear();

                        for(ParseObject obj : parseObjects){
                            dptos.add(obj);
                        }


                    }
                } else {
                    Log.e("", e.toString());
                }
            }
        });
    }
}
