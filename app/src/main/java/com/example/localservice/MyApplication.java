package com.example.localservice;

import android.app.Application;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Cloudinary configuration
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dh00ohm41");  // Replace with your Cloudinary Cloud Name
        config.put("api_key", "738731145191813");        // Replace with your Cloudinary API Key
        config.put("api_secret", "pPdeMXAe0IpKm0UeV8_BQQNMX6s");  // Replace with your Cloudinary API Secret

        // Initialize Cloudinary
        MediaManager.init(this, config);
    }
}
