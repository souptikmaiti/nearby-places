package com.example.googlenearbyplacesdemo;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {
    GoogleMap googleMap;
    String urlstr;
    @Override
    protected String doInBackground(Object... objects) {
        googleMap = (GoogleMap) objects[0];
        urlstr = (String) objects[1];
        InputStream inputStream;
        BufferedReader bufferedReader;
        StringBuffer stringBuffer = new StringBuffer();
        String data = null;
        try{
            URL url = new URL(urlstr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line ="";
            while((line=bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }
            data = stringBuffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject root = new JSONObject(s);
            JSONArray results = root.getJSONArray("results");

            for (int i = 0; i <results.length() ; i++) {
                JSONObject current = results.getJSONObject(i);
                JSONObject location = current.getJSONObject("geometry").getJSONObject("location");
                String lat = location.getString("lat");
                String lng = location.getString("lng");
                String name = current.getString("name");
                String vicinity = current.getString("vicinity");

                MarkerOptions mo = new MarkerOptions();
                mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mo.title(name + "   " + vicinity).position(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng)));
                googleMap.addMarker(mo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
