package com.example.app100;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Menu _menu;
    private boolean startedNewGame = false;
    private String documentId;
    private GoogleMap mMap;
    List<MarkerOptions> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                List<LatLng> latLngList = getIntent().getParcelableArrayListExtra("latLngList");
                if (latLngList != null && latLngList.size() > 1) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLngList.get(0));
                    googleMap.animateCamera(cameraUpdate);
                    Log.d(TAG, "latLngList: " + latLngList.toString());
                    for (LatLng latLng: latLngList) {
                        addMarker(latLng);
                    }
                    String url = DirectionsApiUrlBuilder.buildUrl(latLngList);
                    new AsyncDirectionsApiTask(MainActivity.this, url).execute();

                    Log.d(TAG, "URL: " + url);
                }
                else {
                    Log.d(TAG, "Lista pusta");
                }
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        if (startedNewGame) {
                            markers.add(new MarkerOptions().position(latLng));
                            addMarker(latLng);
                        }
                    }
                });
            }
        });

    }
    public void addMarker(LatLng latLng) {
        MarkerOptions marker = new MarkerOptions().position(latLng);
        mMap.addMarker(marker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        _menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        MenuItem saveRoadItem = menu.findItem(R.id.save_road);
        saveRoadItem.setEnabled(false);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                if (!startedNewGame) {
                    startedNewGame = true;
                    MenuItem saveRoadItem = _menu.findItem(R.id.save_road);
                    saveRoadItem.setEnabled(true);
                }
                if(startedNewGame) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Enter new road name");

                    final EditText input = new EditText(this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Pobierz wprowadzone przez użytkownika ID dokumentu
                            documentId = input.getText().toString();
                            // Tutaj możesz już wykonać odpowiednie operacje na bazie Firestore, używając wprowadzonego ID
                        }
                    });
                    builder.setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startedNewGame = false;
                            MenuItem saveRoadItem = _menu.findItem(R.id.save_road);
                            saveRoadItem.setEnabled(false);
                            dialog.cancel();
                        }
                    });

                    builder.show();

                    MenuItem saveRoadItem = _menu.findItem(R.id.save_road);
                    saveRoadItem.setEnabled(true);
                    ClearMarkers();
                }

                return true;
            case R.id.show_roades:
                Intent intent = new Intent(MainActivity.this, NewActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.save_road:
                startedNewGame = false;
                SaveRoad();
                MenuItem saveRoadItem = _menu.findItem(R.id.save_road);
                saveRoadItem.setEnabled(false);
                ClearMarkers();
                return true;
            case R.id.help:
                return true;
            case R.id.show_markers_info:
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("markers", new ArrayList<>(markers));
                Intent intent_marker = new Intent(MainActivity.this, MarkersActivity.class);
                intent_marker.putExtras(bundle);
                startActivity(intent_marker);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ClearMarkers() {
        markers.clear();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.clear();
            }
        });
    }
    public void processJsonData(String result) {
        Log.d(TAG, "PROCES_JSON_DATA_STRING: " + result);
        Gson gson = new Gson();
        DirectionsResponse directionsResponse = gson.fromJson(result, DirectionsResponse.class);
        List<LatLng> latLngList = new ArrayList<>();
        for(Step step: directionsResponse.getRoutes().getLegs().get(0).getSteps()) {
            // latLngList.add(directionResponse.getStartLocation());
            // latLngList.add(directionResponse.getEndLocation());
        }

    }

    private void SaveRoad() {
        // Pobierz referencję do kolekcji "marker_lists" w bazie Firestore
        CollectionReference markerListsRef = FirebaseFirestore.getInstance().collection("marker_lists");

        // Utwórz mapę, w której kluczem będzie nazwa listy, a wartością - lista znaczników
        Map<String, Object> markerLists = new HashMap<>();
        markerLists.put("Name", documentId);
        markerLists.put("Markers", markers);

        // Zapisz mapę do kolekcji "marker_lists" w bazie Firestore
        markerListsRef.add(markerLists)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Zapis zakończył się sukcesem
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Zapis nie powiódł się
                    }
                });
    }

}