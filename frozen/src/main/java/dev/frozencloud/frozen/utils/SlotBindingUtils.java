package dev.frozencloud.frozen.utils;

import cc.polyfrost.oneconfig.config.core.OneColor;
import dev.frozencloud.frozen.utils.render.Color;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static dev.frozencloud.frozen.Frozen.mc;

public class SlotBindingUtils {
    public static final File configFile = new File(mc.mcDataDir, "config/frozen/slotbindings.json");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static SlotbindingsConfig slotbindingsConfig;
    public static Map<Integer, Integer> colorMap = new HashMap<>();

    public static void init() {
        configFile.getParentFile().mkdirs();
        if (!configFile.exists()) {
            slotbindingsConfig = new SlotbindingsConfig();
        } else {
            try (FileReader reader = new FileReader(configFile)) {
                slotbindingsConfig = GSON.fromJson(reader, SlotbindingsConfig.class);
            } catch (IOException e) {
                slotbindingsConfig = new SlotbindingsConfig(); // fallback
            }
        }

        slotbindingsConfig.current = buildBidirectionalMap(slotbindingsConfig.saved);
        colorMap = assignGroupColors(slotbindingsConfig.current);
    }

    public static Map<Integer, Set<Integer>> buildBidirectionalMap(Map<Integer, List<Integer>> oneWay) {
        Map<Integer, Set<Integer>> bidir = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : oneWay.entrySet()) {
            int a = entry.getKey();
            for (int b : entry.getValue()) {
                bidir.computeIfAbsent(a, k -> new HashSet<>()).add(b);
                bidir.computeIfAbsent(b, k -> new HashSet<>()).add(a);
            }
        }
        return bidir;
    }

    public static Map<Integer, Integer> assignGroupColors(Map<Integer, Set<Integer>> bindings) {
        Map<Integer, Integer> colorMap = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        int colorId = 0;

        for (Integer slot : bindings.keySet()) {
            if (visited.contains(slot)) continue;

            // Start a new group and color it
            Queue<Integer> queue = new LinkedList<>();
            queue.add(slot);
            visited.add(slot);

            while (!queue.isEmpty()) {
                int current = queue.poll();
                colorMap.put(current, colorId);

                for (int neighbor : bindings.getOrDefault(current, Collections.emptySet())) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }

            colorId++;
        }
        return colorMap;
    }

    public static OneColor getColorById(int id) {
        List<OneColor> palette = Arrays.stream(Color.values()).map(Color::getColor).collect(Collectors.toList());
        return palette.get(id % palette.size());
    }
    public static void addBinding(int slot1, int slot2) {
        // Determine inventory and hotbar slot
        int inventorySlot = -1;
        int hotbarSlot = -1;

        // Check slot ranges for inventory and hotbar

        if ((slot1 >= 9 && slot1 <= 35 && slot2 >= 36 && slot2 <= 44) || (slot1 >= 5 && slot1 <= 8 && slot2 >= 36 && slot2 <= 44)) {
            inventorySlot = slot1;
            hotbarSlot = slot2;
        } else if ((slot2 >= 9 && slot2 <= 35 && slot1 >= 36 && slot1 <= 44) || (slot2 >= 5 && slot2 <= 8 && slot1 >= 36 && slot1 <= 44)) {
            inventorySlot = slot2;
            hotbarSlot = slot1;
        } else {
            // Invalid slot pair, do not bind
            return;
        }

        // Remove existing binding for this inventory slot, if any
        // Since saved is Map<Integer, List<Integer>>, remove old hotbar(s)
        slotbindingsConfig.saved.remove(inventorySlot);

        // Add new binding
        List<Integer> bindings = new ArrayList<>();
        bindings.add(hotbarSlot);
        slotbindingsConfig.saved.put(inventorySlot, bindings);

        slotbindingsConfig.saved.computeIfAbsent(inventorySlot, k -> new ArrayList<>()).add(hotbarSlot);

        slotbindingsConfig.current = buildBidirectionalMap(slotbindingsConfig.saved);
        colorMap = assignGroupColors(slotbindingsConfig.current);
        saveData();
    }

    public static void removeBinding(int slot) {
        // Remove neighbors from the slot’s set
        Set<Integer> neighbors = slotbindingsConfig.current.remove(slot);
        if (neighbors == null) return;

        // For each neighbor, remove this slot from their sets
        for (Integer neighbor : neighbors) {
            Set<Integer> neighborSet = slotbindingsConfig.current.get(neighbor);
            if (neighborSet != null) {
                neighborSet.remove(slot);
                // If empty after removal, optionally remove the neighbor key itself
                if (neighborSet.isEmpty()) {
                    slotbindingsConfig.current.remove(neighbor);
                }
            }
        }

        // Also remove from saved (one-way map), to keep data consistent:
        slotbindingsConfig.saved.remove(slot);
        for (List<Integer> targets : slotbindingsConfig.saved.values()) {
            targets.removeIf(t -> t == slot);
        }

        // Recompute colors after removal
        colorMap = assignGroupColors(slotbindingsConfig.current);
        saveData();
    }

    public static Map<Integer, Set<Integer>> getCurrentBindings() {
        return slotbindingsConfig.current;
    }

    public static Set<Integer> getDirectlyConnectedSlots(int slot) {
        return slotbindingsConfig.current.getOrDefault(slot, Collections.emptySet());
    }

    public static void swapSlots(int slotId, int hotbarSlot) {
        mc.playerController.windowClick(
                mc.thePlayer.openContainer.windowId,
                slotId,
                hotbarSlot,
                2, // mode 2 = swap with hotbar
                mc.thePlayer
        );
    }

    public static void saveData() {
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(slotbindingsConfig, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class SlotbindingsConfig {
        public transient Map<Integer, Set<Integer>> current = new HashMap<>();
        public Map<Integer, List<Integer>> saved = new HashMap<>();
        public Map<String, Map<Integer, Set<Integer>>> presets = new HashMap<>();
    }
}
