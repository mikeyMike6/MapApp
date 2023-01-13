package com.example.app100;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DirectionsResponse {
    public List<LatLng> latLngList;
    public DirectionsResponse() {
        latLngList = new ArrayList<>();
    }
}
