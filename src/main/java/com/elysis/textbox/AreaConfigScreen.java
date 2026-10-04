package com.elysis.textbox;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;

/**
 * Pantalla para editar el visual de la textbox de área del jugador.
 */
public class AreaConfigScreen extends Screen {

    private static final String[] POSITIONS = {"bottom", "top"};
    private static final String[] LABEL_KEYS = {
            "textbox.config.name", "textbox.config.color", "textbox.config.sound", "textbox.config.icon",
            "textbox.config.box_texture",
            "textbox.config.position", "textbox.config.blocking", "textbox.config.radius", "textbox.config.time", "textbox.config.size",
            "textbox.config.area"
    };

    private static final double TIME_STEP = 0.5;
    private static final double TIME_MIN = 0.5;
    private static final double TIME_MAX = 30.0;

    private static final int TOP_MARGIN = 24;
    private static final int BOTTOM_MARGIN = 8;
    private static final int ROW_HEIGHT = 24;
    private static final int CONTENT_HEIGHT = 12 * ROW_HEIGHT + 4; // alto total del contenido

    private final AreaTextboxConfig config;

    private String currentName;
    private String currentColor;
    private String currentSound;
    private String currentIcon;
    private String currentBoxTexture;
    private int positionIndex;
    private int radius;
    private double time;
    private boolean blocking;
    private int sizeIndex;

    private int contentTop;
    private int fieldX;
    private int fieldW;
    private int scroll;

    public AreaConfigScreen() {
        super(Component.translatable("textbox.config.title"));
        this.config = AreaTextboxConfig.get();
        this.currentName = config.name;
        this.currentColor = config.color;
        this.currentSound = config.sound;
        this.currentIcon = config.icon;
        this.currentBoxTexture = config.boxTexture;
        this.positionIndex = config.position.equalsIgnoreCase("top") ? 1 : 0;
        this.radius = config.radius;
        this.time = config.time;
        this.blocking = config.blocking;
        this.sizeIndex = sizeIndexFor((float) config.size);
    }

    @Override
    protected void init() {
        this.fieldX = this.width / 2 - 10;
        this.fieldW = 170;

        int usableHeight = Math.max(60, this.height - TOP_MARGIN - BOTTOM_MARGIN);
        int maxScroll = Math.max(0, CONTENT_HEIGHT - usableHeight);
        this.scroll = Math.max(0, Math.min(this.scroll, maxScroll));
        this.contentTop = (maxScroll == 0)
                ? TOP_MARGIN + (usableHeight - CONTENT_HEIGHT) / 2
                : TOP_MARGIN - this.scroll;

        int y = this.contentTop;

        EditBox nameField = new EditBox(this.font, fieldX, y, fieldW, 20, Component.translatable("textbox.config.name"));
        nameField.setMaxLength(100);
        nameField.setValue(currentName);
        nameField.setResponder(value -> currentName = value);
        nameField.setTooltip(Tooltip.create(Component.translatable("textbox.config.tooltip.name")));
        this.addRenderableWidget(nameField);
        y += ROW_HEIGHT;

        EditBox colorField = new EditBox(this.font, fieldX, y, fieldW - 24, 20, Component.translatable("textbox.config.color"));
        colorField.setMaxLength(7);
        colorField.setValue(currentColor);
        colorField.setResponder(value -> currentColor = value);
        colorField.setTooltip(Tooltip.create(Component.translatable("textbox.config.tooltip.color")));
        this.addRenderableWidget(colorField);
        y += ROW_HEIGHT;

        EditBox soundField = new EditBox(this.font, fieldX, y, fieldW, 20, Component.translatable("textbox.config.sound"));
        soundField.setMaxLength(200);
        soundField.setValue(currentSound);
        soundField.setResponder(value -> currentSound = value);
        soundField.setTooltip(Tooltip.create(Component.translatable("textbox.config.tooltip.sound")));
        this.addRenderableWidget(soundField);
        y += ROW_HEIGHT;

        EditBox iconField = new EditBox(this.font, fieldX, y, fieldW, 20, Component.translatable("textbox.config.icon"));
        iconField.setMaxLength(400);
        iconField.setValue(currentIcon);
        iconField.setResponder(value -> currentIcon = value);
        iconField.setTooltip(Tooltip.create(Component.translatable("textbox.config.tooltip.icon")));
        this.addRenderableWidget(iconField);
        y += ROW_HEIGHT;

        EditBox boxTextureField = new EditBox(this.font, fieldX, y, fieldW, 20, Component.translatable("textbox.config.box_texture"));
        boxTextureField.setMaxLength(400);
        boxTextureField.setValue(currentBoxTexture);
        boxTextureField.setResponder(value -> currentBoxTexture = value);
        boxTextureField.setTooltip(Tooltip.create(Component.translatable("textbox.config.tooltip.box_texture")));
        this.addRenderableWidget(boxTextureField);
        y += ROW_HEIGHT;

        this.addRenderableWidget(button(positionButtonLabel(), b -> cyclePosition(), fieldX, y, fieldW, "textbox.config.tooltip.position"));
        y += ROW_HEIGHT;

        this.addRenderableWidget(button(blockingButtonLabel(), b -> toggleBlocking(), fieldX, y, fieldW, "textbox.config.tooltip.blocking"));
        y += ROW_HEIGHT;

        this.addRenderableWidget(button(Component.literal("-"), b -> changeRadius(-1), fieldX, y, 40, null));
        this.addRenderableWidget(button(radiusButtonLabel(), b -> {}, fieldX + 44, y, fieldW - 88, "textbox.config.tooltip.radius"));
        this.addRenderableWidget(button(Component.literal("+"), b -> changeRadius(1), fieldX + fieldW - 40, y, 40, null));
        y += ROW_HEIGHT;

        this.addRenderableWidget(button(Component.literal("-"), b -> changeTime(-TIME_STEP), fieldX, y, 40, null));
        this.addRenderableWidget(button(timeButtonLabel(), b -> {}, fieldX + 44, y, fieldW - 88, "textbox.config.tooltip.time"));
        this.addRenderableWidget(button(Component.literal("+"), b -> changeTime(TIME_STEP), fieldX + fieldW - 40, y, 40, null));
        y += ROW_HEIGHT;

        this.addRenderableWidget(button(sizeButtonLabel(), b -> cycleSize(), fieldX, y, fieldW, "textbox.config.tooltip.size"));
        y += ROW_HEIGHT;

        this.addRenderableWidget(button(areaButtonLabel(), b -> toggleArea(), fieldX, y, fieldW, "textbox.config.tooltip.area"));
        y += ROW_HEIGHT;

        this.addRenderableWidget(button(Component.translatable("textbox.config.done"), b -> onDone(), fieldX, y, 84, null));
        this.addRenderableWidget(button(Component.translatable("textbox.config.cancel"), b -> onClose(), fieldX + 88, y, 82, null));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int labelX = this.width / 2 - 120;
        for (int i = 0; i < LABEL_KEYS.length; i++) {
            guiGraphics.drawString(this.font, Component.translatable(LABEL_KEYS[i]), labelX, contentTop + i * ROW_HEIGHT + 6, 0xFFAAAAAA);
        }

        // Previsualización del color
        int previewX = fieldX + fieldW - 20;
        int previewY = contentTop + ROW_HEIGHT;
        guiGraphics.fill(previewX - 1, previewY - 1, previewX + 21, previewY + 21, 0xFFFFFFFF);
        guiGraphics.fill(previewX, previewY, previewX + 20, previewY + 20, parseColor(currentColor));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int usableHeight = Math.max(60, this.height - TOP_MARGIN - BOTTOM_MARGIN);
        int maxScroll = Math.max(0, CONTENT_HEIGHT - usableHeight);
        this.scroll = (int) Math.max(0, Math.min(maxScroll, this.scroll - verticalAmount * 16));
        this.rebuildWidgets();
        return true;
    }

    private Button button(Component label, Button.OnPress onPress, int x, int y, int width, String tooltipKey) {
        Button b = Button.builder(label, onPress).bounds(x, y, width, 20).build();
        if (tooltipKey != null) {
            b.setTooltip(Tooltip.create(Component.translatable(tooltipKey)));
        }
        return b;
    }

    private Component positionButtonLabel() {
        return Component.translatable("textbox.config.position")
                .append(": ")
                .append(Component.translatable("textbox.config.position." + currentPosition()));
    }

    private Component blockingButtonLabel() {
        return Component.translatable("textbox.config.blocking")
                .append(": ")
                .append(Component.translatable(blocking ? "textbox.config.yes" : "textbox.config.no"));
    }

    private Component radiusButtonLabel() {
        return Component.translatable("textbox.config.radius")
                .append(": ")
                .append(Component.literal(radius + " "))
                .append(Component.translatable("textbox.config.blocks"));
    }

    private Component timeButtonLabel() {
        return Component.translatable("textbox.config.time")
                .append(": ")
                .append(Component.literal(formatTime(time) + " s"));
    }

    private Component sizeButtonLabel() {
        return Component.translatable("textbox.config.size")
                .append(": ")
                .append(Component.literal(currentPercent() + "%"));
    }

    private Component areaButtonLabel() {
        return Component.translatable("textbox.config.area")
                .append(": ")
                .append(Component.translatable(AreaConfigClient.isAreaChatEnabled() ? "textbox.config.yes" : "textbox.config.no"));
    }

    private void toggleArea() {
        AreaConfigClient.toggleAreaChat();
        rebuildWidgets();
    }

    private void cyclePosition() {
        positionIndex = 1 - positionIndex;
        rebuildWidgets();
    }

    private void toggleBlocking() {
        blocking = !blocking;
        rebuildWidgets();
    }

    private void changeRadius(int delta) {
        radius = Math.max(4, Math.min(24, radius + delta));
        rebuildWidgets();
    }

    private void changeTime(double delta) {
        double next = Math.round((time + delta) * 10.0) / 10.0;
        time = Math.max(TIME_MIN, Math.min(TIME_MAX, next));
        rebuildWidgets();
    }

    private void cycleSize() {
        sizeIndex = (sizeIndex + 1) % TextboxMod.SIZE_PERCENTS.length;
        rebuildWidgets();
    }

    private void onDone() {
        config.name = currentName;
        config.color = currentColor;
        config.sound = currentSound;
        config.icon = currentIcon;
        config.boxTexture = currentBoxTexture;
        config.position = currentPosition();
        config.radius = radius;
        config.time = time;
        config.blocking = blocking;
        config.size = currentPercent() / 100.0;
        config.save();
        AreaConfigClient.sendConfigToServer();
        onClose();
    }

    private String currentPosition() {
        return POSITIONS[positionIndex];
    }

    private int currentPercent() {
        return TextboxMod.SIZE_PERCENTS[sizeIndex];
    }

    private static int sizeIndexFor(float scale) {
        float snapped = TextboxMod.snapSize(scale);
        for (int i = 0; i < TextboxMod.SIZE_PERCENTS.length; i++) {
            if (Math.abs(TextboxMod.SIZE_PERCENTS[i] / 100.0f - snapped) < 0.001f) {
                return i;
            }
        }
        return 0;
    }

    private String formatTime(double seconds) {
        return String.format(Locale.ROOT, "%.1f", seconds);
    }

    private static int parseColor(String hex) {
        String h = hex == null ? "" : hex.trim().replaceFirst("^#", "");
        if (h.matches("[0-9a-fA-F]{6}")) {
            return 0xFF000000 | (int) Long.parseLong(h, 16);
        }
        return TextboxMod.BRAND_COLOR;
    }
}
