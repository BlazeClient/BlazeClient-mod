package me.nobokik.blazeclient.api.helpers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.mod.GeneralSettings;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static me.nobokik.blazeclient.Client.mc;
import static me.nobokik.blazeclient.Client.uuid;

public class CapeHelper {

    public static HashMap<String, String> capes = new HashMap<>();
    public static ArrayList<String> ownedCapes = new ArrayList<>();

    public static String equippedCape = "";
    public static void init() {
        capes.put("blaze-red", "Blaze Red");
        capes.put("pastel-aesthetic", "Pastel Aesthetic");
        capes.put("animated-astelic", "Astelic");
        capes.put("animated-purple-sky", "Purple Sky");
        capes.put("axolotl", "Axolotl");
        capes.put("glow-squid", "Glow Squid");
    }

    public static void equipCosmetic(String cosmetic) {
        if(mc.player == null) return;
        if(!Client.modManager().getMod(GeneralSettings.class).showCosmetics.isEnabled()) return;
        try {
            URL url = new URL("http://94.250.250.243:1337/equipCosmetic");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("uuid", mc.player.getUuid().toString());
            jsonObject.addProperty("cosmetic", cosmetic);
            String jsonInputString = jsonObject.toString();

            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
        } catch (Exception ignored) {}
    }

    private static final Gson GSON = new Gson();

    public static void getPlayerCosmetics() {
        if(!Client.modManager().getMod(GeneralSettings.class).showCosmetics.isEnabled()) return;
        try {
            URL url = new URL("http://94.250.250.243:1337/user/" + uuid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(false);
            connection.setDoInput(true);

            System.out.println("Getting cosmetics...");
            InputStream response = connection.getInputStream();
            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            JsonObject convertedResponse = new Gson().fromJson(responseBody, JsonObject.class);
            JsonObject cosmetics = convertedResponse.getAsJsonObject("cosmetics");
            Set<Map.Entry<String, JsonElement>> entrySet = cosmetics.entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                String s = cosmetics.get(entry.getKey()).toString().replace("\"", "");
                System.out.println(entry.getKey() + " -> " + s);
                if (entry.getKey().equalsIgnoreCase("equipped")) {
                    equippedCape = s;
                }
                if (entry.getKey().equalsIgnoreCase("allUnlocked")) {
                    ownedCapes.clear();
                    ownedCapes.addAll(capes.keySet());
                } else {
                    if (!ownedCapes.contains(entry.getKey())) ownedCapes.add(entry.getKey());
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static ArrayList<String> getOwnedCosmetics(UUID uuid) {
        ArrayList<String> capes = new ArrayList<>();
        if(mc.player == null) return capes;
        try {

            URL url = new URL("http://94.250.250.243:1337/user/"+uuid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(false);
            connection.setDoInput(true);

            //System.out.println("Getting cosmetics...");
            InputStream response = connection.getInputStream();
            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            JsonObject convertedResponse = new Gson().fromJson(responseBody, JsonObject.class);
            JsonObject cosmetics = convertedResponse.getAsJsonObject("cosmetics");
            Set<Map.Entry<String, JsonElement>> entrySet = cosmetics.entrySet();
            for(Map.Entry<String,JsonElement> entry : entrySet){
                String s = cosmetics.get(entry.getKey()).toString().replace("\"", "");
                if (!entry.getKey().equalsIgnoreCase("equipped")) {
                    capes.add(s);
                }
            }
            return capes;
        } catch (Exception ignored) {}
        return capes;
    }

    public static String getEquippedCosmetic(UUID uuid) {
        if(mc.player == null) return "";
        if(!Client.modManager().getMod(GeneralSettings.class).showCosmetics.isEnabled()) return "";
        try {
            URL url = new URL("http://94.250.250.243:1337/user/"+uuid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(false);
            connection.setDoInput(true);

            //System.out.println("Getting cosmetics...");
            InputStream response = connection.getInputStream();
            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            JsonObject convertedResponse = new Gson().fromJson(responseBody, JsonObject.class);
            JsonObject cosmetics = convertedResponse.getAsJsonObject("cosmetics");
            Set<Map.Entry<String, JsonElement>> entrySet = cosmetics.entrySet();
            for(Map.Entry<String,JsonElement> entry : entrySet){
                String s = cosmetics.get(entry.getKey()).toString().replace("\"", "");
                if (entry.getKey().equalsIgnoreCase("equipped")) {
                    return s;
                }
            }
            return "";
        } catch (Exception ignored) {}
        return "";
    }
}
