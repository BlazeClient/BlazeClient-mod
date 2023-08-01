package me.nobokik.blazeclient.api.config;

import com.google.common.base.Joiner;
import com.google.gson.*;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import me.nobokik.blazeclient.Client;
import me.nobokik.blazeclient.api.font.JColor;
import me.nobokik.blazeclient.mod.Mod;
import me.nobokik.blazeclient.mod.mods.CrosshairMod;
import me.nobokik.blazeclient.mod.setting.Setting;
import me.nobokik.blazeclient.mod.setting.settings.BooleanSetting;
import me.nobokik.blazeclient.mod.setting.settings.ColorSetting;
import me.nobokik.blazeclient.mod.setting.settings.ModeSetting;
import me.nobokik.blazeclient.mod.setting.settings.NumberSetting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class ConfigManager {
    private final Gson GSON = new Gson();
    private final Path pathConfigFolder;
    private final Path pathConfig;
    private JsonObject jsonConfig;

    public ConfigManager() {
        pathConfigFolder = FabricLoader.getInstance().getConfigDir();
        pathConfig = pathConfigFolder.resolve("blazeclient.json");
    }

    public void loadConfig() {
        try {
            if (!Files.isRegularFile(pathConfig))
                return;

            jsonConfig = GSON.fromJson(Files.readString(pathConfig), JsonObject.class);

            for (Mod mod : Client.modManager().mods) {
                JsonElement moduleJson = jsonConfig.get(mod.getName());
                if (moduleJson == null || !moduleJson.isJsonObject())
                    continue;
                JsonObject moduleConfig = moduleJson.getAsJsonObject();

                JsonElement enabledJson = moduleConfig.get("enabled");
                if (enabledJson == null || !enabledJson.isJsonPrimitive())
                    continue;

                if (enabledJson.getAsBoolean())
                    mod.enable();

                for (Setting setting : mod.settings) {
                    JsonElement settingJson = moduleConfig.get(setting.name);
                    if (settingJson == null)
                        continue;

                    if (setting instanceof BooleanSetting booleanSetting) {
                        booleanSetting.enabled = settingJson.getAsBoolean();
                    } else if (setting instanceof ModeSetting modeSetting) {
                        modeSetting.setMode(settingJson.getAsString());
                    } else if (setting instanceof NumberSetting numberSetting) {
                        numberSetting.setValue(settingJson.getAsDouble());
                    } else if (setting instanceof ColorSetting colorSetting) {
                        if (!settingJson.isJsonObject())
                            continue;

                        JsonObject colorJson = settingJson.getAsJsonObject();
                        JColor jc = new JColor(colorJson.get("color").getAsInt());
                        float[] jf = jc.getFloatColor();
                        System.out.println();
                        colorSetting.setColor(new JColor(jf[0], jf[1], jf[2], colorJson.get("alpha").getAsInt() * 0.392156862745098f / 100f), colorJson.get("rainbow").getAsBoolean());
                    }
                }
                JsonElement positionXJson = moduleConfig.get("posX");
                JsonElement positionYJson = moduleConfig.get("posY");
                mod.position.x = positionXJson.getAsFloat();
                mod.position.y = positionYJson.getAsFloat();


                if(mod.getName().equals("Crosshair")) {
                    CrosshairMod crosshairMod = (CrosshairMod) mod;
                    JsonElement rowsElement = moduleConfig.get("crosshair");
                    JsonObject rows = rowsElement.getAsJsonObject();

                    for (int row = 0; row < 11; row++) {
                        String str = rows.get(String.valueOf(row)).getAsString();
                        str = str.replace("[", "");
                        str = str.replace("]", "");
                        int col = 0;
                        for(String s : str.split(", ")) {
                            crosshairMod.crosshair[row][col] = s.equals("true");
                            col++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveConfig() {
        try {
            Files.createDirectories(pathConfigFolder);
            jsonConfig = new JsonObject();

            for (Mod mod : Client.modManager().mods) {
                JsonObject moduleConfig = new JsonObject();

                moduleConfig.addProperty("enabled", mod.isEnabled());
                for (Setting setting : mod.settings) {
                    if (setting instanceof BooleanSetting booleanSetting) {
                        moduleConfig.addProperty(setting.getName(), booleanSetting.isEnabled());
                    } else if (setting instanceof ModeSetting modeSetting) {
                        moduleConfig.addProperty(setting.getName(), modeSetting.getMode());
                    } else if (setting instanceof NumberSetting numberSetting) {
                        moduleConfig.addProperty(setting.getName(), numberSetting.getValue());
                    } else if (setting instanceof ColorSetting colorSetting) {
                        JsonObject colorJson = new JsonObject();
                        colorJson.addProperty("color", colorSetting.getValue().getRGB());
                        colorJson.addProperty("alpha", colorSetting.getValue().getAlpha());
                        colorJson.addProperty("rainbow", colorSetting.isRainbow());
                        moduleConfig.add(setting.getName(), colorJson);
                    }
                }

                jsonConfig.add(mod.getName(), moduleConfig);

                moduleConfig.addProperty("posX", mod.position.x);
                moduleConfig.addProperty("posY", mod.position.y);

                if(mod.getName().equals("Crosshair")) {
                    CrosshairMod crosshairMod = (CrosshairMod) mod;
                    JsonObject rows = new JsonObject();
                    for (int row = 0; row < 11; row++) {
                        rows.addProperty(String.valueOf(row), Arrays.toString(crosshairMod.crosshair[row]));
                        //System.out.println(Arrays.toString(crosshairMod.crosshair[row]));
                    }
                    moduleConfig.add("crosshair", rows);
                }
            }
            Files.writeString(pathConfig, GSON.toJson(jsonConfig));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
