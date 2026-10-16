package lu.kolja.expandedae.client.button;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.IconButton;
import appeng.util.Icon;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public abstract class ExpButton extends IconButton {
    public ExpButton(OnPress onPress) {
        super(onPress);
    }

    abstract Blitter getBlitterIcon();

    @Override
    public final Icon getIcon() {
        return null;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            Blitter blitter = getBlitterIcon();
            if (!this.active) blitter.opacity(0.5f);
            if (this.isHalfSize()) {
                this.width = 8;
                this.height = 8;
            }
            if (isFocused()) {
                guiGraphics.fill(getX() - 1, getY() - 1, getX() + width + 1, getY(), 0xFFFFFFFF);
                guiGraphics.fill(getX() - 1, getY(), getX(), getY() + height, 0xFFFFFFFF);
                guiGraphics.fill(getX() + width, getY(), getX() + width + 1, getY() + height, 0xFFFFFFFF);
                guiGraphics.fill(getX() - 1, getY() + height, getX() + width + 1, getY() + height + 1, 0xFFFFFFFF);
            }
            if (this.isHalfSize()) {
                var pose = guiGraphics.pose();
                pose.pushMatrix();
                pose.translate(getX(), getY());
                pose.scale(0.5F, 0.5F);
                if (!this.isDisableBackground()) {
                    Blitter.icon(Icon.TOOLBAR_BUTTON_BACKGROUND).dest(0, 0).blit(guiGraphics);
                }
                blitter.dest(0, 0).blit(guiGraphics);
                pose.popMatrix();
            } else {
                if (!this.isDisableBackground()) {
                    Blitter.icon(Icon.TOOLBAR_BUTTON_BACKGROUND).dest(getX(), getY()).blit(guiGraphics);
                }
                blitter.dest(getX(), getY()).blit(guiGraphics);
            }
            var item = getItemOverlay();
            if (item != null) {
                guiGraphics.item(new ItemStack(item), getX(), getY());
            }
        }
    }
}
