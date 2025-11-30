package com.example.coursework;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.coursework.R;
import java.util.Calendar;

public class AddEditHikeActivity extends AppCompatActivity {

    private EditText etName, etLocation, etDate, etDistance, etDuration,
            etElevation, etGroupSize, etTerrain, etDescription;
    private Spinner spDifficulty;
    private Switch swParking;
    private Button btnSave, btnCancel;
    private TextView tvFormTitle;

    private HikeDbHelper dbHelper;
    private long editingId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_hike);

        dbHelper = new HikeDbHelper(this);

        tvFormTitle = findViewById(R.id.tvFormTitle);
        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etDistance = findViewById(R.id.etDistance);
        etDuration = findViewById(R.id.etDuration);
        etElevation = findViewById(R.id.etElevation);
        etGroupSize = findViewById(R.id.etGroupSize);
        etTerrain = findViewById(R.id.etTerrain);
        etDescription = findViewById(R.id.etDescription);
        spDifficulty = findViewById(R.id.spDifficulty);
        swParking = findViewById(R.id.swParking);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        String[] diffs = {"Easy", "Moderate", "Hard", "Expert"};
        ArrayAdapter<String> diffAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, diffs);
        diffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDifficulty.setAdapter(diffAdapter);

        etDate.setOnClickListener(v -> showDatePicker());

        String mode = getIntent().getStringExtra("mode");
        isEditMode = "edit".equals(mode);
        if (isEditMode) {
            editingId = getIntent().getLongExtra("id", -1);
            tvFormTitle.setText("Edit Hike");
            btnSave.setText("Save Changes");
            loadExistingHike();
        } else {
            tvFormTitle.setText("Add New Hike");
            btnSave.setText("Add Hike");
        }

        btnSave.setOnClickListener(v -> validateAndConfirm());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String text = (month + 1) + "/" + dayOfMonth + "/" + year;
            etDate.setText(text);
        }, y, m, d).show();
    }

    private void loadExistingHike() {
        if (editingId == -1) return;
        Hike h = dbHelper.getHike(editingId);
        if (h == null) return;

        etName.setText(h.getName());
        etLocation.setText(h.getLocation());
        etDate.setText(h.getDate());
        etDistance.setText(String.valueOf(h.getDistanceKm()));
        etDuration.setText(String.valueOf(h.getDurationHours()));
        etElevation.setText(String.valueOf(h.getElevationM()));
        etGroupSize.setText(String.valueOf(h.getGroupSize()));
        etTerrain.setText(h.getTerrain());
        etDescription.setText(h.getDescription());

        String diff = h.getDifficulty();
        ArrayAdapter adapter = (ArrayAdapter) spDifficulty.getAdapter();
        int pos = adapter.getPosition(diff);
        if (pos >= 0) spDifficulty.setSelection(pos);

        swParking.setChecked(h.hasParking());
    }

    // validate & show confirm dialog
    private void validateAndConfirm() {
        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String distanceStr = etDistance.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();
        String elevationStr = etElevation.getText().toString().trim();
        String groupStr = etGroupSize.getText().toString().trim();
        String terrain = etTerrain.getText().toString().trim();
        String difficulty = (String) spDifficulty.getSelectedItem();
        boolean parking = swParking.isChecked();
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(location) ||
                TextUtils.isEmpty(date) ||
                TextUtils.isEmpty(distanceStr) ||
                TextUtils.isEmpty(durationStr) ||
                TextUtils.isEmpty(elevationStr) ||
                TextUtils.isEmpty(groupStr)) {

            Toast.makeText(this, "Please fill all required fields (*)", Toast.LENGTH_SHORT).show();
            return;
        }

        double distance, duration;
        int elevation, groupSize;
        try {
            distance = Double.parseDouble(distanceStr);
            duration = Double.parseDouble(durationStr);
            elevation = Integer.parseInt(elevationStr);
            groupSize = Integer.parseInt(groupStr);
        } catch (NumberFormatException ex) {
            Toast.makeText(this,
                    "Distance, duration, elevation and group size must be numbers",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // build summary
        String summary = "Name: " + name +
                "\nLocation: " + location +
                "\nDate: " + date +
                "\nDistance: " + distance + " km" +
                "\nDuration: " + duration + " h" +
                "\nElevation: " + elevation + " m" +
                "\nDifficulty: " + difficulty +
                "\nParking: " + (parking ? "Yes" : "No") +
                "\nGroup size: " + groupSize +
                "\nTerrain: " + terrain;

        new AlertDialog.Builder(this)
                .setTitle(isEditMode ? "Confirm changes" : "Confirm new hike")
                .setMessage(summary)
                .setPositiveButton("Confirm", (d, w) -> {
                    saveToDatabase(name, location, date, difficulty,
                            distance, duration, elevation,
                            parking, groupSize, terrain, description);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveToDatabase(String name, String location, String date,
                                String difficulty, double distance, double duration,
                                int elevation, boolean parking, int groupSize,
                                String terrain, String description) {

        if (!isEditMode) {
            Hike hike = new Hike(name, location, date, difficulty,
                    distance, duration, elevation, parking,
                    groupSize, terrain, description);
            long id = dbHelper.insertHike(hike);
            if (id > 0) {
                Toast.makeText(this, "Hike saved", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error saving hike", Toast.LENGTH_SHORT).show();
            }
        } else {
            Hike hike = new Hike(editingId, name, location, date, difficulty,
                    distance, duration, elevation, parking,
                    groupSize, terrain, description);
            int rows = dbHelper.updateHike(hike);
            if (rows > 0) {
                Toast.makeText(this, "Hike updated", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error updating hike", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
