package com.elysis.textbox;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Paquete que envía el cliente al servidor con la configuración de su textbox de área.
 */
public record AreaConfigSyncPayload(String name, String color, String sound, String icon, String position, float time, boolean blocking, int radius, float size, String boxTexture) implements CustomPacketPayload {

    public static final Type<AreaConfigSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("textbox", "area_config_sync"));

    public static final StreamCodec<FriendlyByteBuf, AreaConfigSyncPayload> CODEC = StreamCodec.of(
            (buf, value) -> {
                buf.writeUtf(value.name());
                buf.writeUtf(value.color());
                buf.writeUtf(value.sound());
                buf.writeUtf(value.icon());
                buf.writeUtf(value.position());
                buf.writeFloat(value.time());
                buf.writeBoolean(value.blocking());
                buf.writeVarInt(value.radius());
                buf.writeFloat(value.size());
                buf.writeUtf(value.boxTexture());
            },
            buf -> new AreaConfigSyncPayload(
                    buf.readUtf(),
                    buf.readUtf(),
                    buf.readUtf(),
                    buf.readUtf(),
                    buf.readUtf(),
                    buf.readFloat(),
                    buf.readBoolean(),
                    buf.readVarInt(),
                    buf.readFloat(),
                    buf.readUtf()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public AreaTextboxSettings toSettings() {
        return new AreaTextboxSettings(name, color, sound, icon, position, time, blocking, radius, size, boxTexture);
    }
}
