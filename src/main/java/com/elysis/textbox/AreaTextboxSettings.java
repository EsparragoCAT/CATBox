package com.elysis.textbox;

/**
 * Datos del visual de la textbox de área de un jugador.
 * Se usa tanto en el cliente como en el servidor.
 */
public record AreaTextboxSettings(String name, String color, String sound, String icon, String position, float time, boolean blocking, int radius, float size, String boxTexture) {

    public static final AreaTextboxSettings DEFAULT = new AreaTextboxSettings(
            "",                          // nombre mostrado (vacío = nombre real del jugador)
            TextboxMod.BRAND_COLOR_HEX,  // color de marca (verde)
            "minecraft:ui.button.click", // sonido por defecto
            "",                          // icono vacío => cabeza del que habla
            "bottom",                    // posición
            3.0f,                        // segundos totales de animación
            false,                       // no bloqueante
            8,                           // radio en bloques
            1.0f,                        // tamaño (1.0 = 100%)
            ""                           // textura de caja (vacío = caja por defecto)
    );
}
