package com.elysis.textbox;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Lado cliente. Abre la pantalla de configuración con la tecla y
 * sincroniza la configuración con el servidor.
 */
@EventBusSubscriber(modid = TextboxMod.MODID, value = Dist.CLIENT)
public class AreaConfigClient {

    private static boolean areaChatEnabled = false;
    private static boolean areaAllowed = false;

    public static boolean isAreaChatEnabled() {
        return areaChatEnabled;
    }

    public static void setAreaAllowed(boolean allowed) {
        areaAllowed = allowed;
    }

    public static void toggleAreaChat() {
        areaChatEnabled = !areaChatEnabled;
        PacketDistributor.sendToServer(new AreaChatTogglePayload(areaChatEnabled));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        while (ClientKeybinds.OPEN_AREA_CONFIG.consumeClick()) {
            if (mc.screen == null) {
                if (areaAllowed) {
                    mc.setScreen(new AreaConfigScreen());
                } else {
                    mc.player.displayClientMessage(Component.translatable("textbox.gui.no_permission"), false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        sendConfigToServer();
        preloadIcon();
    }

    @SubscribeEvent
    public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ImageIconCache.clear();
        areaChatEnabled = false;
        areaAllowed = false;
    }

    /** Precarga las imágenes (icono y textura de caja) si son URLs. */
    private static void preloadIcon() {
        AreaTextboxConfig config = AreaTextboxConfig.get();
        String icon = config.icon;
        if (icon != null && (icon.startsWith("http://") || icon.startsWith("https://"))) {
            ImageIconCache.get(icon);
        }
        String boxTexture = config.boxTexture;
        if (boxTexture != null && (boxTexture.startsWith("http://") || boxTexture.startsWith("https://"))) {
            ImageIconCache.get(boxTexture);
        }
    }

    public static void sendConfigToServer() {
        AreaTextboxConfig config = AreaTextboxConfig.get();
        PacketDistributor.sendToServer(new AreaConfigSyncPayload(
                config.name,
                config.color,
                config.sound,
                config.icon,
                config.position,
                (float) config.time,
                config.blocking,
                config.radius,
                (float) config.size,
                config.boxTexture
        ));
    }
}
