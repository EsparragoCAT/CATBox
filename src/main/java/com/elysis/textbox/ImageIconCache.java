package com.elysis.textbox;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Descarga imágenes desde una URL en segundo plano y las convierte en texturas,
 * guardándolas en caché para no volver a descargarlas.
 */
@OnlyIn(Dist.CLIENT)
public class ImageIconCache {

    private static final int MAX_BYTES = 2 * 1024 * 1024; // 2 MB
    private static final int MAX_DIMENSION = 1024;

    private static final Map<String, ResourceLocation> CACHE = new ConcurrentHashMap<>();
    private static final Set<String> LOADING = ConcurrentHashMap.newKeySet();
    private static final AtomicLong IDS = new AtomicLong();

    /** Devuelve la textura si ya está cargada; si no, empieza a cargarla y devuelve null. */
    public static ResourceLocation get(String url) {
        ResourceLocation cached = CACHE.get(url);
        if (cached != null) return cached;
        if (!LOADING.contains(url)) {
            startLoad(url);
        }
        return null;
    }

    /** Libera todas las texturas en caché. Debe llamarse en el hilo de render. */
    public static void clear() {
        Minecraft mc = Minecraft.getInstance();
        for (ResourceLocation loc : CACHE.values()) {
            mc.getTextureManager().release(loc);
        }
        CACHE.clear();
        LOADING.clear();
    }

    private static void startLoad(String url) {
        LOADING.add(url);
        CompletableFuture.runAsync(() -> {
            NativeImage image = null;
            try {
                byte[] data = download(url);
                image = NativeImage.read(new ByteArrayInputStream(data));
                if (image.getWidth() > MAX_DIMENSION || image.getHeight() > MAX_DIMENSION) {
                    throw new IOException("imagen demasiado grande (" + image.getWidth() + "x" + image.getHeight() + ")");
                }
                NativeImage toUpload = image;
                image = null; // la propiedad pasa al hilo de render
                Minecraft.getInstance().execute(() -> upload(url, toUpload));
            } catch (Throwable t) {
                if (image != null) image.close();
                LOADING.remove(url);
                TextboxMod.LOGGER.warn("No se pudo cargar la imagen de {}: {}", url, t.getMessage());
            }
        });
    }

    private static void upload(String url, NativeImage image) {
        try {
            DynamicTexture texture = new DynamicTexture(image);
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("textbox", "url_icon_" + IDS.incrementAndGet());
            Minecraft.getInstance().getTextureManager().register(loc, texture);
            CACHE.put(url, loc);
        } catch (Throwable t) {
            image.close();
            TextboxMod.LOGGER.warn("No se pudo crear la textura de {}: {}", url, t.getMessage());
        } finally {
            LOADING.remove(url);
        }
    }

    private static byte[] download(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Textbox Mod)");
        connection.setInstanceFollowRedirects(true);
        try {
            int code = connection.getResponseCode();
            if (code != 200) {
                throw new IOException("HTTP " + code);
            }
            int length = connection.getContentLength();
            if (length > MAX_BYTES) {
                throw new IOException("imagen demasiado grande");
            }
            try (InputStream in = connection.getInputStream()) {
                byte[] data = in.readAllBytes();
                if (data.length > MAX_BYTES) {
                    throw new IOException("imagen demasiado grande");
                }
                return data;
            }
        } finally {
            connection.disconnect();
        }
    }
}
