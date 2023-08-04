package me.nobokik.blazeclient.api.helpers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.mod.GeneralSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.UUID;

import static me.nobokik.blazeclient.Client.mc;


public class IndicatorHelper {
    public static ArrayList<UUID> clientUsers = new ArrayList<>();
    public static Identifier badgeIcon = new Identifier("blaze-client", "icon.png");
    private static final Gson GSON = new Gson();

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static void addBadge(Entity entity, MatrixStack matrices) {
        if(!Client.modManager().getMod(GeneralSettings.class).showClientBadges.isEnabled()) return;
        if (entity instanceof PlayerEntity && !entity.isSneaky()) {
            if (isUsingClient(entity.getUuid())) {
                RenderSystem.enableDepthTest();
                RenderSystem.setShaderTexture(0, badgeIcon);

                assert MinecraftClient.getInstance().player != null;
                int x = -(MinecraftClient.getInstance().textRenderer
                        .getWidth(
                                (Team.decorateName(entity.getScoreboardTeam(), entity.getName())
                                        .getString()))
                        / 2
                        + (10));

                RenderSystem.setShaderColor(1, 1, 1, 1);
                DrawableHelper.drawTexture(matrices, x, 0, 0, 0, 8, 8, 8, 8);
            }
        }
    }

    public static boolean isUsingClient(UUID u) {
        assert MinecraftClient.getInstance().player != null;
        if (u == MinecraftClient.getInstance().player.getUuid()) {
            return true;
        } else {
            return clientUsers.contains(u);
        }
    }

    public static void getUsers() {
        if(!Client.modManager().getMod(GeneralSettings.class).showClientBadges.isEnabled()) return;
        HttpURLConnection connection;
        try {
            URL url = new URL("http://94.250.250.243:1337/getPlayers");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            //connection.setRequestProperty("Authorization", "a");
            connection.setDoOutput(true);

            JsonObject jsonObject = new JsonObject();
            for (PlayerListEntry entry : Objects.requireNonNull(mc.getNetworkHandler()).getPlayerList()) {
                jsonObject.addProperty(entry.getProfile().getId().toString(), "false");
            }
            String jsonInputString = jsonObject.toString();
            //System.out.println(jsonInputString);

            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String response = readAll(br);
                JsonObject convertedResponse = GSON.fromJson(response, JsonObject.class);
                //System.out.println(convertedResponse.toString());
                for (PlayerListEntry entry : Objects.requireNonNull(mc.getNetworkHandler()).getPlayerList()) {
                    if (convertedResponse.get(entry.getProfile().getId().toString()).getAsString().equals("true")) clientUsers.add(entry.getProfile().getId());
                    else clientUsers.remove(entry.getProfile().getId());
                }

            }
        } catch (Exception ignored) {}
    }

    public static void enableClient() {
        if(mc.player == null) return;
        try {
            URL url = new URL("http://94.250.250.243:1337/enableClient");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("uuid", mc.player.getUuid().toString());
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

    public static void disableClient() {
        if(mc.player == null) return;
        try {
            URL url = new URL("http://94.250.250.243:1337/disableClient");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("uuid", mc.player.getUuid().toString());
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

}
