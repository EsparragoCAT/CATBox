package com.elysis.textbox;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EventBusSubscriber(modid = TextboxMod.MODID, value = net.neoforged.api.distmarker.Dist.CLIENT)
public class TextboxUI extends Screen {

    // --- Tamaño y layout de la caja ---
    private static final float TARGET_SCALE = 3.0f;
    private static final int BOX_HEIGHT = 50;
    private static final int BOX_MAX_WIDTH = 500;
    private static final int BOX_H_MARGIN = 60;
    // Anclaje al HUD (en píxeles de la interfaz)
    private static final int HEALTH_BAR_FROM_BOTTOM = 39; // distancia desde abajo hasta el top de la vida
    private static final int BOSS_BAR_BOTTOM = 17;        // distancia desde arriba hasta el final de la bossbar
    private static final int HUD_GAP = 3;                 // separación en píxeles respecto al HUD
    private static final int OVERLAY_CLOSE_TICKS = 80;
    private static final int BACKGROUND_COLOR = 0xEE0D0D14;
    private static final int SPEAKER_COLOR = 0xFFFFFF;
    private static final int SEPARATOR_COLOR = 0x33FFFFFF;
    private static final int DEFAULT_BORDER = TextboxMod.BRAND_COLOR;
    private static final int BOX_TEXTURE_SIZE = 64;
    private static final int BOX_TEXTURE_SLICE = 8;

    private static boolean isOverlayActive = false;
    private static FormattedData overlaySpeakerData;
    private static FormattedData overlayTextData;
    private static String overlaySound = "";
    private static TextboxIcon overlayIcon = TextboxIcon.empty();
    private static String overlayPosition = "bottom";
    private static float overlaySizeScale = 1.0f;
    private static String overlayBoxTexture = "";
    private static int overlayTicks = 0;
    private static int maxTicks = 0;
    private static float overlayTotalTypingTicks = 0;
    private static int overlayLastChars = 0;

    private static boolean wasSkipPressed = false;

    private final FormattedData speakerData;
    private final FormattedData textData;
    private final String soundId;
    private final TextboxIcon iconStack;
    private final String position;
    private final float sizeScale;
    private final String boxTexture;
    private final float totalTypingTicks;
    private int ticks = 0;
    private int lastCharsShown = 0;

    public TextboxUI(String speaker, String text, String soundId, String iconItem, String position, float timeInSeconds, float size, String boxTexture) {
        super(Component.literal("Textbox"));
        this.speakerData = new FormattedData(speaker);
        this.textData = new FormattedData(text);
        this.soundId = soundId;
        this.iconStack = parseIcon(iconItem);
        this.position = position;
        this.sizeScale = size;
        this.boxTexture = boxTexture == null ? "" : boxTexture;
        this.totalTypingTicks = timeInSeconds * 20.0f;
    }

    public static void showOverlay(String speaker, String text, String soundId, String iconItem, String position, float timeInSeconds, float size, String boxTexture) {
        overlaySpeakerData = new FormattedData(speaker);
        overlayTextData = new FormattedData(text);
        overlaySound = soundId;
        overlayIcon = parseIcon(iconItem);
        overlayPosition = position;
        overlaySizeScale = size;
        overlayBoxTexture = boxTexture == null ? "" : boxTexture;
        overlayTicks = 0;
        overlayLastChars = 0;
        overlayTotalTypingTicks = timeInSeconds * 20.0f;
        maxTicks = (int) overlayTotalTypingTicks + OVERLAY_CLOSE_TICKS; // Cierra 4 segundos después de terminar
        isOverlayActive = true;
    }

    private static TextboxIcon parseIcon(String iconItem) {
        if (iconItem == null || iconItem.isEmpty()) return TextboxIcon.empty();
        if (iconItem.startsWith("http://") || iconItem.startsWith("https://")) {
            return TextboxIcon.url(iconItem);
        }
        if (iconItem.startsWith("@")) {
            String playerName = iconItem.substring(1);
            ItemStack head = new ItemStack(Items.PLAYER_HEAD);
            head.set(net.minecraft.core.component.DataComponents.PROFILE, new ResolvableProfile(new GameProfile(Util.NIL_UUID, playerName)));
            return TextboxIcon.item(head);
        }
        ResourceLocation itemLoc = ResourceLocation.tryParse(iconItem);
        if (itemLoc != null && BuiltInRegistries.ITEM.containsKey(itemLoc)) {
            return TextboxIcon.item(BuiltInRegistries.ITEM.get(itemLoc).getDefaultInstance());
        }
        return TextboxIcon.empty();
    }

    @Override
    public void tick() {
        super.tick();
        this.ticks++;
        handleSkip(false);
        int charsToShow = getCharsToShow(this.textData.text.length(), this.ticks, this.totalTypingTicks);
        if (charsToShow > this.lastCharsShown) {
            playTypingSound(this.soundId);
            this.lastCharsShown = charsToShow;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        drawBox(guiGraphics, this.speakerData, this.textData, this.iconStack, this.position, this.ticks, this.totalTypingTicks, this.sizeScale, this.boxTexture);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (isOverlayActive) {
            overlayTicks++;
            handleSkip(true);
            int charsToShow = getCharsToShow(overlayTextData.text.length(), overlayTicks, overlayTotalTypingTicks);
            if (charsToShow > overlayLastChars) {
                playTypingSound(overlaySound);
                overlayLastChars = charsToShow;
            }
            if (overlayTicks > maxTicks) isOverlayActive = false;
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (isOverlayActive && Minecraft.getInstance().screen == null) {
            drawBox(event.getGuiGraphics(), overlaySpeakerData, overlayTextData, overlayIcon, overlayPosition, overlayTicks, overlayTotalTypingTicks, overlaySizeScale, overlayBoxTexture);
        }
    }

    // Dibuja la textbox encima de pantallas abiertas (chat, menú de pausa, etc.)
    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (isOverlayActive && Minecraft.getInstance().screen != null) {
            drawBox(event.getGuiGraphics(), overlaySpeakerData, overlayTextData, overlayIcon, overlayPosition, overlayTicks, overlayTotalTypingTicks, overlaySizeScale, overlayBoxTexture);
        }
    }

    private static void handleSkip(boolean isOverlay) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) return;
        boolean isVPressed = InputConstants.isKeyDown(mc.getWindow().getWindow(), ClientKeybinds.SKIP_ANIMATION.getKey().getValue());
        if (isVPressed && !wasSkipPressed) {
            if (isOverlay) {
                if (overlayTicks < overlayTotalTypingTicks) overlayTicks = (int) overlayTotalTypingTicks;
            } else {
                if (mc.screen instanceof TextboxUI ui) {
                    if (ui.ticks < ui.totalTypingTicks) ui.ticks = (int) ui.totalTypingTicks;
                }
            }
        }
        wasSkipPressed = isVPressed;
    }

    private static int getCharsToShow(int totalChars, int currentTicks, float typingTicks) {
        if (typingTicks <= 0 || currentTicks >= typingTicks) return totalChars;
        float charsPerTick = totalChars / typingTicks;
        return Math.min(totalChars, (int) (currentTicks * charsPerTick));
    }

    private static void drawBox(GuiGraphics guiGraphics, FormattedData speakerData, FormattedData textData,
                                TextboxIcon icon, String position, int currentTicks, float typingTicks, float sizeScale, String boxTexture) {
        Minecraft mc = Minecraft.getInstance();
        float actualScale = (float) mc.getWindow().getGuiScale();
        float scaleMultiplier = TARGET_SCALE / actualScale;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 500f);
        guiGraphics.pose().scale(scaleMultiplier, scaleMultiplier, 1.0f);

        int virtualWidth = (int) (mc.getWindow().getScreenWidth() / TARGET_SCALE);
        int virtualHeight = (int) (mc.getWindow().getScreenHeight() / TARGET_SCALE);

        int boxWidth = Math.min(virtualWidth - BOX_H_MARGIN, BOX_MAX_WIDTH);
        int startX = (virtualWidth - boxWidth) / 2;
        int startY;
        if (position.equalsIgnoreCase("top")) {
            startY = Math.round((BOSS_BAR_BOTTOM + HUD_GAP) * actualScale / TARGET_SCALE);
        } else {
            startY = Math.round(virtualHeight - (HEALTH_BAR_FROM_BOTTOM + HUD_GAP) * actualScale / TARGET_SCALE - BOX_HEIGHT);
        }

        // Escala el box (y todo su contenido) alrededor de su centro, manteniendo centrado y posición.
        float centerX = startX + boxWidth / 2.0f;
        float centerY = startY + BOX_HEIGHT / 2.0f;
        guiGraphics.pose().translate(centerX, centerY, 0);
        guiGraphics.pose().scale(sizeScale, sizeScale, 1.0f);
        guiGraphics.pose().translate(-centerX, -centerY, 0);

        boolean hasIcon = !icon.isEmpty();
        int portraitSize = hasIcon ? BOX_HEIGHT : 0;
        int borderColor = speakerData.borderColor;

        // Sombra suave detrás de la caja
        guiGraphics.fill(startX + 2, startY + 2, startX + boxWidth + 2, startY + BOX_HEIGHT + 2, 0x55000000);

        ResourceLocation boxTex = (boxTexture == null || boxTexture.isEmpty()) ? null : ImageIconCache.get(boxTexture);
        if (boxTex != null) {
            drawBoxTexture(guiGraphics, boxTex, startX, startY, boxWidth, BOX_HEIGHT);
        } else {
            guiGraphics.fill(startX, startY, startX + boxWidth, startY + BOX_HEIGHT, BACKGROUND_COLOR);
            guiGraphics.fill(startX - 1, startY - 1, startX + boxWidth + 1, startY, borderColor);
            guiGraphics.fill(startX - 1, startY + BOX_HEIGHT, startX + boxWidth + 1, startY + BOX_HEIGHT + 1, borderColor);
            guiGraphics.fill(startX - 1, startY, startX, startY + BOX_HEIGHT, borderColor);
            guiGraphics.fill(startX + boxWidth, startY, startX + boxWidth + 1, startY + BOX_HEIGHT, borderColor);
        }

        if (hasIcon) {
            guiGraphics.fill(startX + portraitSize, startY, startX + portraitSize + 1, startY + BOX_HEIGHT, borderColor);
            icon.render(guiGraphics, startX, startY, BOX_HEIGHT);
        }

        int innerX = startX + portraitSize + 10;
        int innerY = startY + 5;

        if (!speakerData.text.isEmpty()) {
            guiGraphics.drawString(mc.font, Component.literal(speakerData.text), innerX, innerY, SPEAKER_COLOR, true);
            innerY += 12;
            guiGraphics.fill(innerX, innerY, startX + boxWidth - 10, innerY + 1, SEPARATOR_COLOR);
            innerY += 4;
        }

        int charsToShow = getCharsToShow(textData.text.length(), currentTicks, typingTicks);

        guiGraphics.drawWordWrap(mc.font, Component.literal(textData.text.substring(0, charsToShow)), innerX, innerY, (startX + boxWidth) - innerX - 10, SPEAKER_COLOR);

        // Pista "Pulsa X para saltar" mientras el texto se está escribiendo
        if (charsToShow < textData.text.length()) {
            String keyName = ClientKeybinds.SKIP_ANIMATION.getTranslatedKeyMessage().getString();
            Component hint = Component.translatable("textbox.hint.skip", keyName);
            guiGraphics.drawString(mc.font, hint, startX + boxWidth - mc.font.width(hint.getString()) - 4, startY + BOX_HEIGHT - 11, 0xFFAAAAAA);
        }

        guiGraphics.pose().popPose();
    }

    private static void drawBoxTexture(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width, int height) {
        int s = BOX_TEXTURE_SLICE;
        int t = BOX_TEXTURE_SIZE;
        // Esquinas
        guiGraphics.blit(texture, x, y, 0, 0, s, s, t, t);
        guiGraphics.blit(texture, x + width - s, y, t - s, 0, s, s, t, t);
        guiGraphics.blit(texture, x, y + height - s, 0, t - s, s, s, t, t);
        guiGraphics.blit(texture, x + width - s, y + height - s, t - s, t - s, s, s, t, t);
        // Bordes (se estiran)
        int cw = width - 2 * s;
        int ch = height - 2 * s;
        guiGraphics.blit(texture, x + s, y, s, 0, cw, s, t, t);
        guiGraphics.blit(texture, x + s, y + height - s, s, t - s, cw, s, t, t);
        guiGraphics.blit(texture, x, y + s, 0, s, s, ch, t, t);
        guiGraphics.blit(texture, x + width - s, y + s, t - s, s, s, ch, t, t);
        // Centro (se estira)
        guiGraphics.blit(texture, x + s, y + s, s, s, cw, ch, t, t);
    }

    private static void playTypingSound(String soundId) {
        if (soundId == null || soundId.isEmpty() || soundId.equals("minecraft:air")) return;
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation soundLoc = ResourceLocation.tryParse(soundId);
        if (soundLoc != null && mc.level != null) {
            float pitch = 0.9F + (mc.level.random.nextFloat() * 0.2F);
            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(soundLoc), pitch));
        }
    }

    public static class FormattedData {
        private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");
        private static final Pattern CODE_PATTERN = Pattern.compile("&([0-9a-fA-F])");

        public final String text;
        public final int borderColor;

        public FormattedData(String raw) {
            if (raw == null || raw.isEmpty()) { this.text = ""; this.borderColor = DEFAULT_BORDER; return; }
            String processed = raw;
            int extractedColor = -1;

            Matcher hexMatcher = HEX_PATTERN.matcher(processed);
            if (hexMatcher.find()) {
                extractedColor = 0xFF000000 | (int) Long.parseLong(hexMatcher.group(1), 16);
                StringBuffer sb = new StringBuffer();
                Matcher m = HEX_PATTERN.matcher(processed);
                while (m.find()) {
                    StringBuilder rep = new StringBuilder("§x");
                    for (char c : m.group(1).toCharArray()) rep.append("§").append(c);
                    m.appendReplacement(sb, rep.toString());
                }
                m.appendTail(sb);
                processed = sb.toString();
            }

            if (extractedColor == -1) {
                Matcher codeMatcher = CODE_PATTERN.matcher(processed);
                if (codeMatcher.find()) extractedColor = getMcColorHex(codeMatcher.group(1).toLowerCase().charAt(0));
            }

            this.text = processed.replace("&", "§");
            this.borderColor = (extractedColor != -1) ? extractedColor : DEFAULT_BORDER;
        }

        private static int getMcColorHex(char code) {
            return switch (code) {
                case '0' -> 0xFF000000; case '1' -> 0xFF0000AA; case '2' -> 0xFF00AA00; case '3' -> 0xFF00AAAA;
                case '4' -> 0xFFAA0000; case '5' -> 0xFFAA00AA; case '6' -> 0xFFFFAA00; case '7' -> 0xFFAAAAAA;
                case '8' -> 0xFF555555; case '9' -> 0xFF5555FF; case 'a' -> 0xFF55FF55; case 'b' -> 0xFF55FFFF;
                case 'c' -> 0xFFFF5555; case 'd' -> 0xFFFF55FF; case 'e' -> 0xFFFFFF55; case 'f' -> 0xFFFFFFFF;
                default -> DEFAULT_BORDER;
            };
        }
    }
}
