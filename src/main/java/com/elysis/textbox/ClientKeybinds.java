package com.elysis.textbox;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

/**
 * Aquí se registran las teclas del mod para que aparezcan en el menú "Controles".
 */
@EventBusSubscriber(modid = TextboxMod.MODID, value = Dist.CLIENT)
public class ClientKeybinds {

    // Categoría que se ve en "Controles". La clave se traduce en el archivo de idioma.
    public static final String CATEGORY = "key.categories.textbox";

    // Tecla para saltar la animación del texto (por defecto la V).
    public static final KeyMapping SKIP_ANIMATION = new KeyMapping(
            "key.textbox.skip",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );

    // Tecla para abrir la configuración de la textbox de área (por defecto la B).
    public static final KeyMapping OPEN_AREA_CONFIG = new KeyMapping(
            "key.textbox.area_config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            CATEGORY
    );

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SKIP_ANIMATION);
        event.register(OPEN_AREA_CONFIG);
    }
}
