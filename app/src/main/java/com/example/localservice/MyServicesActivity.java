package com.example.localservice;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MyServicesActivity extends AppCompatActivity {
    private RecyclerView servicesRecyclerView;
    private ArrayList<Service> serviceList;
    private ServiceAdapter adapter;
    private DatabaseReference serviceDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_services);

        // Initialize views
        servicesRecyclerView = findViewById(R.id.services_recycler_view);
        serviceList = new ArrayList<>();
        adapter = new ServiceAdapter(serviceList, this);
        servicesRecyclerView.setAdapter(adapter);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize database reference
        serviceDatabase = FirebaseDatabase.getInstance().getReference("services");

        // Load services added by the logged-in provider
        loadServices();
    }

    private void loadServices() {
        String providerId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get logged-in user's UID

        serviceDatabase.orderByChild("providerId").equalTo(providerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                serviceList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service service = snapshot.getValue(Service.class);
                    serviceList.add(service);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MyServices", "Failed to load services", databaseError.toException());
            }
        });
    }
}
