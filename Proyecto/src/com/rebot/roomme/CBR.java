package com.rebot.roomme;
import com.parse.ParseException;
import com.parse.ParseObject;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Strike on 5/1/14.
 */
public class CBR {
    private static final double CUMPLEANOS = 10;
    private static final double GENERO = 7;
    private static final double ESTADO = 7;
    private static final double PELICULAS = 5;
    private static final double LIBROS = 5;
    private static final double MUSICA = 5;
    private static final double PELICULAS_NO = 7;
    private static final double LIBROS_NO = 7;
    private static final double MUSICA_NO = 7;
    private static final double PUNTOS = GENERO + ESTADO + PELICULAS
            + LIBROS + MUSICA + CUMPLEANOS + PELICULAS_NO + LIBROS_NO + MUSICA_NO;

    public static double calculaCBR(ParseObject current, ParseObject other) {
        double sumatoria = 0.0;

        JSONObject currentJson = current.getJSONObject("profile");
        JSONObject otherJson = other.getJSONObject("profile");

        try {
            if(currentJson.optString("gender") != null && otherJson.optString("gender") != null){
                sumatoria += genero(currentJson.getString("gender"), otherJson.getString("gender"));
            } else {
                //TODO NO TOMAR EN CUENTA EL PESO DEL GENERO
                sumatoria += 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<Object> currentList = current.getList("books");
        List<Object> otherList = other.getList("books");

        sumatoria += libros(currentList.toArray(new String[currentList.size()]),
                otherList.toArray(new String[otherList.size()]));

        sumatoria += librosNo(currentList.toArray(new String[currentList.size()]),
                otherList.toArray(new String[otherList.size()]));

        currentList = current.getList("movies");
        otherList = other.getList("movies");

        sumatoria += peliculas(currentList.toArray(new String[currentList.size()]),
                otherList.toArray(new String[otherList.size()]));

        sumatoria += peliculasNo(currentList.toArray(new String[currentList.size()]),
                otherList.toArray(new String[otherList.size()]));

        currentList = current.getList("music");
        otherList = other.getList("music");

        sumatoria += musica(currentList.toArray(new String[currentList.size()]),
                otherList.toArray(new String[otherList.size()]));

        sumatoria += musicaNo(currentList.toArray(new String[currentList.size()]),
                otherList.toArray(new String[otherList.size()]));

        try {
            if(!currentJson.optString("location", "").equalsIgnoreCase("") &&
                    !otherJson.optString("location", "").equalsIgnoreCase("")){
                sumatoria += estado(currentJson.getString("location"), otherJson.getString("location"));
            } else {
                //TODO NO TOMAR EN CUENTA EL PESO DE LA LOCALIDAD
                sumatoria += 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if(!currentJson.optString("birthday", "").equalsIgnoreCase("") &&
                    !otherJson.optString("birthday", "").equalsIgnoreCase("")){
                sumatoria += cumpleanos(currentJson.getString("birthday"), otherJson.getString("birthday"));
            } else {
                //TODO NO TOMAR EN CUENTA EL PESO DEL CUMPLEAÑOS
                sumatoria += 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return regla3(PUNTOS, sumatoria);
    }

    public static double genero(String current, String other){
        if(current.equalsIgnoreCase(other)){
            return 1 * GENERO;
        }
        return 0;
    }

    public static double libros(String[] current, String[] other){
        String concatena = "";
        int contador = 0;

        if(current.length > other.length){
            for (int i = 0; i < current.length; i++) {
                concatena += current[i];
            }

            for (int i = 0; i < other.length; i++) {
                if(concatena.contains(other[i])){
                    contador += 1;
                }
            }
            return (contador/current.length) * LIBROS;
        } else {
            for (int i = 0; i < other.length; i++) {
                concatena += other[i];
            }

            for (int i = 0; i < current.length; i++) {
                if(concatena.contains(current[i])){
                    contador += 1;
                }
            }

            if(contador == 0){
                return 0;
            } else {
                return (contador/other.length) * LIBROS;
            }
        }
    }

    public static double peliculas(String[] current, String[] other){
        String concatena = "";
        int contador = 0;

        if(current.length > other.length){
            for (int i = 0; i < current.length; i++) {
                concatena += current[i];
            }

            for (int i = 0; i < other.length; i++) {
                if(concatena.contains(other[i])){
                    contador += 1;
                }
            }
            return (contador/current.length) * PELICULAS;
        } else {
            for (int i = 0; i < other.length; i++) {
                concatena += other[i];
            }

            for (int i = 0; i < current.length; i++) {
                if(concatena.contains(current[i])){
                    contador += 1;
                }
            }

            if(contador == 0){
                return 0;
            } else {
                return (contador/other.length) * PELICULAS;
            }
        }
    }

    public static double musica(String[] current, String[] other){
        String concatena = "";
        int contador = 0;

        if(current.length > other.length){
            for (int i = 0; i < current.length; i++) {
                concatena += current[i];
            }

            for (int i = 0; i < other.length; i++) {
                if(concatena.contains(other[i])){
                    contador += 1;
                }
            }
            return (contador/current.length) * MUSICA;
        } else {
            for (int i = 0; i < other.length; i++) {
                concatena += other[i];
            }

            for (int i = 0; i < current.length; i++) {
                if(concatena.contains(current[i])){
                    contador += 1;
                }
            }
            if(contador == 0){
                return 0;
            } else {
                return (contador/other.length) * MUSICA;
            }

        }
    }

    public static double estado(String current, String other){
        if(current.contains(other)){
            return 1 * ESTADO;
        }

        if(other.contains(current)){
            return 1 * ESTADO;
        }

        return 0;
    }

    public static double cumpleanos(String current, String other){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date actual = new Date();

        try {
            Date birthdayCurrent = formatter.parse(current);
            Date birthdayOther = formatter.parse(other);

            Calendar cal = Calendar.getInstance();
            cal.setTime(birthdayCurrent);
            int yearCurrent = cal.get(Calendar.YEAR);
            cal.setTime(birthdayOther);
            int yearOther = cal.get(Calendar.YEAR);
            cal.setTime(actual);
            int yearActual = cal.get(Calendar.YEAR);

            double currentBirthday = yearActual-yearCurrent;
            double otherBirthday = yearActual-yearOther;


            if(currentBirthday > otherBirthday){
                return (otherBirthday / currentBirthday) * CUMPLEANOS;
            }else if(otherBirthday > currentBirthday){
                return (currentBirthday / otherBirthday) * CUMPLEANOS;
            }else{
                return CUMPLEANOS;
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double peliculasNo(String[] current, String[] other){
        if(current.length > other.length){
            return (other.length / current.length) * PELICULAS_NO;
        }else if(other.length > current.length){
            return (current.length / other.length) * PELICULAS_NO;
        }else{
            return PELICULAS_NO;
        }
    }

    public static double librosNo(String[] current, String[] other){
        if(current.length > other.length){
            return (other.length / current.length) * LIBROS_NO;
        }else if(other.length > current.length){
            return (current.length / other.length) * LIBROS_NO;
        }else{
            return LIBROS_NO;
        }
    }

    public static double musicaNo(String[] current, String[] other){
        if(current.length > other.length){
            return (other.length / current.length) * PELICULAS_NO;
        }else if(other.length > current.length){
            return (current.length / other.length) * PELICULAS_NO;
        }else{
            return PELICULAS_NO;
        }
    }


    public static double regla3(double cachichen, double valorObtener){
        return (valorObtener * 100) / cachichen;
    }
}
