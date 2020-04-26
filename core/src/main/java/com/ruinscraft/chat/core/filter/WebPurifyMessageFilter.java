package com.ruinscraft.chat.core.filter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

public class WebPurifyMessageFilter extends MessageFilter {

    private static final String URL_BASE = "https://api1.webpurify.com/services/rest/?method=webpurify.live.check&api_key=%s&text=%s&format=json";
    private static final JsonParser PARSER = new JsonParser();

    public WebPurifyMessageFilter(String webpurifyKey) {
        super("WebPurify Filter", message -> {
            try {
                URL url = new URL(String.format(URL_BASE, webpurifyKey, message));
                URLConnection request = url.openConnection();

                request.connect();

                JsonElement jsonResponse = PARSER.parse(new InputStreamReader((InputStream) request.getContent()));
                int found = jsonResponse.getAsJsonObject().get("rsp").getAsJsonObject().get("found").getAsInt();

                if (found > 0) {
                    return Optional.empty();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return Optional.of(message);
        });
    }

}
