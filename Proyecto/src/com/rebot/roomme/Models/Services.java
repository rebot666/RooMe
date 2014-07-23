package com.rebot.roomme.Models;

import com.parse.ParseObject;
import com.rebot.roomme.Adapters.ServiceCheckAdapter;

/**
 * Created by Toshiba on 22/07/2014.
 */
public class Services {
    private ParseObject serviceParse;
    private boolean checked;
    private String description;

    public Services(ParseObject serviceParse){
        this.serviceParse = serviceParse;
        this.setChecked(false);
        this.setDescription("");
    }

    public ParseObject getServiceParse() {
        return serviceParse;
    }

    public void setServiceParse(ParseObject serviceParse) {
        this.serviceParse = serviceParse;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
