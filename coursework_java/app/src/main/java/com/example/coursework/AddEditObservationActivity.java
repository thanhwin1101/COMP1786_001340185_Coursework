package com.example.coursework;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditObservationActivity extends AppCompatActivity {

    private EditText etObsTitle, etObsTime, etObsComment;
    private Button btnSaveObs, btnCancelObs;
    private TextView tvObsFormTitle;

    private HikeDbHelper dbHelper;
    private boolean isEditMode = false;
    private long hikeId = -1;
    private long obsId = -1;

    // Calendar lưu thời điểm đang chọn
    private Calendar obsCalendar;
    private final SimpleDateFormat sdf =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_observation);

        dbHelper = new HikeDbHelper(this);

        etObsTitle = findViewById(R.id.etObsTitleInput);
        etObsTime = findViewById(R.id.etObsTimeInput);
        etObsComment = findViewById(R.id.etObsCommentInput);
        btnSaveObs = findViewById(R.id.btnSaveObs);
        btnCancelObs = findViewById(R.id.btnCancelObs);
        tvObsFormTitle = findViewById(R.id.tvObsFormTitle);

        obsCalendar = Calendar.getInstance();

        String mode = getIntent().getStringExtra("mode");
        isEditMode = "edit".equals(mode);

        if (isEditMode) {
            obsId = getIntent().getLongExtra("obsId", -1);
            tvObsFormTitle.setText("Edit Observation");
            btnSaveObs.setText("Save Changes");
            loadExistingObservation();
        } else {
            hikeId = getIntent().getLongExtra("hikeId", -1);
            tvObsFormTitle.setText("Add Observation");
            btnSaveObs.setText("Add");
            // default time = now
            etObsTime.setText(getNowString());
        }

        // Không cho gõ bàn phím, chỉ bấm để chọn ngày giờ
        etObsTime.setInputType(InputType.TYPE_NULL);
        etObsTime.setOnClickListener(v -> showDateTimePicker());

        btnSaveObs.setOnClickListener(v -> saveObservation());
        btnCancelObs.setOnClickListener(v -> finish());
    }

    private String getNowString() {
        return sdf.format(new Date());
    }

    private void loadExistingObservation() {
        if (obsId == -1) return;
        Observation o = dbHelper.getObservation(obsId);
        if (o == null) return;

        hikeId = o.getHikeId();
        etObsTitle.setText(o.getTitle());
        etObsTime.setText(o.getTime());
        etObsComment.setText(o.getComment());

        // cố gắng parse time hiện có vào Calendar để DatePicker/TimePicker mở đúng
        try {
            Date parsed = sdf.parse(o.getTime());
            if (parsed != null) {
                obsCalendar.setTime(parsed);
            }
        } catch (ParseException ignored) {
        }
    }

    private void showDateTimePicker() {
        // B1: chọn ngày
        int y = obsCalendar.get(Calendar.YEAR);
        int m = obsCalendar.get(Calendar.MONTH);
        int d = obsCalendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            obsCalendar.set(Calendar.YEAR, year);
            obsCalendar.set(Calendar.MONTH, month);
            obsCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // B2: sau khi có ngày thì chọn giờ
            int h = obsCalendar.get(Calendar.HOUR_OF_DAY);
            int min = obsCalendar.get(Calendar.MINUTE);

            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                obsCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                obsCalendar.set(Calendar.MINUTE, minute);
                etObsTime.setText(sdf.format(obsCalendar.getTime()));
            }, h, min, true).show();

        }, y, m, d).show();
    }

    private void saveObservation() {
        String title = etObsTitle.getText().toString().trim();
        String time = etObsTime.getText().toString().trim();
        String comment = etObsComment.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Observation title is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(time)) {
            time = getNowString();
        }

        if (!isEditMode) {
            Observation obs = new Observation(hikeId, title, time, comment);
            long id = dbHelper.insertObservation(obs);
            if (id > 0) {
                Toast.makeText(this, "Observation added", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error saving observation", Toast.LENGTH_SHORT).show();
            }
        } else {
            Observation obs = new Observation(obsId, hikeId, title, time, comment);
            int rows = dbHelper.updateObservation(obs);
            if (rows > 0) {
                Toast.makeText(this, "Observation updated", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error updating observation", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
