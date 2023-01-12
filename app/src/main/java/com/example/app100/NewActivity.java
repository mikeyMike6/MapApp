package com.example.app100;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class NewActivity extends AppCompatActivity {
    ListView listView;
    List<LatLng> latLngList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        listView=(ListView)findViewById(R.id.list_view);
        ArrayList<String> arrayList=new ArrayList<>();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            String name = arrayList.get(i);
            Log.w(TAG, "Item click:" + i);
            Log.w(TAG, "Collection:" + name);
            db.collection("marker_lists")
                    .whereEqualTo("Name", name)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                List<Map<String, Object>> markers = (List<Map<String, Object>>)
                                        documentSnapshot.get("Markers");
                                latLngList = new ArrayList<>();
                                for(Map<String, Object> markerMap : markers) {
                                    LatLng position =
                                            new LatLng(((HashMap<String, Double>) markerMap
                                                    .get("position"))
                                                    .get("latitude"),
                                                    ((HashMap<String, Double>) markerMap
                                                            .get("position"))
                                                            .get("longitude"));
                                    latLngList.add(position);
                                    Log.d(TAG,"Marker: " + position.toString());
                                }
                                Intent intent = new Intent(NewActivity.this, MainActivity.class);
                                intent.putParcelableArrayListExtra("latLngList", (ArrayList<LatLng>) latLngList);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

        });

        db.collection("marker_lists").get().addOnCompleteListener(
                task -> {
                    if(task.isSuccessful()) {
                        QuerySnapshot collections = task.getResult();
                        for (DocumentSnapshot collection : collections) {
                            arrayList.add(collection.getString("Name"));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    arrayAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } else {
                        Log.w(TAG, "Error getting collections", task.getException());
                    }
                }
        );


    }


}
