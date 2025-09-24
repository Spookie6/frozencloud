package com.github.spookie6.frozen.utils.skyblock.dungeon;

import cc.polyfrost.oneconfig.config.core.OneColor;
import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.render.Color;
import com.github.spookie6.frozen.utils.StringUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class DungeonEnums {
    public static class DungeonPlayer {
        public final String username;
        public EntityPlayer entity;
        public Class clazz;
        public int clazzLevel;
        public String numeral;
        public boolean isDead = false;
        public int deaths = 0;

        public DungeonPlayer(String username, Class clazz, int clazzlvl, EntityPlayer entity) {
            this.username = username;
            this.clazz = clazz;
            this.clazzLevel = clazzlvl;
            this.entity = entity;
            this.isDead = false;
            this.deaths = 0;
        }

        public void setClassName(String className) {
            this.clazz = Class.getClass(className);
        }

        public void setNumeral(String numeral) {
            this.numeral = numeral;
            this.clazzLevel = (byte) StringUtils.romanToDecimal(numeral);
        }

        public void setDead(boolean dead) {
            isDead = dead;
        }
    }

    public enum Class {
        ARCHER("Archer", Color.MINECRAFT_GOLD.getColor(), '6'),
        BERSERK("Berserk", Color.MINECRAFT_DARK_RED.getColor(), '4'),
        MAGE("Mage", Color.MINECRAFT_AQUA.getColor(), 'b'),
        TANK("Tank", Color.MINECRAFT_DARK_GREEN.getColor(), '2'),
        HEALER("Healer", Color.MINECRAFT_LIGHT_PURPLE.getColor(), 'd'),
        Unknown("(None)", Color.MINECRAFT_WHITE.getColor(), 'f');

        private String className;

        Class(String className, OneColor color, char colorCode) {
            this.className = className;
        }

        public String toString() {
            return this.className;
        }

        public static Class getClass(String className) {
            return Arrays.stream(Class.values()).filter(x -> x.toString().equals(className)).findFirst().orElse(Class.Unknown);
        }

        public boolean isClass(Class clazz) {
            return this.equals(clazz);
        }
        public boolean isNotClass(Class clazz) {
            return !this.equals(clazz);
        }
    }

    public enum Floor {
        E("E"),
        F1("F1"),
        F2("F2"),
        F3("F3"),
        F4("F4"),
        F5("F5"),
        F6("F6"),
        F7("F7"),
        M1("M1"),
        M2("M2"),
        M3("M3"),
        M4("M4"),
        M5("M5"),
        M6("M6"),
        M7("M7"),
        None(null);

        private String identifier;
        public int floorNumber;
        public boolean isMM;

        Floor(String identifier) {
            this.identifier = identifier;
            if (identifier == null) {
                floorNumber = -1;
            } else {
                this.floorNumber = identifier.replaceAll("^[MFmfE]*", "").isEmpty() ? 0 : Integer.parseInt(identifier.replaceAll("^[MFmfE]*", ""));
                this.isMM = identifier.contains("M");
            }
        }

        public String toString() {
            return this.identifier;
        }

        public static Floor getFloor(String match) {
            return Arrays.stream(Floor.values()).filter(x -> x.toString().equals(match)).findFirst().orElse(Floor.None);
        }

        public boolean isFloor(int n) {
            return this.floorNumber == n;
        }

        public boolean isExactMatch(Floor fl) {
            return this.equals(fl);
        }
    }

    public enum M7Phases {
        P1("P1"),
        P2("P2"),
        P3("P3"),
        P4("P4"),
        P5("P5"),
        UNKOWN("Unknown");

        private String identifier;

        M7Phases(String identifier) {
            this.identifier = identifier;
        }
    }

    public enum Relic {
        GREEN("GREEN_KING_RELIC", "Green", 'a', Color.MINECRAFT_GREEN.getColor(), new Vec3(20.5, 6.5, 94.5), new BlockPos(49.0, 7.0, 44.0)),
        PURPLE("PURPLE_KING_RELIC", "Purple", '5', Color.MINECRAFT_DARK_PURPLE.getColor(),  new Vec3(56.5, 8.5, 132.5), new BlockPos(54.0, 7.0, 41.0)),
        BLUE("BLUE_KING_RELIC", "Blue",'b', Color.MINECRAFT_AQUA.getColor(), new Vec3(91.5, 6.5, 94.5), new BlockPos(59.0, 7.0, 44.0)),
        ORANGE("ORANGE_KING_RELIC", "Orange", '6', Color.MINECRAFT_GOLD.getColor(), new Vec3(90.5, 6.5, 56.5), new BlockPos(57.0, 7.0, 42.0)),
        RED("RED_KING_RELIC", "Red", 'c', Color.MINECRAFT_RED.getColor(), new Vec3(22.5, 6.5, 59.5), new BlockPos(51.0, 7.0, 42.0)),
        NONE("", "", 'f', Color.MINECRAFT_WHITE.getColor(), new Vec3(0, 0, 0), new BlockPos(0, 0, 0));

        public final String id;
        public final String name;
        public final char colorCode;
        public final OneColor color;
        public final Vec3 spawnPos;
        public final BlockPos cauldronPos;

        Relic(String id, String name, char colorCode, OneColor color, Vec3 spawnPos, BlockPos cauldronPos) {
            this.id = id;
            this.name = name;
            this.colorCode = colorCode;
            this.color = color;
            this.spawnPos = spawnPos;
            this.cauldronPos = cauldronPos;
        }

        public static Relic getRelicByName(String name) {
            for (Relic relic : Relic.values()) {
                if (relic.id.toLowerCase().contains(name.toLowerCase())) return relic;
            }
            return Relic.NONE;
        }
    }

    public enum Dragon {
        GREEN( "Green", 'a', Color.MINECRAFT_GREEN.getColor(), new int[]{3, 2}),
        PURPLE("Purple", 'd', Color.MINECRAFT_LIGHT_PURPLE.getColor(), new int[]{0, 4}),
        BLUE("Blue",'b', Color.MINECRAFT_AQUA.getColor(), new int[]{1, 0}),
        ORANGE("Orange", '6', Color.MINECRAFT_GOLD.getColor(), new int[]{4, 3}),
        RED("Red", 'c', Color.MINECRAFT_RED.getColor(), new int[]{2, 1});

        public final String name;
        public final char colorCode;
        public final OneColor color;
        public final int[] prio;

        Dragon(String name, char colorCode, OneColor color, int[] prio) {
            this.name = name;
            this.colorCode = colorCode;
            this.color = color;
            this.prio = prio;
        }
    }

    public enum LocationEnums {
        SIMONSAYS(new BlockPos(108, 120, 94), 4, "At SS!", Pattern.compile("At (SS|Simon *Says)!", Pattern.CASE_INSENSITIVE), Class.HEALER, () -> ModConfig.locationsMessageSimonSays),
        EARLY_ENTER_2(new BlockPos(56, 109, 131), 2.5, "At Pre Enter 2!", Pattern.compile("At (2 *)*(pre enter|early *enter|EE) *(2 *)*!", Pattern.CASE_INSENSITIVE), Class.ARCHER, () -> ModConfig.locationsMessageEarlyEnter2),
        SAFE_SPOT_2(new BlockPos(48, 109, 121.988), 2.5, "At 2 Safespot!", Pattern.compile("At (2 *)*Safe *Spot *(2 *)*!", Pattern.CASE_INSENSITIVE), Class.ARCHER, () -> ModConfig.locationsMessageSafeSpot2),
        EARLY_ENTER_3(new BlockPos(1, 109, 104), 6, "At Pre Enter 3!", Pattern.compile("At (3 *)*(pre enter|early *enter|EE)( *3)*!", Pattern.CASE_INSENSITIVE), Class.HEALER, () -> ModConfig.locationsMessageEarlyEnter3),
        SAFE_SPOT_3(new BlockPos(18, 121, 93), 4, "At 3 Safespot!", Pattern.compile("At (3 *)*Safe *Spot *(3 *)*!", Pattern.CASE_INSENSITIVE), Class.HEALER, () -> ModConfig.locationsMessageSafeSpot3),
        CORE(new BlockPos(54, 115, 50), 2.5, "At Core!", Pattern.compile("At Core!", Pattern.CASE_INSENSITIVE), Class.MAGE, () -> ModConfig.locationsMessageCore),
        TUNNEL(new BlockPos(54, 115, 57), 4, "Inside Tunnel!", Pattern.compile("(Inside|At) (Goldor )*Tunnel!", Pattern.CASE_INSENSITIVE), Class.MAGE, () -> ModConfig.locationsMessageTunnel),
        MID(new BlockPos(54.5, 65, 76.5), 49, "At Mid!", Pattern.compile("At (Mid|Necron)!", Pattern.CASE_INSENSITIVE), Class.HEALER, () -> ModConfig.locationsMessageMid);

        public final BlockPos pos;
        public final double range;
        public final String message;
        public final Pattern pattern;
        public final Class clazz;
        public final Supplier<Boolean> configSupplier;

        LocationEnums(BlockPos pos, double range, String message, Pattern pattern, Class clazz, Supplier<Boolean> configSupplier) {
            this.pos = pos;
            this.range = range;
            this.message = message;
            this.pattern = pattern;
            this.clazz = clazz;
            this.configSupplier = configSupplier;
        }

        public boolean isAtLocation(BlockPos pos) {
            if (this.equals(SAFE_SPOT_2)) {
                return this.pos.distanceSq(pos.getX(), pos.getY(), pos.getZ()) < this.range && pos.getZ() <= 121.800;
            }
            return this.pos.distanceSq(pos.getX(), pos.getY(), pos.getZ()) < this.range;
        }

        public static LocationEnums getLocationEnumByMessage(String msg) {
            return Arrays.stream(LocationEnums.values()).filter(x -> x.pattern.matcher(msg).find()).findFirst().orElse(null);
        }
    }
}