package com.github.spookie6.frozen.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;

import cc.polyfrost.oneconfig.config.data.OptionSize;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;
import com.github.spookie6.frozen.Frozen;
import org.lwjgl.input.Keyboard;

import static com.github.spookie6.frozen.Frozen.mc;

public class ModConfig extends Config {

    public ModConfig() {
        super(new Mod(Frozen.MODID, ModType.SKYBLOCK, "/frozen.svg"), Frozen.MODID + ".json");
        initialize();

        registerKeyBind(moveOverlaysKeybind, Frozen.guiOverlayEditor::open);

        addDependency("bloodTimer", "split");
//        addDependency("slotbinding", false);
//        addDependency("wardrobeAutoClose", false);
        slotbinding = false;
        wardrobeAutoClose = false;

        addListener("legacyAxes", mc::refreshResources);

        addDependency("wardrobePreventUnequip", "wardrobePackets");
        addDependency("wdKeyOne", "wardrobePackets");
        addDependency("wdKeyTwo", "wardrobePackets");
        addDependency("wdKeyThree", "wardrobePackets");
        addDependency("wdKeyFour", "wardrobePackets");
        addDependency("wdKeyFive", "wardrobePackets");
        addDependency("wdKeySix", "wardrobePackets");
        addDependency("wdKeySeven", "wardrobePackets");
        addDependency("wdKeyEight", "wardrobePackets");
        addDependency("wdKeyNine", "wardrobePackets");
    }

    //    SETTINGS
    @KeyBind(
            name = "Open overlay editor",
            description = "Opens the overlay locations editor gui"
    )
    public static OneKeyBind moveOverlaysKeybind = new OneKeyBind(UKeyboard.KEY_RCONTROL);

    // Debug control
    @Switch(
            name = "Debug messages"
    )
    public static boolean debugMessages = false;

    @Switch(
            name = "Debug overlays"
    )
    public static boolean debugOverlays = false;

//    @Button(
//            name = "Open Locational Messages", text = "Open GUI!", description = "Opens a gui where you can change your locational messages")
//    Runnable runnable = () -> Frozen.guiLocationalMessagesList.open();

    //   PLAYER
    @Switch(
            name = "Custom player scale",
            description = "Enabled custom player scale",
            subcategory = "Player"
    )
    public static boolean customPlayerScale = false;

    @Slider(
            name = "Player scale X",
            subcategory = "Player",
            min = 0.1F,
            max = 3.0F
    )
    public static float playerScaleX = 1.0F;

    @Slider(
            name = "Player scale Y",
            subcategory = "Player",
            min = 0.1F,
            max = 3.0F
    )
    public static float playerScaleY = 1.0F;

    @Slider(
            name = "Player scale Z",
            subcategory = "Player",
            min = 0.1F,
            max = 3.0F
    )
    public static float playerScaleZ = 1.0F;

    @Switch(
            name = "Legacy axes",
            subcategory = "Misc"
    )
    public static boolean legacyAxes = false;

    //    DUNGEONS
    @Switch(
            name = "Explosive shot",
            description = "Lets you know how much damage your explosion shot did per enemy",
            category = "Dungeons",
            subcategory = "General"
    )
    public static boolean exploShot = false;

    @Switch(
            name = "Auto architects draft",
            description = "Automatically gets a darft from your sack if you fail a puzzle",
            category = "Dungeons",
            subcategory = "General"
    )
    public static boolean autoDraft = false;

    @Switch(
            name = "Auto potion bag",
            description = "Auto opend potion bag opun entering a dungeon",
            category = "Dungeons",
            subcategory = "General"
    )
    public static boolean autoPotBag = false;

    @Switch(
            name = "Crystal place title",
            description = "Displays a title on your screen when you have a crystal",
            category = "Dungeons",
            subcategory = "General"
    )
    public static boolean crystalTitle = false;

    @Switch(
            name = "Crystal time message",
            description = "Sends a message how long it took you to place your crystal",
            category = "Dungeons",
            subcategory = "General"
    )
    public static boolean sendCrystalTime = false;

    //    Refills
    @Dropdown(
            name = "Auto pearl refill",
            description = "Automatically refills pearls from your sacks to inv",
            category = "Dungeons",
            subcategory = "Refills",
            options = {"Don't", "On Threshold", "On Dungeon Start"},
            size = OptionSize.DUAL
    )
    public static int pearlRefill = 0;

    @Slider(
            name = "Auto pearl refill threshold",
            category = "Dungeons",
            subcategory = "Refills",
            min = 1,
            max = 15,
            step = 1
    )
    public static int pearlRefillThreshold = 8;

    @Dropdown(
            name = "Auto jerry refill",
            description = "Automatically refills pearls from your sacks to inv",
            category = "Dungeons",
            subcategory = "Refills",
            options = {"Don't", "On Threshold", "On Dungeon Start"},
            size = OptionSize.DUAL
    )
    public static int jerryRefill = 0;

    @Slider(
            name = "Auto jerry refill threshold",
            category = "Dungeons",
            subcategory = "Refills",
            min = 1,
            max = 63,
            step = 1
    )
    public static int jerryRefillThreshold = 16;

    @Dropdown(
            name = "Auto superboom refill",
            description = "Automatically refills pearls from your sacks to inv",
            category = "Dungeons",
            subcategory = "Refills",
            options = {"Don't", "On Threshold", "On Dungeon Start"},
            size = OptionSize.DUAL
    )
    public static int superboomRefill = 0;

    @Slider(
            name = "Auto superboom refill threshold",
            category = "Dungeons",
            subcategory = "Refills",
            min = 1,
            max = 63,
            step = 1
    )
    public static int superboomRefillThreshold = 16;

    //    Leaping
    @Dropdown(
        name = "Hide players after leaping",
        description = "When to hide players",
        category = "Dungeons",
        subcategory = "Leap menu",
        options = { "Never", "Always", "Only during boss", "Only during terminals", "Only during clear"}
    )
    public static int hideAfterLeap = 0;

    @Slider(
            name = "Player hide duration",
            description = "How long the players will be hidden for after leaping (in seconds)",
            category = "Dungeons",
            subcategory = "Leap menu",
            step = 1,
            min = 1,
            max = 60
    )
    public static int hidePlayersAfterLeapDuration = 5;

    @Switch(
            name = "Crystal spawn timer",
            category = "Dungeons",
            subcategory = "Tick Timers"
    )
    public static boolean crystalTicks = false;

    @Switch(
            name = "Storm pad timer",
            category = "Dungeons",
            subcategory = "Tick Timers"
    )
    public static boolean padTicks = false;

    @Switch(
            name = "Storm crush timer",
            category = "Dungeons",
            subcategory = "Tick Timers"
    )
    public static boolean crushTicks = false;

    @Switch(
            name = "Barrier timer",
            category = "Dungeons",
            subcategory = "Tick Timers"
    )
    public static boolean barrierTicks = false;

    @Switch(
            name = "Dynamic barrier colors",
            description = "Dynamic colors red -> yellow -> green for barrier timer",
            category = "Dungeons",
            subcategory = "Tick Timers"
    )
    public static boolean barrierTicksDynamicColors = false;

    @Switch(
            name = "Start timer",
            category = "Dungeons",
            subcategory = "Tick Timers"
    )
    public static boolean startTimer = false;

    @Switch(
            name = "Dragon spawn timer",
            category = "Dungeons",
            subcategory = "Tick Timers"
    )
    public static boolean dragonTimer = false;

    @Switch(
            name = "Dynamic dragon timer colors",
            description = "Dynamic colors according to drag prio for timer",
            category = "Dungeons",
            subcategory = "Tick Timers"
    )
    public static boolean dragonSpawnTimerDynamicColors = false;

    @Switch(
            name = "Custom terminal titles",
            description = "Replaces the vanilla \"Spookie6 has completed terminal (5/7)\" with custom ones.",
            category = "Dungeons",
            subcategory = "Terminals"
    )
    public static boolean customTerminalTitles = false;

    @Switch(
            name = "Static color",
            category = "Dungeons",
            subcategory = "Terminals"
    )
    public static boolean terminalTitlesStaticColor = false;

    @Slider(
            name = "Title duration (s)",
            category = "Dungeons",
            subcategory = "Terminals",
            min = 1.0F,
            max = 5.0F
    )
    public static float terminalTitleDuration = 3.0F;

    @Switch(
            name = "Main toggle",
            description = "Main toggle for locational message features",
            category = "Dungeons",
            subcategory = "Locational Messages",
            size = OptionSize.DUAL
    )
    public static boolean locationalMessages = false;

    @Slider(
            name = "Title duration (s)",
            category = "Dungeons",
            subcategory = "Locational Messages",
            min = 1.0F,
            max = 5.0F
    )
    public static float locationMessageTitleDuration = 1.5F;

    @Switch(
            name = "Simon says",
            category = "Dungeons",
            subcategory = "Locational Messages"
    )
    public static boolean locationsMessageSimonSays = false;

    @Switch(
            name = "Early enter 2",
            category = "Dungeons",
            subcategory = "Locational Messages"
    )
    public static boolean locationsMessageEarlyEnter2 = false;

    @Switch(
            name = "Safe spot 2",
            category = "Dungeons",
            subcategory = "Locational Messages"
    )
    public static boolean locationsMessageSafeSpot2 = false;

    @Switch(
            name = "Early enter 3",
            category = "Dungeons",
            subcategory = "Locational Messages"
    )
    public static boolean locationsMessageEarlyEnter3 = false;

    @Switch(
            name = "Safe spot 3",
            category = "Dungeons",
            subcategory = "Locational Messages"
    )
    public static boolean locationsMessageSafeSpot3;

    @Switch(
            name = "Core",
            category = "Dungeons",
            subcategory = "Locational Messages"
    )
    public static boolean locationsMessageCore;

    @Switch(
            name = "Tunnel",
            category = "Dungeons",
            subcategory = "Locational Messages"
    )
    public static boolean locationsMessageTunnel;

    @Switch(
            name = "Mid",
            category = "Dungeons",
            subcategory = "Locational Messages"
    )
    public static boolean locationsMessageMid;

    @Switch(
            name = "Relic timer",
            description = "Timer for when relics will spawn",
            category = "Dungeons",
            subcategory = "Relics"
    )
    public static boolean relicTimer = false;

    @Switch(
            name = "Send relic times",
            description = "Send relic times to chat",
            category = "Dungeons",
            subcategory = "Relics"
    )
    public static boolean sendRelicTimes = false;

    @Switch(
            name = "Block incorrect clicks",
            description = "Blocks you from placing your relic on the wrong cauldron",
            category = "Dungeons",
            subcategory = "Relics"
    )
    public static boolean relicsBlockIncorrect = false;

    @Dropdown(
            name = "Highlight correct cauldron",
            options = {"Disabled", "Outlined", "Filled"},
            category = "Dungeons",
            subcategory = "Relics"
    )
    public static int cauldronHighlight = 0;

    //    HUD
    @Switch(
            name = "Time display",
            category = "HUD"
    )
    public static boolean timeHud = false;

    @Switch(
            name = "Speed display",
            category = "HUD"
    )
    public static boolean speedHud = false;

    @Switch(
            name = "Warp cooldown",
            category = "HUD"
    )
    public static boolean warpCooldown = false;

    @Switch(
            name = "Reaper timer",
            category = "HUD"
    )
    public static boolean reaperTimer = false;

    //    INVINCIBILITY
    @Switch(
            name = "Mask timers",
            category = "HUD",
            subcategory = "Invincibility"
    )
    public static boolean maskTimers = false;

    @Switch(
            name = "Invincibility timer",
            category = "HUD",
            subcategory = "Invincibility"
    )
    public static boolean invincibilityTimer = false;

    @Switch(
            name = "Right align",
            description = "Align timers to the right",
            category = "HUD",
            subcategory = "Invincibility"
    )
    public static boolean masktimersRightAlign = false;

    @Slider(
            name = "Extra width",
            description = "How much extra width to add to the mask timers overlay",
            category = "HUD",
            subcategory = "Invincibility",
            min = 0,
            max = 50,
            step = 1
    )
    public static int maskTimersExtraWidth = 10;

    @Switch(
            name = "Send chat notification",
            category = "HUD",
            subcategory = "Invincibility",
            description = "Sends a message to party chat about a mask popping",
            size = 2
    )
    public static boolean maskTimerSendNoti = false;

    @Switch(
            name = "Dungeons only",
            category = "HUD",
            subcategory = "Invincibility",
            description = "Whether to show the overlay outside of dungeon servers"
    )
    public static boolean maskTimerDungeonsOnly = true;

    @Text(
            name = "Avaiable title",
            category = "HUD",
            subcategory = "Invincibility",
            description = "Text to show when mask is available",
            placeholder = "Available"
    )
    public static String maskTimerReadyTitle = "Available";

    @Info(
            text = "Use {mask} in the chat message for the mask type",
            category = "HUD",
            subcategory = "Invincibility",
            type = InfoType.INFO,
            size = OptionSize.DUAL
    )
    public static boolean ignored;

    @Text(
            name = "Chat message",
            category = "HUD",
            subcategory = "Invincibility",
            description = "Text to send to chat",
            placeholder = "{mask} procced!",
            size = OptionSize.DUAL
    )
    public static String maskTimerChatMsg = "{mask} procced!";

//        SPLITS
    @Switch(
            name = "Run splits",
            category = "HUD",
            subcategory = "Splits"
    )
    public static boolean splits = false;

    @Switch(
            name = "Blood timer",
            category = "HUD",
            subcategory = "Splits"
    )
    public static boolean bloodSplit = false;

    @Switch(
            name = "Show minutes",
            description = "Format like \"1m 20s\" instead of \"80s\"",
            category = "HUD",
            subcategory = "Splits"
    )
    public static boolean splitsShowMinutes;

    @Switch(
            name = "Show tick minutes",
            description = "Format like \"1m 20s\" instead of \"80s\"",
            category = "HUD",
            subcategory = "Splits"
    )
    public static boolean splitsTickShowMinutes;

    @Switch(
            name = "Right align",
            description = "Align timers to the right",
            category = "HUD",
            subcategory = "Splits"
    )
    public static boolean splitsRightAlign = false;

    @Switch(
            name = "Send splits",
            category = "HUD",
            subcategory = "Splits",
            description = "Send the splits to chat at the end of the run",
            size = 2
    )
    public static boolean sendSplits = false;

    @Slider(
            name = "Extra width",
            description = "How much extra width to add to the splits overlay",
            category = "HUD",
            subcategory = "Splits",
            min = 0,
            max = 50,
            step = 1
    )
    public static int splitsExtraWidth = 10;

    @Dropdown(
            name = "Show boss split",
            category = "HUD",
            subcategory = "Splits",
            options = {
                    "Always",
                    "Never",
                    "All floors except 7",
                    "Only floor 7"
            }
    )
    public static int showBossSplit = 0;

//    GUI OPTIONS
    @Info(
            text = "All settings under this category go in direct violation of hypixel's TOS and guidelines",
            type = InfoType.WARNING,
            category = "Gui",
            size = 3
    )
    public static boolean ignored2;

    @Switch(
            name = "Middle click pets",
            description = "Sends a click packet on left click in pets menu (no item pickup)",
            category = "Gui"
    )
    public static boolean petsPacket = false;

    @Switch(
            name = "Middle click equipment",
            description = "Sends a click packet on left click in equipment menu (no item pickup)",
            category = "Gui"
    )
    public static boolean equipmentPacket = false;

    @Switch(
            name = "Middle click trades",
            description = "Sends a click packet on left click in trades menu (no item pickup)",
            category = "Gui"
    )
    public static boolean tradesPacket = false;

    @Switch(
            name = "Enable wardrobe keybinds",
            description = "Enables all wadrobe functionality",
            category = "Gui",
            subcategory = "Wardrobe"
    )
    public static boolean wardrobePackets = false;

    @Switch(
            name = "Prevent unequip",
            description = "Prevents you from equipping armor in the wardrobe when using keybinds",
            category = "Gui",
            subcategory = "Wardrobe"
    )
    public static boolean wardrobePreventUnequip = false;

    @Switch(
            name = "Auto close",
            description = "Auto close the gui after equipping a new armor set",
            category = "Gui",
            subcategory = "Wardrobe"
    )
    public static boolean wardrobeAutoClose = false;

    @KeyBind(
            name = "Slot 1",
            size = 2,
            category = "Gui",
            subcategory = "Wardrobe"
    )
    public static OneKeyBind wdKeyOne = new OneKeyBind(Keyboard.KEY_1);
    @KeyBind(
            name = "Slot 2",
            size = 2,
            category = "Gui",
            subcategory = "Wardrobe"
    )
    public static OneKeyBind wdKeyTwo = new OneKeyBind(Keyboard.KEY_2);
    @KeyBind(
            name = "Slot 3",
            size = 2,
            category = "Gui",
            subcategory = "Wardrobe"
    )
    public static OneKeyBind wdKeyThree = new OneKeyBind(Keyboard.KEY_3);
    @KeyBind(
            name = "Slot 4",
            size = 2,
            category = "Gui",
            subcategory = "Wardrobe"
    )
    public static OneKeyBind wdKeyFour = new OneKeyBind(Keyboard.KEY_4);
    @KeyBind(
            name = "Slot 5",
            size = 2,
            category = "Gui",
            subcategory = "Wardrobe"
    )
    public static OneKeyBind wdKeyFive = new OneKeyBind(Keyboard.KEY_5);
    @KeyBind(
            name = "Slot 6",
            size = 2,
            category = "Gui",
            subcategory = "Wardrobe"
    )
    public static OneKeyBind wdKeySix = new OneKeyBind(Keyboard.KEY_6);
    @KeyBind(
            name = "Slot 7",
            size = 2,
            category = "Gui",
            subcategory = "Wardrobe"
    )
    public static OneKeyBind wdKeySeven = new OneKeyBind(Keyboard.KEY_7);
    @KeyBind(
            name = "Slot 8",
            size = 2,
            category = "Gui",
            subcategory = "Wardrobe"
    )
    public static OneKeyBind wdKeyEight = new OneKeyBind(Keyboard.KEY_8);
    @KeyBind(
            name = "Slot 9",
            size = 2,
            category = "Gui",
            subcategory = "Wardrobe"
    )
    public static OneKeyBind wdKeyNine = new OneKeyBind(Keyboard.KEY_9);

    //    SlotBinding
    @Switch(
            name = "Slot binding",
            category = "Gui",
            subcategory = "Slot binding"
    )
    public static boolean slotbinding = false;

    @Switch(
            name = "Prevent dropping",
            description = "Prevents you from dropping items in bound slots",
            category = "Gui",
            subcategory = "Slot binding"
    )
    public static boolean slotbindingPreventDrop = false;

    @Switch(
            name = "Unique colors",
            description = "Makes every binding have its own color",
            category = "Gui",
            subcategory = "Slot binding"
    )
    public static boolean slotbindingUniqueColors = false;

    @Color(
            name = "Color",
            description = "Color if no unique colors",
            category = "Gui",
            subcategory = "Slot binding"
    )
    public static OneColor slotbindingColor = new OneColor(255, 125, 255, 255);

    @Switch(
            name = "Draw line",
            description = "Draw line between slots when hovering over either bound slot",
            category = "Gui",
            subcategory = "Slot binding"
    )
    public static boolean slotbindingDrawLine = false;

    @KeyBind(
            name = "Binding keybind",
            size = 2,
            category = "Gui",
            subcategory = "Slot binding"
    )
    public static OneKeyBind slotbindingKeybind = new OneKeyBind(Keyboard.KEY_SPACE);

    @KeyBind(
            name = "Clear keybind",
            size = 2,
            category = "Gui",
            subcategory = "Slot binding"
    )
    public static OneKeyBind slotbindingClearKeybind = new OneKeyBind(Keyboard.KEY_TAB);

    @Slider(
            name = "Outline thickness",
            category = "Gui",
            subcategory = "Slot binding",
            min = 1.0f,
            max = 8.0f
    )
    public static float slotbindingOutlineThickness = 2.0f;
}
