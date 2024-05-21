package com.lakescorp.twitchchattts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

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


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Get references to your checkboxes
        CheckBox normalUserCheckBox = findViewById(R.id.normalUser_checkBox);
        CheckBox subscriberCheckBox = findViewById(R.id.suscriber_checkBox);
        CheckBox moderatorCheckBox = findViewById(R.id.moderator_checkBox);

// Create a shared preferences object
        SharedPreferences sharedPreferences = getSharedPreferences("RoleFilters", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Load the state of the checkboxes
        normalUserCheckBox.setChecked(sharedPreferences.getBoolean("ignoreNormalUsers", false));
        subscriberCheckBox.setChecked(sharedPreferences.getBoolean("ignoreSubscribers", false));
        moderatorCheckBox.setChecked(sharedPreferences.getBoolean("ignoreModerators", false));
// Save the state of the checkboxes when they are clicked
        normalUserCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("ignoreNormalUsers", isChecked);
            editor.apply();
        });

        subscriberCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("ignoreSubscribers", isChecked);
            editor.apply();
        });

        moderatorCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("ignoreModerators", isChecked);
            editor.apply();
        });

        Button logoutButton = findViewById(R.id.logOut_button); // replace with your logout button's id
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}

