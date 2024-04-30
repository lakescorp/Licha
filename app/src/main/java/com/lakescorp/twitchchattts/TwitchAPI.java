package com.lakescorp.twitchchattts;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TwitchAPI {

    /**
     * Callback interface for the connect method.
     */
    public interface Callback {
        void onComplete(String username);
        void onError(String errorMessage);
    }

    /**
     * Connect to the Twitch API and get the username of the user.
     * @param context The context of the application.
     * @param callback The callback to be called when the request is complete.
     * @param oauth The OAuth token to be used for the request.
     */
    public void connect(Context context, Callback callback, String oauth) {
        RequestQueue queue = Volley.newRequestQueue(context);

        // Validate the OAuth token.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, context.getString(R.string.TwitchOauthValidateURL),
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String username = jsonObject.getString("login");
                        callback.onComplete(username);
                    } catch (JSONException err) {
                        callback.onError(err.toString());
                    }
                }, error -> callback.onError(error.toString())) {

            /**
             * Add the OAuth token to the request headers.
             * @return The headers to be added to the request.
             */
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + oauth.substring(6));
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
