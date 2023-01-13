package com.example.app100;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class DirectionsApiUrlBuilder {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String API_KEY = "AIzaSyDIE5JHJiRYUQXCacSSlBNLTW-Cto6kJS0";

    public static String buildUrl(List<LatLng> latLngList) {
        StringBuilder urlBuilder = new StringBuilder(BASE_URL);

        double originLat = latLngList.get(0).latitude;
        double originLng = latLngList.get(0).longitude;
        double destLat =  latLngList.get(latLngList.size() - 1).latitude;
        double destLng = latLngList.get(latLngList.size() - 1).longitude;

        urlBuilder.append("origin=").append(originLat).append(",").append(originLng);
        urlBuilder.append("&destination=").append(destLat).append(",").append(destLng);

        if(latLngList != null && !latLngList.isEmpty()) {
            urlBuilder.append("&waypoints=");
            for(int i = 1; i < latLngList.size() - 1; i++) {
                urlBuilder.append(latLngList.get(i).latitude)
                        .append(",")
                        .append(latLngList.get(i + 1).longitude);
                if(i + 2 < latLngList.size()) {
                    urlBuilder.append("|");
                }
            }
        }
        urlBuilder.append("&key=").append(API_KEY);
        return urlBuilder.toString();
    }
}
