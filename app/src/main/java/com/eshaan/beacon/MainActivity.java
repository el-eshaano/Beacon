package com.eshaan.beacon;

import static com.eshaan.beacon.SettingsActivity.HEIGHT_KEY;
import static com.eshaan.beacon.SettingsActivity.WEIGHT_KEY;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    SharedPreferences sharedPreferences;
    private String userId;

    LineChart chart;
    ArrayList<HashMap<String, Object>> bmiData;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    DottedProgressBar progressBar;
    int baselineSteps = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("com.eshaan.beacon", MODE_PRIVATE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        progressBar = findViewById(R.id.DottedProgressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d("MainActivity", "User is not signed in");
            redirectTo(LoginActivity.class);
        }
        assert currentUser != null;
        userId = currentUser.getUid();

        TextView usernameTextView = findViewById(R.id.UsernameText);
        usernameTextView.setText(currentUser.getDisplayName() + "!");

        chart = findViewById(R.id.BMIChart);

        initializeUI();
        refreshData();
        styleChart();
        refreshSteps();
    }



    private void styleChart() {
        // X-axis styling and formatting
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DateAxisFormatter());
        xAxis.setAxisLineWidth(2);
        xAxis.setAxisLineColor(0xFF49454F);
        xAxis.setTextColor(0xFF49454F);
        xAxis.setTextSize(12);

        // Y-axis styling and formatting
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisLineWidth(2);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisLineColor(0xFF49454F);
        yAxis.setTextColor(0xFF49454F);

        // Disable right axis and legend
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.setBorderWidth(3);
        chart.setDescription(null);
    }

    private void refreshData() {

        DocumentReference userDoc = db.collection("users").document(userId);
        userDoc.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("Firebase", "Failed to get user document", task.getException());
                return;
            }

            DocumentSnapshot userSnapshot = task.getResult();
            assert userSnapshot != null;

            if (!userSnapshot.exists()) {
                Log.e("Firebase", "User document does not exist");
                return;
            }

            Log.d("Firebase", "User document found");
            HashMap<String, Object> data = (HashMap<String, Object>) userSnapshot.getData();
            assert data != null;

            bmiData = (ArrayList<HashMap<String, Object>>) data.get("bmi");
            assert bmiData != null;

            updateChart();
        });
    }

    private void updateChart() {

        // Sort bmiData based on timestamp
        bmiData.sort((a, b) -> {
            Timestamp dateA = (Timestamp) a.get("date");
            Timestamp dateB = (Timestamp) b.get("date");
            return dateA.compareTo(dateB);
        });

        ArrayList<Timestamp> dateValues = new ArrayList<>();
        ArrayList<Float> bmiValues = new ArrayList<>();

        // If length of data is less than 2, delete the chart from the layout
        if (bmiData.size() < 2) {
            Log.w("MainActivity", "Not enough data to display chart");
            chart.setVisibility(View.GONE);
            return;
        }

        for (HashMap<String, Object> entry : bmiData) {
            Timestamp date = (Timestamp) entry.get("date");
            dateValues.add(date);

            float bmi = ((Number) entry.get("val")).floatValue();
            bmiValues.add(bmi);
        }

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < bmiValues.size(); i++) {
            entries.add(new Entry(dateValues.get(i).toDate().getTime(), bmiValues.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "BMI");

        // Dataset formatting
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(5f);
        dataSet.setColor(0xFF3F51B5);
        dataSet.setCircleColor(0xFF3F51B5);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawFilled(true);

        // Blur background
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
        dataSet.setFillDrawable(drawable);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        chart.invalidate();
    }

    private void redirectTo(Class<?> activity){
        Intent intent = new Intent(MainActivity.this, activity);
        startActivity(intent);
    }

    private void initializeUI() {
        ImageButton settingsButton = findViewById(R.id.Settings_Button);
        settingsButton.setOnClickListener(v -> redirectTo(SettingsActivity.class));

        ImageButton signOutButton = findViewById(R.id.SignOut_Button);
        signOutButton.setOnClickListener(v -> {
            mAuth.signOut();
            redirectTo(LoginActivity.class);
        });

        ImageButton supportButton = findViewById(R.id.Support_Button);
        supportButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(android.net.Uri.parse("tel:8369801262"));
            startActivity(intent);
        });

        Button notesButton = findViewById(R.id.Notes_Button);
        notesButton.setOnClickListener(v -> redirectTo(NotesActivity.class));

        String preferredHeightUnit = sharedPreferences.getString(HEIGHT_KEY, "cm");
        TextView heightInputTextView = findViewById(R.id.HeightUnitDisplay_TextView);
        heightInputTextView.setText(preferredHeightUnit);

        String preferredWeightUnit = sharedPreferences.getString(WEIGHT_KEY, "kg");
        TextView weightInputTextView = findViewById(R.id.WeightUnitDisplay_TextView);
        weightInputTextView.setText(preferredWeightUnit);

        ImageButton addDataButton = findViewById(R.id.AddData_Button);
        addDataButton.setOnClickListener(v -> {
            EditText weightInput = findViewById(R.id.Weight_EditText);
            EditText heightInput = findViewById(R.id.Height_EditText);

            float weight = Float.parseFloat(weightInput.getText().toString());
            float height = Float.parseFloat(heightInput.getText().toString());

            if (preferredWeightUnit.equals("lbs")) weight *= 0.453592f; // Convert lbs to kg
            if (preferredHeightUnit.equals("in")) height *= 2.54f/100; // Convert inches to meters
            else height /= 100; // Convert cm to meters

            HashMap<String, Object> data = new HashMap<>();
            data.put("date", Timestamp.now());

            float bmi = weight / (height * height);
            data.put("val", bmi);

            bmiData.add(data);

            db.collection("users").document(userId).update("bmi", bmiData).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Failed to update user document", task.getException());
                    return;
                }

                Log.d("Firebase", "User document updated");
                updateChart();
            });
        });
    }

    private void refreshSteps() {
        if (stepCounterSensor == null) {
            Log.w("StepCounter", "Step counter sensor not found");
            return;
        }

        sensorManager.registerListener((SensorEventListener) this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();

        sensorManager.unregisterListener((SensorEventListener) this, stepCounterSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (!(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER)) return;

        int steps = (int) sensorEvent.values[0];
        if (baselineSteps == -1) {
            baselineSteps = steps;
            Log.d("StepCounter", "Set baseline: " + baselineSteps);
            return;
        }
        Log.d("StepCounter", "Steps: " + steps);

        progressBar.setProgress(steps - baselineSteps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        ;;
    }
}