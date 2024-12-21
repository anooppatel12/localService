package com.example.localservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button serviceProviderButton = findViewById(R.id.service_provider_button);
        Button serviceTakerButton = findViewById(R.id.service_taker_button);

        serviceProviderButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ServiceProviderActivity.class);
            startActivity(intent);
        });

        serviceTakerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ServiceTakerActivity.class);
            startActivity(intent);
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

}