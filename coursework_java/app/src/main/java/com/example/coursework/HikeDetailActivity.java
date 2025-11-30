package com.example.coursework;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class HikeDetailActivity extends AppCompatActivity {

    private TextView tvDetailName, tvDetailDifficulty, tvDetailLocation,
            tvDetailDate, tvDetailParking, tvDetailDistance, tvDetailDuration,
            tvDetailElevation, tvDetailGroupSize, tvDetailTerrain, tvDetailDescription;

    private HikeDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_detail);

        dbHelper = new HikeDbHelper(this);

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailDifficulty = findViewById(R.id.tvDetailDifficulty);
        tvDetailLocation = findViewById(R.id.tvDetailLocation);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailParking = findViewById(R.id.tvDetailParking);
        tvDetailDistance = findViewById(R.id.tvDetailDistance);
        tvDetailDuration = findViewById(R.id.tvDetailDuration);
        tvDetailElevation = findViewById(R.id.tvDetailElevation);
        tvDetailGroupSize = findViewById(R.id.tvDetailGroupSize);
        tvDetailTerrain = findViewById(R.id.tvDetailTerrain);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);

        long id = getIntent().getLongExtra("id", -1);
        if (id == -1) {
            finish();
            return;
        }

        Hike h = dbHelper.getHike(id);
        if (h == null) {
            finish();
            return;
        }

        setTitle("Hike details");

        tvDetailName.setText(h.getName());
        tvDetailDifficulty.setText(h.getDifficulty());
        tvDetailLocation.setText("Location: " + h.getLocation());
        tvDetailDate.setText("Date: " + h.getDate());
        tvDetailParking.setText("Parking: " + (h.hasParking() ? "Yes" : "No"));
        tvDetailDistance.setText("Distance: " + h.getDistanceKm() + " km");
        tvDetailDuration.setText("Duration: " + h.getDurationHours() + " h");
        tvDetailElevation.setText("Elevation: " + h.getElevationM() + " m");
        tvDetailGroupSize.setText("Group size: " + h.getGroupSize());
        tvDetailTerrain.setText("Terrain: " + (h.getTerrain() == null ? "" : h.getTerrain()));
        tvDetailDescription.setText("Notes: " + (h.getDescription() == null ? "" : h.getDescription()));
    }
}
