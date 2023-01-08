package com.example.app100;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public final class MapManager {
    private static MapManager instance;
    static GoogleMap mMap;
    MarkerList actual_markers;
    private MapManager(GoogleMap _mMap) {
        mMap = _mMap;
    }
    public void ClearMarkers(List<MarkerOptions> markers) {
        markers.clear();
        mMap.clear();
    }
}
