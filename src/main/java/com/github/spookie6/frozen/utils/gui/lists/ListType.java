package com.github.spookie6.frozen.utils.gui.lists;

import java.io.File;

import static com.github.spookie6.frozen.Frozen.mc;

public enum ListType {
    LOCATIONAL_MESSAGE(new File(mc.mcDataDir, "config/frozen/location_messages"));

    public File configFile;

    ListType(File configFile) {
        this.configFile = configFile;
    }
}
