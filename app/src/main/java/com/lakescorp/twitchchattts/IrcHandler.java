package com.lakescorp.twitchchattts;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListener;
import com.gikk.twirk.types.emote.Emote;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import com.vdurmont.emoji.EmojiParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class IrcHandler{

    private final Context context;
    TextView chatView;
    TextToSpeech tts;
    String lastUser = "";
    ArrayList<String> ignore = new ArrayList<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Removes emotes from a message
     * @param message The message to clean
     * @return The message with emotes removed
     */
    String cleanEmotes(TwitchMessage message) {
        String ret = message.getContent();
        for (Emote emote : message.getEmotes()) {
            ret = ret.replace(emote.getPattern(), "");
        }
        ret = EmojiParser.removeAllEmojis(ret);
        return ret;
    }

    public IrcHandler(TextView chatView, Context context) {
        this.chatView = chatView;
        ignore.add("nightbot"); // Ignore Nightbot user messages
        this.context = context.getApplicationContext();

        tts = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.getDefault());
            }
        });
    }

    /**
     * Start the IRC connection
     * @param strings The username and oauth token
     */
    public void start(String... strings) {
        executorService.submit(() -> {
            try {
                Twirk twirk = new TwirkBuilder(strings[0], strings[0], strings[1]).build();
                twirk.addIrcListener(new TwirkListener() {

                    public void onPrivMsg(TwitchUser sender, TwitchMessage message) {
                        if (ignore.contains(sender.getDisplayName().toLowerCase())) return; // Ignore messages from ignored users

                        if (message.getContent().startsWith("!")) return; // Ignore messages starting with "!"

                        // Update the chat view with the new message
                        new Handler(Looper.getMainLooper()).post(() -> {
                            chatView.setText(chatView.getText() + "\n" + sender.getDisplayName() + ": " + message.getContent());
                        });

                        String toSpeak;
                        String cleanMessage = cleanEmotes(message);
                        if (cleanMessage.isEmpty()) return;

                        // Not adding the user's name if they sent multiple messages in a row
                        if (sender.getDisplayName().equals(lastUser)) {
                            toSpeak = cleanMessage;
                        } else {
                            toSpeak = sender.getDisplayName().replace("_", " ") + " " + context.getString(R.string.said) + " " + cleanMessage;
                        }

                        lastUser = sender.getDisplayName();
                        tts.speak(toSpeak, TextToSpeech.QUEUE_ADD, null, null);
                    }
                });
                twirk.connect();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace(); // Handle exception properly
            }
        });
    }

    public void stop() {
        executorService.shutdownNow();
    }
}
