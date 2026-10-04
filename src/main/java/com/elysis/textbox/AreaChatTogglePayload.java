package com.elysis.textbox;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Paquete que envía el cliente al servidor para activar/desactivar su area chat.
 */
public record AreaChatTogglePayload(boolean enabled) implements CustomPacketPayload {

    public static final Type<AreaChatTogglePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("textbox", "area_chat_toggle"));

    public static final StreamCodec<FriendlyByteBuf, AreaChatTogglePayload> CODEC = StreamCodec.of(
            (buf, value) -> buf.writeBoolean(value.enabled()),
            buf -> new AreaChatTogglePayload(buf.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
