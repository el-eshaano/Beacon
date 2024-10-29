package com.eshaan.beacon;

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

import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

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


        RadioGroup unitRadioGroup = findViewById(R.id.Unit_RadioGroup);
        unitRadioGroup.check(sharedPreferences.getString("unit", "kg").equals("kg") ? R.id.Kg_RadioButton : R.id.Lbs_RadioButton);
        setRadioGroupListener(unitRadioGroup, "unit", Map.of(
                R.id.Kg_RadioButton, "kg",
                R.id.Lbs_RadioButton, "lbs"
        ));

        RadioGroup notificationFreqRadioGroup = findViewById(R.id.Notification_RadioGroup);
        notificationFreqRadioGroup.check(sharedPreferences.getString("notificationFreq", "3").equals("3") ? R.id.ThreeMonths_RadioButton :
                sharedPreferences.getString("notificationFreq", "3").equals("6") ? R.id.SixMonths_RadioButton : R.id.TwelveMonths_RadioButton);
        setRadioGroupListener(notificationFreqRadioGroup, "notificationFreq", Map.of(
                R.id.ThreeMonths_RadioButton, 3,
                R.id.SixMonths_RadioButton, 6,
                R.id.TwelveMonths_RadioButton, 12
        ));


    }

    private <T> void setRadioGroupListener(RadioGroup radioGroup, String key, Map<Integer, T> valueMap) {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, valueMap.get(checkedId).toString());
            editor.apply();
            Log.d("SettingsActivity", "Saved " + key + " as " + valueMap.get(checkedId));
        });
    }
}