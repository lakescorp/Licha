package com.lakescorp.twitchchattts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements TwitchAPI.Callback {

    Button getToken_button;
    Button logIn_button;
    String oauth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the oauth token from shared preferences
        SharedPreferences sharedPref = getSharedPreferences("preferences", MODE_PRIVATE);
        oauth = sharedPref.getString("oauth", "");

        // Set the oauth token in the input field
        ((EditText) findViewById(R.id.oauthInput)).setText(oauth);

        // Open the browser to get the oauth token
        getToken_button = (Button) findViewById(R.id.buttonToken) ;
        getToken_button.setOnClickListener(v -> {
            // Do something in response to button click
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.OauthObtainerURL)));
            startActivity(browserIntent);
        });

        // Log in to the chat
        logIn_button = (Button) findViewById(R.id.buttonLogIn);
        logIn_button.setOnClickListener(v -> {
            progressBar = new ProgressBar(this, null);
                    /*show(MainActivity.this, "Log In",
                    "Please wait...", true);*/
            oauth = ((EditText) findViewById(R.id.oauthInput)).getText().toString();
            connectToAPI();
        });
    }

    /**
     * This method launches the TwitchAPI class to connect to the Twitch API
     */
    public void connectToAPI(){
        TwitchAPI twitchAPI = new TwitchAPI();
        twitchAPI.connect(MainActivity.this, this, oauth);

    }

    /**
     * This method is called when the TwitchAPI class completes the connection to the Twitch API
     * It saves the oauth token in shared preferences and launches the ChatActivity
     * @param username The username of the user
     */
    @Override
    public void onComplete(String username) {
        //progressBar.dismiss();
        SharedPreferences sharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("oauth", oauth);
        editor.apply();
        Intent i = new Intent(MainActivity.this, ChatActivity.class);
        i.putExtra("oauth", oauth);
        i.putExtra("username", username);
        startActivity(i);
    }

    /**
     * This method is called when the TwitchAPI class encounters an error
     * It displays a toast with the error message
     * @param errorMessage The error message
     */
    @Override
    public void onError(String errorMessage) {
        //progressBar.dismiss();
        Toast errorToast = Toast.makeText(MainActivity.this, "Error" + errorMessage, Toast.LENGTH_LONG);
        errorToast.show();
    }
}



