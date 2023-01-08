package com.example.app100;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MarkersActivity extends AppCompatActivity {

    ArrayList<MarkerOptions> markers = new ArrayList<>();
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        markers = bundle.getParcelableArrayList("markers");

        setContentView(R.layout.activity_markers);
        ListView listView = findViewById(R.id.list_view);

        List<String> latLngStrList = new ArrayList<>();
        for (MarkerOptions marker : markers) {
            double lat = marker.getPosition().latitude;
            double lng = marker.getPosition().longitude;
            latLngStrList.add(String.format("%.6f, %.6f", lat, lng));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                latLngStrList
                );

        listView.setAdapter(adapter);
    }
}