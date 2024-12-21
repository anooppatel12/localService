package com.example.localservice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.util.Log;

public class ServiceProviderActivity extends AppCompatActivity {
    private EditText serviceName, serviceDescription, contactDetails;
    private Button submitServiceButton, uploadImageButton;
    private Uri imageUri;
    private DatabaseReference serviceDatabase;
    private ImageView imagePreview; // ImageView to show the selected image preview

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider);

        // Initialize views
        serviceName = findViewById(R.id.service_name);
        serviceDescription = findViewById(R.id.service_description);
        contactDetails = findViewById(R.id.contact_details);
        submitServiceButton = findViewById(R.id.submit_service_button);
        uploadImageButton = findViewById(R.id.upload_image_button);
        imagePreview = findViewById(R.id.selected_image_view); // Initialize ImageView

        Button viewServicesButton = findViewById(R.id.view_services_button);
        viewServicesButton.setOnClickListener(v -> {
            Intent intent = new Intent(ServiceProviderActivity.this, MyServicesActivity.class);
            startActivity(intent);
        });


        // Initialize Firebase database reference
        serviceDatabase = FirebaseDatabase.getInstance().getReference("services");

        // Set the click listener for the submit button
        submitServiceButton.setOnClickListener(v -> submitService());

        // Set the click listener for the upload image button
        uploadImageButton.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    // Handle the image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();

            // Load the image into the ImageView (preview)
            imagePreview.setImageURI(imageUri);
        }
    }

    private void submitService() {
        String name = serviceName.getText().toString().trim();
        String description = serviceDescription.getText().toString().trim();
        String contact = contactDetails.getText().toString().trim();

        // Check if all fields are filled
        if (!name.isEmpty() && !description.isEmpty() && !contact.isEmpty()) {
            // Generate a unique service ID
            String serviceId = serviceDatabase.push().getKey();

            // Check if image is selected
            if (imageUri != null) {
                uploadImageAndSaveService(name, description, contact, serviceId);
            } else {
                // Create a new Service object with no image URL
                Service service = new Service(serviceId, name, description, contact, "");
                saveServiceToDatabase(serviceId, service);
            }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageAndSaveService(String name, String description, String contact, String serviceId) {
        if (imageUri != null) {
            // Upload the image to Cloudinary using the Uri
            MediaManager.get().upload(imageUri) // Use the Uri directly
                    .option("upload_preset", "localservice") // Replace with your preset name
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Log.d("Upload", "Upload started...");
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            double progress = (double) bytes / totalBytes;
                            Log.d("Upload", "Progress: " + (progress * 100) + "%");
                        }

                        @Override
                        public void onSuccess(String requestId, java.util.Map resultData) {
                            String imageUrl = resultData.get("secure_url").toString();
                            Log.d("Upload", "Upload successful: " + imageUrl);

                            // Create the Service object with the image URL
                            Service service = new Service(serviceId, name, description, contact, imageUrl);
                            saveServiceToDatabase(serviceId, service);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Log.e("UploadError", "Error: " + error.getDescription());
                            Toast.makeText(ServiceProviderActivity.this, "Image upload failed: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            Log.e("UploadError", "Upload rescheduled: " + error.getDescription());
                        }
                    }).dispatch();
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveServiceToDatabase(String serviceId, Service service) {
        String providerId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get logged-in user's UID
        service.setProviderId(providerId); // Set providerId in the Service object

        // Save service data to Firebase
        serviceDatabase.child(serviceId).setValue(service)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ServiceProvider", "Service added successfully");
                        Toast.makeText(ServiceProviderActivity.this, "Service added successfully", Toast.LENGTH_SHORT).show();
                        clearFields(); // Clear the input fields after successful submission
                    } else {
                        Log.e("ServiceProvider", "Failed to add service", task.getException());
                        Toast.makeText(ServiceProviderActivity.this, "Failed to add service", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        // Clear input fields after submitting
        serviceName.setText("");
        serviceDescription.setText("");
        contactDetails.setText("");
    }

    // Utility method to get the real path from URI (if needed)
    private String getRealPathFromURI(Uri contentUri) {
        // Implement logic to get real file path from URI
        // Can use content resolver or other methods based on Android version
        return contentUri.getPath();  // Example, adapt as needed
    }


}