package lu.kolja.expandedae.client.gui.widgets;

import appeng.util.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.ITooltip;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ModifyIconButton extends Button implements ITooltip {
    private final ExpIcon icon;
    private final Component displayName;
    private final Component displayValue;
    private boolean halfSize = false;
    private boolean disableClickSound = false;
    private boolean disableBackground = false;

    public ModifyIconButton(Button.OnPress onPress, ExpIcon icon, Component displayName, Component displayValue) {
        super(0, 0, 8, 8, Component.empty(), onPress, DEFAULT_NARRATION);
        this.icon = icon;
        this.displayName = displayName;
        this.displayValue = displayValue;
    }

    public void setVisibility(boolean vis) {
        visible = vis;
        active = vis;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            var item = this.getItemOverlay();

            if (this.halfSize) {
                this.width = 8;
                this.height = 8;
            }

            var yOffset = isHovered() ? 1 : 0;

            if (this.halfSize) {
                if (!disableBackground) {
                    Blitter.icon(Icon.TOOLBAR_BUTTON_BACKGROUND).dest(getX(), getY()).blit(guiGraphics);
                }
                if (item != null) {
                    guiGraphics.item(new ItemStack(item), getX(), getY());
                } else if (icon != null) {
                    Blitter blitter = icon.getBlitter();
                    if (!this.active) {
                        blitter.opacity(0.5f);
                    }
                    blitter.dest(getX(), getY()).blit(guiGraphics);
                }
            } else {
                if (!disableBackground) {
                    var bgIcon = isHovered() ? ExpIcon.HALF_TOOLBAR_BUTTON_BACKGROUND_HOVER
                            : isFocused() ? ExpIcon.HALF_TOOLBAR_BUTTON_BACKGROUND_FOCUS : ExpIcon.HALF_TOOLBAR_BUTTON_BACKGROUND;

                    bgIcon.getBlitter()
                            .dest(getX() - 1, getY() + yOffset, 9, 10)
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

    @Nullable
    protected Item getItemOverlay() {
        return null;
    }

    @Override
    public List<Component> getTooltipMessage() {
        return Collections.singletonList(Component.empty().append(displayName).append("\n").append(displayValue));
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(
                getX(),
                getY(),
                8,
                8
        );
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
