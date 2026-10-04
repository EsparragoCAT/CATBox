package com.elysis.textbox;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * El servidor informa al cliente si tiene permiso para usar el area chat y su interfaz.
 */
public record AreaPermissionPayload(boolean allowed) implements CustomPacketPayload {

    public static final Type<AreaPermissionPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("textbox", "area_permission"));

    public static final StreamCodec<FriendlyByteBuf, AreaPermissionPayload> CODEC = StreamCodec.of(
            (buf, value) -> buf.writeBoolean(value.allowed()),
            buf -> new AreaPermissionPayload(buf.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
