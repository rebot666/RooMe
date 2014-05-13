package com.rebot.roomme.Models;

import com.parse.ParseUser;

import java.util.Comparator;

/**
 * Created by Strike on 5/2/14.
 */
public class Users implements Comparator<Users>, Comparable<Users>{

    private ParseUser user;
    private double percentage;
    public Users(ParseUser u, double p) {
        user = u;
        percentage = p;
    }

    public ParseUser getUser() {
        return user;
    }

    public void setUser(ParseUser user) {
        this.user = user;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }


    @Override
    public int compare(Users o1, Users o2) {
        if(o1.getPercentage() > o2.getPercentage()){
            return 1;
        }else if(o1.getPercentage() > o2.getPercentage()){
            return -1;
        }else{
            return 0;
        }
    }

    @Override
    public int compareTo(Users another) {
        Double anotherObject = new Double(another.getPercentage());
        Double thisObject = new Double(getPercentage());

        return anotherObject.compareTo(thisObject);
    }
}
