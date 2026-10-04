package com.elysis.textbox;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

/**
 * Permisos del mod. El nodo "textbox.area" se puede dar con LuckPerms.
 * Por defecto lo tienen los jugadores con nivel de OP 2 o superior.
 */
public class TextboxPermissions {

    public static final PermissionNode<Boolean> AREA = new PermissionNode<>(
            TextboxMod.MODID,
            "area",
            PermissionTypes.BOOLEAN,
            (player, uuid, context) -> player != null && player.hasPermissions(2)
    );

    public static void registerPermissionNodes(PermissionGatherEvent.Nodes event) {
        event.addNodes(AREA);
    }

    public static boolean hasAreaPermission(ServerPlayer player) {
        try {
            return PermissionAPI.getPermission(player, AREA);
        } catch (Exception e) {
            // Fallback si el handler de permisos (p. ej. LuckPerms) falla o no está listo.
            return player.hasPermissions(2);
        }
    }

    /** Al entrar, informa al cliente si tiene permiso para abrir la interfaz. */
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new AreaPermissionPayload(hasAreaPermission(player)));
        }
    }
}
