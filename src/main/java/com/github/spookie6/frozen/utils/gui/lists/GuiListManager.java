package com.github.spookie6.frozen.utils.gui.lists;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;
import java.util.List;

public class GuiListManager extends GuiScreen {

    private ListType type;
    public ScaledResolution res;

    private List<Gui> components;

    public GuiListManager (ListType type) {
        this.type = type;
    }

    @Override
    public void initGui() {
        this.res = new ScaledResolution(mc);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {}

    @Override
    public void handleMouseInput() throws IOException {}

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {}

    public void open() {
        mc.addScheduledTask(() -> mc.displayGuiScreen(this));
    }

//    private void saveConfig() {
//        Map<String, Object> data = new HashMap<>();
//        data.put("show_invisible", showinvisible);
//        data.put("snap", snap);
//
//        try (FileWriter writer = new FileWriter(configFile)) {
//            GSON.toJson(data, writer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadConfig() {
//        configFile.getParentFile().mkdirs();
//
//        if (!configFile.exists()) return;
//
//        Type type = new TypeToken<Map<String, Object>>(){}.getType();
//        Map<String, Object> loaded;
//
//        try (FileReader reader = new FileReader(configFile)) {
//            loaded = GSON.fromJson(reader, type);
//            showinvisible = loaded.get("show_invisible") instanceof Boolean && (boolean) loaded.get("show_invisible");
//            snap = loaded.get("snap") instanceof Boolean && (boolean) loaded.get("snap");
//        } catch (IOException | ClassCastException e) {
//            e.printStackTrace(); // optionally show an error dialog/log
//        }
//    }
}
