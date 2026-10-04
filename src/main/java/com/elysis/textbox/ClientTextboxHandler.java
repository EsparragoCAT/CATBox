package com.elysis.textbox;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientTextboxHandler {

    public static void handlePayload(TextboxPayload payload) {
        if (payload.blocking()) {
            Minecraft.getInstance().setScreen(new TextboxUI(
                    payload.speaker(),
                    payload.text(),
                    payload.soundId(),
                    payload.iconItem(),
                    payload.position(),
                    payload.time(),
                    payload.size(),
                    payload.boxTexture()
            ));
        } else {
            TextboxUI.showOverlay(
                    payload.speaker(),
                    payload.text(),
                    payload.soundId(),
                    payload.iconItem(),
                    payload.position(),
                    payload.time(),
                    payload.size(),
                    payload.boxTexture()
            );
        }
    }
}
