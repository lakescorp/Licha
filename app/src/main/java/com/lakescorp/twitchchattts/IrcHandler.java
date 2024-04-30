package com.lakescorp.twitchchattts;

import android.content.Context;
import android.os.AsyncTask;
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

class ConnectToTwitch extends AsyncTask<String, Void, Void> {

    private final Context context;
    TextView textView;
    TextToSpeech t1;
    String lastUser = "";
    ArrayList<String> ignore = new ArrayList<>();

    String cleanText(TwitchMessage message) {
        String ret = message.getContent();
        for (Emote emote : message.getEmotes()) {
            ret = ret.replace(emote.getPattern(), "");
        }
        ret = EmojiParser.removeAllEmojis(ret);
        return ret;
    }

    public ConnectToTwitch(TextView textView, Context context) {
        this.textView = textView;
        ignore.add("nightbot");
        this.context = context.getApplicationContext();
        t1 = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                t1.setLanguage(Locale.getDefault());
            }
        });
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            final Twirk twirk = new TwirkBuilder(strings[0], strings[0], strings[1]).build();
            //Check to see if new people are online and add them to the list of viewers
            twirk.addIrcListener(new TwirkListener() {

                public void onPrivMsg(TwitchUser sender, TwitchMessage message) {
                    //System.out.println(sender.getDisplayName()+":"+message.toString());
                    if (!ignore.contains(sender.getDisplayName().toLowerCase())) {
                        if (!message.getContent().startsWith("!")) {
                            boolean updateLast;
                            textView.setText(textView.getText() + "\n" + sender.getDisplayName() + ": " + message.getContent());
                            String toSpeak;
                            if (sender.getDisplayName().equals(lastUser)) {
                                toSpeak = cleanText(message);
                                updateLast = !toSpeak.isEmpty();
                            } else {
                                String mensaje = cleanText(message);
                                toSpeak = sender.getDisplayName().replace("_", " ") + " " + context.getString(R.string.said) + " " + mensaje;
                                updateLast = !mensaje.isEmpty();
                            }
                            if (updateLast) {
                                lastUser = sender.getDisplayName();
                                t1.speak(toSpeak, TextToSpeech.QUEUE_ADD, null, null);
                            }
                        }
                    }
                }
            });
            twirk.connect();
        } catch (IOException | InterruptedException e) {

        }

        return null;
    }
}
