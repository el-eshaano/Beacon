package com.eshaan.beacon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    static final String WEIGHT_KEY = "weight_unit";
    static final String HEIGHT_KEY = "height_unit";
    static final String NOTIFICATION_KEY = "notification_freq";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button backButton = findViewById(R.id.Back_Button);
        backButton.setOnClickListener(v -> {
            finish();
        });

        sharedPreferences = getSharedPreferences("com.eshaan.beacon", MODE_PRIVATE);


        RadioGroup weightUnitRadioGroup = findViewById(R.id.WeightUnit_RadioGroup);
        weightUnitRadioGroup.check(
            sharedPreferences.getString(WEIGHT_KEY, "kg")
                    .equals("kg") ? R.id.Kg_RadioButton : R.id.Lbs_RadioButton
        );
        weightUnitRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(WEIGHT_KEY, checkedId == R.id.Kg_RadioButton ? "kg" : "lbs");
            editor.apply();
            Log.d("SettingsActivity", "Saved weight unit as " + (checkedId == R.id.Kg_RadioButton ? "kg" : "lbs"));
        });

        RadioGroup heightUnitRadioGroup = findViewById(R.id.HeightUnit_RadioGroup);
        heightUnitRadioGroup.check(
            sharedPreferences.getString(HEIGHT_KEY, "cm")
                    .equals("cm") ? R.id.Cm_RadioButton : R.id.Inches_RadioButton
        );
        heightUnitRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(HEIGHT_KEY, checkedId == R.id.Cm_RadioButton ? "cm" : "in");
            editor.apply();
            Log.d("SettingsActivity", "Saved height unit as " + (checkedId == R.id.Cm_RadioButton ? "cm" : "in"));
        });

        RadioGroup notificationFreqRadioGroup = findViewById(R.id.Notification_RadioGroup);
        int checkedVal = sharedPreferences.getInt(NOTIFICATION_KEY, 3);
        notificationFreqRadioGroup.check(
           checkedVal == 3 ? R.id.ThreeMonths_RadioButton :
           checkedVal == 6 ? R.id.SixMonths_RadioButton :
                    R.id.TwelveMonths_RadioButton
        );

        notificationFreqRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            int currentVal = sharedPreferences.getInt(NOTIFICATION_KEY, 3);
            int newVal = checkedId == R.id.ThreeMonths_RadioButton ? 3 :
                    checkedId == R.id.SixMonths_RadioButton ? 6 : 12;

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(NOTIFICATION_KEY, newVal);
            editor.apply();
            Log.d("SettingsActivity", "Saved notificationFreq as " + newVal);

            if (currentVal != newVal) updateAlarm(currentVal, newVal);
        });
    }

    private void updateAlarm(int currentVal, int newVal){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(this, NotifManager.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);

//        long interval = AlarmManager.INTERVAL_DAY * 30 * newVal;
        long interval = 60000L; // 1 minute
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10, pendingIntent);
        Log.d("SettingsActivity", "Set alarm to repeat every " + interval + " months");
    }
}