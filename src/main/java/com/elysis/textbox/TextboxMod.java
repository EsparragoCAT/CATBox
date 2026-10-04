package com.elysis.textbox;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod(TextboxMod.MODID)
public class TextboxMod {
    public static final String MODID = "textbox";
    public static final Logger LOGGER = LogUtils.getLogger();

    /** Color de marca (verde CATBox). */
    public static final int BRAND_COLOR = 0xFF22C55E;
    public static final String BRAND_COLOR_HEX = "22C55E";

    /** Tamaños disponibles del textbox (en porcentaje). */
    public static final int[] SIZE_PERCENTS = {100, 90, 80, 70};

    public TextboxMod(IEventBus modEventBus) {
        modEventBus.addListener(ModNetworking::register);
        NeoForge.EVENT_BUS.addListener(TextboxPermissions::registerPermissionNodes);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(AreaChatManager::onServerChat);
        NeoForge.EVENT_BUS.addListener(AreaChatManager::onPlayerLogout);
        NeoForge.EVENT_BUS.addListener(TextboxPermissions::onPlayerLogin);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        TextboxCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    public static float percentToScale(int percent) {
        return percent / 100.0f;
    }

    /** Ajusta una escala al tamaño permitido más cercano. */
    public static float snapSize(float scale) {
        float best = 1.0f;
        float bestDiff = Float.MAX_VALUE;
        for (int p : SIZE_PERCENTS) {
            float s = p / 100.0f;
            float d = Math.abs(scale - s);
            if (d < bestDiff) {
                bestDiff = d;
                best = s;
            }
        }
        return best;
    }
}
