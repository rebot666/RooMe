package com.rebot.roomme.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.rebot.roomme.R;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

import java.util.ArrayList;

/**
 * Created by Strike on 7/15/14.
 */
public class CountryAdapter extends AbstractWheelTextAdapter {
    // Countries names
    private ArrayList<String> countries;
    /**
     * Constructor
     */
    public CountryAdapter(Context context, ArrayList<String> data) {
        super(context, R.layout.country_layout, NO_RESOURCE);
        setItemTextResource(R.id.country_name);
        countries = data;
    }

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        View view = super.getItem(index, cachedView, parent);
        /*ImageView img = (ImageView) view.findViewById(R.id.flag);
        img.setImageResource(flags[index]);*/
        return view;
    }

    @Override
    public int getItemsCount() {
        return countries.size();
    }

    @Override
    protected CharSequence getItemText(int index) {
        return countries.get(index);
    }
}
