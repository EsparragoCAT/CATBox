package com.elysis.textbox;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Icono de una textbox: puede ser un ítem (o cabeza) o una imagen cargada desde una URL.
 */
@OnlyIn(Dist.CLIENT)
public class TextboxIcon {

    private final ItemStack item;   // icono de ítem (EMPTY si no lo hay)
    private final String url;       // URL de imagen (null si no lo hay)

    private TextboxIcon(ItemStack item, String url) {
        this.item = item;
        this.url = url;
    }

    public static TextboxIcon item(ItemStack item) {
        return new TextboxIcon(item, null);
    }

    public static TextboxIcon url(String url) {
        return new TextboxIcon(ItemStack.EMPTY, url);
    }

    public static TextboxIcon empty() {
        return new TextboxIcon(ItemStack.EMPTY, null);
    }

    public boolean isEmpty() {
        return url == null && item.isEmpty();
    }

    /**
     * Dibuja el icono dentro del recuadro (boxX, boxY) de tamaño boxSize.
     * Las imágenes llenan el recuadro; los ítems mantienen su tamaño (16x16 escalado x2).
     * Si es una imagen que aún se está descargando, no dibuja nada hasta que esté lista.
     */
    public void render(GuiGraphics guiGraphics, int boxX, int boxY, int boxSize) {
        if (url != null) {
            ResourceLocation texture = ImageIconCache.get(url);
            if (texture != null) {
                int margin = 2;
                int size = boxSize - margin * 2;
                guiGraphics.blit(texture, boxX + margin, boxY + margin, 0, 0, size, size, size, size);
            }
        } else if (!item.isEmpty()) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(boxX + 9, boxY + 9, 0);
            guiGraphics.pose().scale(2.0f, 2.0f, 1.0f);
            guiGraphics.renderItem(item, 0, 0);
            guiGraphics.pose().popPose();
        }
    }
}
