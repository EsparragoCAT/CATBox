package com.elysis.textbox;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

/**
 * Punto de entrada del lado cliente. No se carga en servidores dedicados.
 */
@Mod(value = TextboxMod.MODID, dist = Dist.CLIENT)
public class TextboxClient {
    public TextboxClient() {
        AreaTextboxConfig.load();
    }
}
