package dev.frozencloud.frozen.utils.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static <T> void saveList(File file, List<T> list) {
        try (Writer writer = new FileWriter(file)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(list, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static <T> List<T> loadList(File file, Type type) {
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            return new Gson().fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}