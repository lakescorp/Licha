package com.lakescorp.twitchchattts;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {
    String username;
    String oauth;

    TextView textView;
    IrcHandler connectToTwitch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        textView = (TextView) findViewById(R.id.chatText) ;

        findViewById(R.id.settings_button).setOnClickListener(v -> {
            // Open the settings activity
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        // Get the username and oauth from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            oauth = extras.getString("oauth");
            username = extras.getString("username");
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Instantiate and start the IrcHandler
        connectToTwitch = new IrcHandler(textView, ChatActivity.this);
        connectToTwitch.start(username, oauth);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the IrcHandler when the activity is destroyed
        if (connectToTwitch != null) {
            connectToTwitch.stop();
        }
    }
}

