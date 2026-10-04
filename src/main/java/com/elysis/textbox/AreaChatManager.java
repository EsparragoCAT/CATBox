package com.elysis.textbox;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lado servidor. Guarda el toggle de "area chat" (por jugador) y la configuración
 * de textbox de cada jugador, y reenvía los mensajes de chat como textbox.
 */
public class AreaChatManager {

    private static final Set<UUID> ENABLED = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, AreaTextboxSettings> SETTINGS = new ConcurrentHashMap<>();

    public static boolean isEnabled(UUID uuid) {
        return ENABLED.contains(uuid);
    }

    public static void setEnabled(UUID uuid, boolean enabled) {
        if (enabled) {
            ENABLED.add(uuid);
        } else {
            ENABLED.remove(uuid);
        }
    }

    public static void putSettings(UUID uuid, AreaTextboxSettings settings) {
        if (settings != null) {
            SETTINGS.put(uuid, settings);
        }
    }

    public static void remove(UUID uuid) {
        ENABLED.remove(uuid);
        SETTINGS.remove(uuid);
    }

    /** Se ejecuta cuando un jugador manda un mensaje de chat. */
    public static void onServerChat(ServerChatEvent event) {
        ServerPlayer sender = event.getPlayer();
        if (!isEnabled(sender.getUUID())) return;

        // El mensaje NO se envía al chat normal, solo como textbox.
        event.setCanceled(true);

        AreaTextboxSettings settings = SETTINGS.get(sender.getUUID());
        if (settings == null) settings = AreaTextboxSettings.DEFAULT;

        // Nombre mostrado: el configurado o, si está vacío, el real del jugador.
        String name = (settings.name() == null || settings.name().isEmpty())
                ? sender.getGameProfile().getName()
                : settings.name();

        // Color del borde: se antepone "&#" para que el sistema de color lo detecte.
        String color = settings.color() == null ? "" : settings.color().trim().replaceFirst("^#", "");
        String speaker = color.matches("[0-9a-fA-F]{6}") ? ("&#" + color + name) : name;

        // Si no hay icono configurado, se usa la cabeza del que habla.
        String icon = settings.icon().isEmpty() ? "@" + sender.getGameProfile().getName() : settings.icon();

        TextboxPayload payload = new TextboxPayload(
                speaker,
                event.getRawText(),
                settings.sound(),
                icon,
                settings.position(),
                settings.time(),
                settings.blocking(),
                settings.size(),
                settings.boxTexture()
        );

        double radius = settings.radius();
        for (ServerPlayer target : sender.serverLevel().players()) {
            if (target.distanceTo(sender) <= radius) {
                PacketDistributor.sendToPlayer(target, payload);
            }
        }
    }

    /** Limpia el toggle y la configuración guardada cuando un jugador sale. */
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        remove(event.getEntity().getUUID());
    }
}
