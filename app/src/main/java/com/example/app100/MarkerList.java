package com.example.app100;

import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.List;

public class MarkerList implements Serializable {
    private String Name;
    private List<MarkerOptions> Markers;


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public List<MarkerOptions> getMarkers() {
        return Markers;
    }

    public void setMarkers(List<MarkerOptions> markers) {
        this.Markers = markers;
    }
}