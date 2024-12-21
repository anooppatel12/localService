package com.example.localservice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceDetailsActivity extends AppCompatActivity {

    private TextView serviceNameDetail, serviceDescriptionDetail, contactDetailsDetail, averageRatingTextView;
    private ImageView serviceImage;
    private Button leaveReviewButton, whatsappButton, callButton;
    private RecyclerView reviewsRecyclerView;
    private ReviewsAdapter reviewsAdapter;
    private List<Review> reviewList;

    private DatabaseReference serviceDatabase, reviewsDatabase;

    private String serviceId;
    private String userRole; // Role passed via intent
    private static final String TAG = "ServiceDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        // Initialize views
        serviceNameDetail = findViewById(R.id.service_name_detail);
        serviceDescriptionDetail = findViewById(R.id.service_description_detail);
        contactDetailsDetail = findViewById(R.id.contact_details_detail);
        averageRatingTextView = findViewById(R.id.average_rating_text_view);
        serviceImage = findViewById(R.id.service_image);
        leaveReviewButton = findViewById(R.id.leave_review_button);
        reviewsRecyclerView = findViewById(R.id.reviews_recycler_view);
        whatsappButton = findViewById(R.id.whatsapp_button);  // WhatsApp button
        callButton = findViewById(R.id.call_button);  // Call button

        // Setup RecyclerView
        reviewList = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(reviewList);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecyclerView.setAdapter(reviewsAdapter);

        // Get service ID and role from intent
        serviceId = getIntent().getStringExtra("service_id");
        userRole = getIntent().getStringExtra("user_role");

        if (serviceId == null || serviceId.isEmpty()) {
            Toast.makeText(this, "Invalid Service ID!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Show leave review button only for "customer"
        leaveReviewButton.setVisibility(View.VISIBLE);

        // Set up leave review button
        leaveReviewButton.setOnClickListener(v -> openReviewDialog());

        // Initialize Firebase references
        serviceDatabase = FirebaseDatabase.getInstance().getReference("services").child(serviceId);
        reviewsDatabase = FirebaseDatabase.getInstance().getReference("reviews").child(serviceId);

        // Load service details and reviews
        loadServiceDetails();
        loadReviews();

        // WhatsApp Button functionality
        whatsappButton.setOnClickListener(v -> {
            String phoneNumber = contactDetailsDetail.getText().toString().trim();
            String message = "Hello, I am interested in your service.";
            openWhatsApp(phoneNumber, message);
        });

        // Call Button functionality
        callButton.setOnClickListener(v -> {
            String phoneNumber = contactDetailsDetail.getText().toString().trim();
            makeCall(phoneNumber);
        });
    }

    private void loadServiceDetails() {
        serviceDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Service service = snapshot.getValue(Service.class);
                    if (service != null) {
                        serviceNameDetail.setText(service.getName());
                        serviceDescriptionDetail.setText(service.getDescription());
                        contactDetailsDetail.setText(service.getContactDetails());

                        if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
                            Picasso.get().load(service.getImageUrl()).into(serviceImage);
                        } else {
                            serviceImage.setImageResource(R.drawable.default_image);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load service details: " + error.getMessage());
            }
        });
    }

    private void loadReviews() {
        reviewsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                float totalRating = 0;
                int count = 0;

                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    if (review != null) {
                        reviewList.add(review);
                        totalRating += review.getRating();
                        count++;
                    }
                }

                float averageRating = count > 0 ? totalRating / count : 0;
                averageRatingTextView.setText(String.format("Rating: %.1f/5", averageRating));

                reviewsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load reviews: " + error.getMessage());
            }
        });
    }

    private void openReviewDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_review, null);
        RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        TextView reviewText = dialogView.findViewById(R.id.review_text);

        new AlertDialog.Builder(this)
                .setTitle("Leave a Review")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    float rating = ratingBar.getRating();
                    String review = reviewText.getText().toString().trim();

                    if (rating > 0) {
                        saveReview(rating, review);
                    } else {
                        Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveReview(float rating, String reviewText) {
        String reviewId = reviewsDatabase.push().getKey();
        Review review = new Review(
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                serviceId,
                reviewText,
                rating,
                new Date().getTime()
        );

        reviewsDatabase.child(reviewId).setValue(review)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Review submitted!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to submit review", Toast.LENGTH_SHORT).show());
    }

    // Function to open WhatsApp chat
    // Function to open WhatsApp chat
    private void openWhatsApp(String phoneNumber, String message) {
        String url = "https://wa.me/" + phoneNumber + "?text=" + Uri.encode(message);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://com.whatsapp"));

        if (isAppInstalled("com.whatsapp")) {
            startActivity(intent);
        } else {
            // WhatsApp not installed, show fallback
            Toast.makeText(this, "WhatsApp is not installed, opening in browser", Toast.LENGTH_SHORT).show();
            openInBrowser(url); // Open in browser instead
        }
    }

    // Function to open WhatsApp link in a browser if app is not installed
    private void openInBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    // Function to check if WhatsApp is installed
    private boolean isAppInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    // Function to make a call
    private void makeCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}
