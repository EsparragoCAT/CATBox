package com.elysis.textbox;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TextboxPayload(String speaker, String text, String soundId, String iconItem, String position, float time, boolean blocking, float size, String boxTexture) implements CustomPacketPayload {

    public static final Type<TextboxPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("textbox", "open_ui"));

    public static final StreamCodec<FriendlyByteBuf, TextboxPayload> CODEC = StreamCodec.of(
            (buf, value) -> {
                buf.writeUtf(value.speaker());
                buf.writeUtf(value.text());
                buf.writeUtf(value.soundId());
                buf.writeUtf(value.iconItem());
                buf.writeUtf(value.position());
                buf.writeFloat(value.time());
                buf.writeBoolean(value.blocking());
                buf.writeFloat(value.size());
                buf.writeUtf(value.boxTexture());
            },
            buf -> new TextboxPayload(
                    buf.readUtf(),
                    buf.readUtf(),
                    buf.readUtf(),
                    buf.readUtf(),
                    buf.readUtf(),
                    buf.readFloat(),
                    buf.readBoolean(),
                    buf.readFloat(),
                    buf.readUtf()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
