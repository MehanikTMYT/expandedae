package lu.kolja.expandedae.client.gui.widgets;

import appeng.client.gui.style.Blitter;
import de.mari_023.ae2wtlib.api.gui.Icon;
import lu.kolja.expandedae.Expandedae;
import net.minecraft.resources.Identifier;

public enum ExpIcon {

    MODIFY_PATTERNS(0, 0),
    MULTIPLY_2(0, 240, 8, 8),
    MULTIPLY_3(12, 240, 8, 8),
    MULTIPLY_8(24, 240, 8, 8),
    DIVISION_2(0, 248, 8, 8),
    DIVISION_3(12, 248, 8, 8),
    DIVISION_8(24, 248, 8, 8),

    EXP_PATTERN_ENCODING(240, 192),

    CUSTOM_TOOLBAR_BUTTON_BACKGROUND(208, 224, 10, 10),
    CUSTOM_TOOLBAR_BUTTON_BACKGROUND_FOCUS(218, 224, 10, 10),
    CUSTOM_TOOLBAR_BUTTON_BACKGROUND_HOVER(228, 224, 10, 10),

    HALF_TOOLBAR_BUTTON_BACKGROUND(208, 240, 9, 10),
    HALF_TOOLBAR_BUTTON_BACKGROUND_FOCUS(217, 240, 9, 10),
    HALF_TOOLBAR_BUTTON_BACKGROUND_HOVER(226, 240, 9, 10),

    DOUBLE_ARROW(192, 240, 8, 8),
    TOOLBAR_BUTTON_BACKGROUND(240, 240);

    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public static final int TEXTURE_WIDTH = 256;
    public static final int TEXTURE_HEIGHT = 256;
    public static final Identifier TEXTURE_RL = Expandedae.makeId("textures/gui/states.png");
    public static final Icon.Texture TEXTURE = new Icon.Texture(TEXTURE_RL, TEXTURE_WIDTH, TEXTURE_HEIGHT);


    ExpIcon(int x, int y) {
        this(x, y, 16, 16);
    }

    ExpIcon(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Blitter getBlitter() {
        return Blitter.texture(TEXTURE_RL, TEXTURE_WIDTH, TEXTURE_HEIGHT).src(this.x, this.y, this.width, this.height);
    }
}
