package dev.frozencloud.frozen.utils.skyblock;

import java.util.Arrays;

public enum Island {
    SingePlayer("Singleplayer"),
    PrivateIsland("Private Island"),
    Garden("Garden"),
    SpiderDen("Spider's Den"),
    CrimsonIsle("Crimson Isle"),
    TheEnd("The End"),
    GoldMine("Gold Mine"),
    DeepCaverns("Deep Caverns"),
    DwarvenMines("Dwarven Mines"),
    CrystalHollows("Crystal Hollows"),
    FarmingIsland("The Farming Islands"),
    ThePark("The Park"),
    Dungeon("Catacombs"),
    DungeonHub("Dungeon Hub"),
    Hub("Hub"),
    DarkAuction("Dark Auction"),
    JerryWorkshop("Jerry's Workshop"),
    Kuudra("Kuudra"),
    Mineshaft("Mineshaft"),
    Rift("The Rift"),
    BackwaterBayou("Backwater Bayou"),
    Unknown("(Unknown)");

    private Island(String island) {
        this.island = island;
    }

    private String island;

    public String toString() {
        return island;
    }

    public boolean isArea(Island area) {
        if (this == SingePlayer) return true;
        return this.equals(area);
    }

    public boolean isNotArea(Island area) {
        if (this == SingePlayer) return false;
        return !this.isArea(area);
    }

    public static Island findMatch(String match) {
        return Arrays.stream(Island.values()).filter(x -> match.contains(x.toString())).findFirst().orElse(Island.Unknown);
    }
}
