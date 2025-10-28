package dev.frozencloud.core

import dev.frozencloud.core.overlaymanager.GuiOverlayEditor
import dev.frozencloud.core.overlaymanager.OverlayConfigManager
import dev.frozencloud.core.overlaymanager.OverlayManager
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object Core {
    var initialized = false
    lateinit var mc: Minecraft
    var guiToOpen: GuiScreen? = null
    val loadedMods = ModEnum.entries.associateWith { false }.toMutableMap()
    val guiOverlayEditor = GuiOverlayEditor()

    @JvmStatic
    fun init(mc: Minecraft, mod: ModEnum) {
        loadedMods[mod] = true
        if (initialized) {
            return
        }

        this.mc = mc
        OverlayConfigManager.init()
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(guiOverlayEditor)

        initialized = true
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.END || guiToOpen == null) return

        mc.displayGuiScreen(guiToOpen)
        guiToOpen = null
    }

    @Mod.EventHandler
    fun onClientStopping(e: FMLServerStoppedEvent?) {
        OverlayConfigManager.saveOverlayConfigs()
    };

    @SubscribeEvent
    fun onRenderOverlay(event: RenderGameOverlayEvent.Post) {
        if (mc.currentScreen != null && mc.currentScreen == guiOverlayEditor) return
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            OverlayManager.renderOverlays()
        }
    }
}