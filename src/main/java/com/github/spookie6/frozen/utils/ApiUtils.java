package com.github.spookie6.frozen.utils;

import com.github.spookie6.frozen.config.ModConfig;

import java.net.URL;

import static com.github.spookie6.frozen.Frozen.mc;

public class ApiUtils {
    public static String uuid = mc.getPlayerUsageSnooper().getUniqueID();
    public static String apiKey = ModConfig.apiKey;

//    public static boolean verifyKey() {
//        Thread thread = new Thread(() -> {
//            URL url = new URL("http://localhost:8080/verify")
//        });
//        thread.start();
//    }
}
