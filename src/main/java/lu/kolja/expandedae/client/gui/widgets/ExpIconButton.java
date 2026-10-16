package lu.kolja.expandedae.client.gui.widgets;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.ITooltip;
import appeng.util.Icon;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class ExpIconButton extends Button implements ITooltip {
    private boolean halfSize = false;

    private boolean disableClickSound = false;

    private boolean disableBackground = false;

    public ExpIconButton(OnPress onPress, int width, int height) {
        super(0, 0, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
    }

    public void setVisibility(boolean vis) {
        this.visible = vis;
        this.active = vis;
    }

    @Override
    public void playDownSound(SoundManager soundHandler) {
        if (!disableClickSound) {
            super.playDownSound(soundHandler);
        }
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {

        if (this.visible) {
            var icon = this.getIcon();
            var item = this.getItemOverlay();

            var yOffset = isHovered() ? 1 : 0;

            if (this.halfSize) {
                if (!disableBackground) {
                    var bgIcon = isHovered() ? ExpIcon.CUSTOM_TOOLBAR_BUTTON_BACKGROUND_HOVER
                            : isFocused() ? ExpIcon.CUSTOM_TOOLBAR_BUTTON_BACKGROUND_FOCUS : ExpIcon.CUSTOM_TOOLBAR_BUTTON_BACKGROUND;

                    bgIcon.getBlitter()
                            .dest(getX() - 1, getY() + yOffset, 10, 10)
                            .blit(guiGraphics);
                }
                if (item != null) {
                    guiGraphics.item(new ItemStack(item), getX(), getY() + 1 + yOffset);
                } else if (icon != null) {
                    icon.getBlitter().dest(getX(), getY() + 1 + yOffset).blit(guiGraphics);
                }
            } else {
                if (!disableBackground) {
                    Icon bgIcon = isHovered() ? Icon.TOOLBAR_BUTTON_BACKGROUND_HOVER
                            : isFocused() ? Icon.TOOLBAR_BUTTON_BACKGROUND_FOCUS : Icon.TOOLBAR_BUTTON_BACKGROUND;

                    Blitter.icon(bgIcon)
                            .dest(getX() - 1, getY() + yOffset, 18, 20)
                            .blit(guiGraphics);
                }
                if (item != null) {
                    guiGraphics.item(new ItemStack(item), getX(), getY() + 1 + yOffset);
                } else if (icon != null) {
                    icon.getBlitter().dest(getX(), getY() + 1 + yOffset).blit(guiGraphics);
                }
            }
        }
    }

    protected abstract ExpIcon getIcon();

    /**
     * Prioritized over {@link #getIcon()} if not null.
     */
    @Nullable
    protected Item getItemOverlay() {
        return null;
    }

    @Override
    public List<Component> getTooltipMessage() {
        return Collections.singletonList(getMessage());
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(
                getX(),
                getY(),
                this.halfSize ? 8 : 16,
                this.halfSize ? 8 : 16);
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return this.visible;
    }

    public boolean isHalfSize() {
        return this.halfSize;
    }

    public void setHalfSize(boolean halfSize) {
        this.halfSize = halfSize;
    }

    public boolean isDisableClickSound() {
        return disableClickSound;
    }

    public void setDisableClickSound(boolean disableClickSound) {
        this.disableClickSound = disableClickSound;
    }

    public boolean isDisableBackground() {
        return disableBackground;
    }

    public void setDisableBackground(boolean disableBackground) {
        this.disableBackground = disableBackground;
    }
}
