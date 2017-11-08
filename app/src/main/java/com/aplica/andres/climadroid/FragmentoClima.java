package com.aplica.andres.climadroid;

/**
 * Created by andres on 07/11/2017.
 */

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;



public class FragmentoClima extends Fragment {
    Typeface weatherFont;

    TextView ciudad;
    TextView ulactualizacion;
    TextView detalles;
    TextView temperatura;
    TextView icono;

    Handler handler;

    public FragmentoClima(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.clima_fragmento, container, false);
        ciudad= (TextView)rootView.findViewById(R.id.ciudad);
        ulactualizacion = (TextView)rootView.findViewById(R.id.ulactualizacion);
        detalles = (TextView)rootView.findViewById(R.id.detalles);
        temperatura = (TextView)rootView.findViewById(R.id.temperatura);
        icono = (TextView)rootView.findViewById(R.id.icono);

        icono.setTypeface(weatherFont);
        return rootView;
    }


    //@Override
    /*public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        ///weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "weather.ttf");
        updateWeatherData(new CiudadPreferida(getActivity()).getCity());
    }*/


    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = Conexion.getJSON(getActivity(), city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.no_encontrado),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }


    private void renderWeather(JSONObject json){

        try {

            ciudad.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);

            JSONObject windo = json.getJSONObject("wind");

            int    numerod=windo.getInt("deg");
            String direc="";
            if(numerod==0)
            {
                direc="NORTE";
            }else if(numerod>0&&numerod<90){
                direc="NORESTE";
            }else if(numerod==90){
                direc="ESTE";
            }else if(numerod>90&&numerod<180){
                direc="SURESTE";
            }else if(numerod==180){
                direc="SUR";
            }else if(numerod>180&&numerod<270){
                direc="SUROESTE";
            }else if(numerod==270){
                direc="OESTE";
            }else if(numerod>270&&numerod<360){
                direc="NOROESTE";
            }
            JSONObject main = json.getJSONObject("main");
            JSONObject wind = json.getJSONObject("wind");
            detalles.setText(
                    details.getString("description").toUpperCase(Locale.US) +

                            "\n" + "Humedad: " + main.getString("humidity") + "%" +
                            "\n" + "Presión: " + main.getString("pressure") + " hPa"+
                            "\n" + "Velocidad Viento: " + wind.getString("speed") + "m/s"+
                            "\n" + "direccion viento: "+ wind.getString("deg") +"°" +
                            "\n"+ direc);

            temperatura.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            ulactualizacion.setText("Ult.información: " + updatedOn);


        }catch(Exception e){
            Log.e("Error", "Uno o más datos no fueron encontrados en los datos JSON");
        }
    }


    public void cambiarCiudad(String ciudad){

        updateWeatherData(ciudad);
    }

}