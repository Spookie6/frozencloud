package dev.frozencloud.core.overlaymanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.frozencloud.core.Core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class OverlayConfigManager {
    public static final File configFile = new File(Core.INSTANCE.getMc().mcDataDir, "config/frozencloud/overlays.json");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final Type type = new TypeToken<Map<String, OverlayConfig>>() {}.getType();
    public static Map<String, OverlayConfig> configMap;

    public static void init() {
        configFile.getParentFile().mkdirs();
        if (!configFile.exists()) {
            configMap = new HashMap<>();
            return;
        }

        try (FileReader reader = new FileReader(configFile)) {
            configMap = GSON.fromJson(reader, type);
        } catch (IOException e) {
            configMap = new HashMap<>();
        }
    }

    public static OverlayConfig getOverlayConfig(String configName) {
        if (!configMap.containsKey(configName)) return new OverlayConfig();
        return configMap.get(configName);
    }

    public static void updateOverlayConfig(String configName, OverlayConfig overlayConfig) {
        configMap.put(configName, overlayConfig);
    }

    public static void saveOverlayConfigs() {
        configFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(configMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
