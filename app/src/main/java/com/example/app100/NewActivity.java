package com.example.app100;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;

public class NewActivity extends AppCompatActivity {
    ListView listView;
    int test;
    MarkerList actual_markers;
    private MarkerListener listener;

    public void setListener(MarkerListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        listView=(ListView)findViewById(R.id.list_view);
        ArrayList<String> arrayList=new ArrayList<>();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                String name = arrayList.get(i);
                                                db.collection("marker_lists")
                                                        .whereEqualTo("Name", name)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                                        MarkerList marker = documentSnapshot.toObject(MarkerList.class);
                                                                        actual_markers = marker;
                                                                        Log.d(TAG, "Marker: " + actual_markers.getName());

                                                                        finish();
                                                                    }
                                                                }
                                                                else {
                                                                    Log.d(TAG, "Error getiting documents: ",
                                                                            task.getException());
                                                                }
                                                            }
                                                        });
                                            }
                                        });

        db.collection("marker_lists").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot collections = task.getResult();
                            for (QueryDocumentSnapshot collection : collections) {

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
                }
        );
        if(actual_markers != null) {
            }


    }


}
