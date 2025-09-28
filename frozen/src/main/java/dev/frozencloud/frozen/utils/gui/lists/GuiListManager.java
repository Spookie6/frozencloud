package dev.frozencloud.frozen.utils.gui.lists;

import dev.frozencloud.frozen.Frozen;
import dev.frozencloud.frozen.utils.gui.overlays.Overlay;
import dev.frozencloud.frozen.utils.gui.overlays.OverlayConfigManager;
import dev.frozencloud.frozen.utils.gui.overlays.OverlayManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;

public class GuiListManager extends GuiScreen {

    private final ListType type;
    public ScaledResolution res;

    public boolean opened = false;

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
        drawDefaultBackground();


    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {}

    @Override
    public void handleMouseInput() throws IOException {}

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_ESCAPE) {
            for (Overlay overlay : OverlayManager.getOverlays()) {
                overlay.setEditMode(false);
            }
            OverlayConfigManager.saveOverlayConfigs();
            saveConfig();
            opened = false;
        }
    }

    public void open() {
        Frozen.guiToOpen = this;
        opened = true;
    }

    private void saveConfig() {
        ;
    }

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
