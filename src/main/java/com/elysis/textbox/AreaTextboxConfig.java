package com.elysis.textbox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuración personal del jugador para su textbox de área.
 * Se guarda en la carpeta "config" como textbox-area.json.
 */
public class AreaTextboxConfig {

    public String name = "";            // nombre mostrado (vacío = nombre real del jugador)
    public String color = TextboxMod.BRAND_COLOR_HEX;     // color de marca (verde)
    public String sound = "minecraft:ui.button.click";
    public String icon = "";            // vacío => se usa la cabeza del que habla
    public String position = "bottom";  // "bottom" o "top"
    public double time = 3.0;           // segundos totales de la animación
    public boolean blocking = false;
    public int radius = 8;              // bloques (mínimo 4, máximo 24)
    public double size = 1.0;           // tamaño del box (1.0 = 100%)
    public String boxTexture = "";      // URL de la textura de caja (64x64, 9-slice); vacío = por defecto

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static AreaTextboxConfig INSTANCE = new AreaTextboxConfig();
    private static boolean loaded = false;

    public static AreaTextboxConfig get() {
        if (!loaded) load();
        return INSTANCE;
    }

    public static synchronized void load() {
        loaded = true;
        Path dir = FMLPaths.CONFIGDIR.get();
        Path file = dir.resolve("textbox-area.json");
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file)) {
                AreaTextboxConfig parsed = GSON.fromJson(reader, AreaTextboxConfig.class);
                if (parsed != null) {
                    INSTANCE = parsed;
                }
            } catch (Exception e) {
                TextboxMod.LOGGER.error("No se pudo leer textbox-area.json", e);
            }
        }
        INSTANCE.sanitize();
    }

    public void save() {
        sanitize();
        try {
            Path dir = FMLPaths.CONFIGDIR.get();
            Files.createDirectories(dir);
            Path file = dir.resolve("textbox-area.json");
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(this, writer);
            }
        } catch (Exception e) {
            TextboxMod.LOGGER.error("No se pudo guardar textbox-area.json", e);
        }
    }

    /** Corrige valores inválidos o incompletos. */
    private void sanitize() {
        if (name == null) name = "";
        if (color == null) color = TextboxMod.BRAND_COLOR_HEX;
        else {
            color = color.trim().replaceFirst("^#", "");
            if (!color.matches("[0-9a-fA-F]{6}")) color = TextboxMod.BRAND_COLOR_HEX;
        }
        if (sound == null) sound = "minecraft:ui.button.click";
        if (icon == null) icon = "";
        if (position == null || (!position.equalsIgnoreCase("top") && !position.equalsIgnoreCase("bottom"))) {
            position = "bottom";
        }
        if (Double.isNaN(time) || time <= 0.0) {
            time = 3.0;
        } else {
            time = Math.max(0.5, Math.min(30.0, time));
        }
        if (radius == 0) {
            radius = 8;
        } else {
            radius = Math.max(4, Math.min(24, radius));
        }
        size = TextboxMod.snapSize((float) size);
    }
}
