package com.ruinscraft.chat.filters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.configuration.ConfigurationSection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;

public class ProfanityFilter implements ChatFilter {

    private String webpurifyKey;
    private static JsonParser jsonParser = new JsonParser();

    public ProfanityFilter(ConfigurationSection profanitySection) {
        webpurifyKey = profanitySection.getString("webpurify-key");
    }

    @Override
    public String filter(String message) throws NotSendableException {
        if (!isOpen("api1.webpurify.com")) {
            return message;
        }
        String url = generateWebPurifyUrl(message, webpurifyKey);
        String response = getResponse(url);
        JsonObject json = jsonParser.parse(response).getAsJsonObject();
        JsonObject jsonResponse = json.get("rsp").getAsJsonObject();
        JsonElement found = jsonResponse.get("found");
        if (found == null) {
            return message;
        }
        if (found.getAsInt() > 0) {
            throw new NotSendableException("Message not appropriate");
        }
        return message;
    }

    private static boolean isOpen(String address) {
        try (Socket socket = new Socket(address, 80)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getResponse(String urlString) {
        URL url;
        BufferedReader br;
        try {
            String content = "";
            url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setAllowUserInteraction(false);
            br = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                content = content + inputLine;
            }
            br.close();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            url = null;
            br = null;
        }
        return null;
    }

    private static String generateWebPurifyUrl(String message, String apiKey) {
        message = encodeUrl(message);
        return "http://api1.webpurify.com/services/rest/?api_key=" + apiKey
                + "&method=webpurify.live.check&format=json&text=" + message;
    }

}
