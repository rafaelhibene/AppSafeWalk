package com.rafaelhibene.safewalk.helpers;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DirectionHelper {

    public static void drawRoute(GoogleMap map, LatLng origin, LatLng destination, String apiKey) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&key=" + apiKey;

        new FetchRouteTask(map).execute(url);
    }

    private static class FetchRouteTask extends AsyncTask<String, Void, ArrayList<LatLng>> {
        GoogleMap mMap;

        public FetchRouteTask(GoogleMap map) {
            this.mMap = map;
        }

        @Override
        protected ArrayList<LatLng> doInBackground(String... urls) {
            ArrayList<LatLng> points = new ArrayList<>();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    json.append(line);
                }

                JSONObject response = new JSONObject(json.toString());
                JSONArray routes = response.getJSONArray("routes");
                if (routes.length() == 0) return points;

                JSONObject overviewPolyline = routes.getJSONObject(0).getJSONObject("overview_polyline");
                String encodedString = overviewPolyline.getString("points");
                points = decodePolyline(encodedString);

            } catch (Exception e) {
                Log.e("DirectionHelper", "Erro ao buscar rota", e);
            }
            return points;
        }

        @Override
        protected void onPostExecute(ArrayList<LatLng> points) {
            if (points != null && !points.isEmpty()) {
                mMap.addPolyline(new PolylineOptions().addAll(points).width(10).color(0xFF2196F3));
            }
        }

        // Decodifica a string compactada de polylines do Google
        private ArrayList<LatLng> decodePolyline(String encoded) {
            ArrayList<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng(lat / 1E5, lng / 1E5);
                poly.add(p);
            }

            return poly;
        }
    }
}
