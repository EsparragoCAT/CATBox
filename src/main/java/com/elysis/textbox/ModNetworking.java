package com.elysis.textbox;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0.0");

        // Servidor -> Cliente: muestra una textbox.
        registrar.playToClient(
                TextboxPayload.TYPE,
                TextboxPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientTextboxHandler.handlePayload(payload))
        );

        // Servidor -> Cliente: informa si el jugador tiene permiso para el area chat.
        registrar.playToClient(
                AreaPermissionPayload.TYPE,
                AreaPermissionPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> AreaConfigClient.setAreaAllowed(payload.allowed()))
        );

        // Cliente -> Servidor: sincroniza la configuración de la textbox de área.
        registrar.playToServer(
                AreaConfigSyncPayload.TYPE,
                AreaConfigSyncPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) {
                        AreaChatManager.putSettings(player.getUUID(), payload.toSettings());
                    }
                })
        );

        // Cliente -> Servidor: activa/desactiva el area chat del jugador (solo con permiso).
        registrar.playToServer(
                AreaChatTogglePayload.TYPE,
                AreaChatTogglePayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player && TextboxPermissions.hasAreaPermission(player)) {
                        AreaChatManager.setEnabled(player.getUUID(), payload.enabled());
                    }
                })
        );
    }
}
