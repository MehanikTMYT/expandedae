package lu.kolja.expandedae.mixin.terminal;

import appeng.api.config.ActionItems;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.menu.me.items.PatternEncodingTermMenu;
import lu.kolja.expandedae.terminal.ExpEncodingTerminalMenu;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PatternEncodingTermScreen.class, remap = false)
public class MixinPatternEncodingTermScreen {

    @Inject(
            // Round 18: pinned to the full descriptor. PatternEncodingTermScreen's constructor
            // contains exactly two lambdas - lambda$new$0 is the mode tab button
            // (btn -> getMenu().setMode(mode), which captures `this`), lambda$new$1 is
            // `act -> menu.encode()`, which captures the *constructor parameter* `menu` and is
            // therefore a static synthetic with (PatternEncodingTermMenu, ActionItems). Verified by
            // reading appeng/client/gui/me/items/PatternEncodingTermScreen.java in our AE2 tree.
            method = "lambda$new$1(Lappeng/menu/me/items/PatternEncodingTermMenu;Lappeng/api/config/ActionItems;)V",
            at = @At("TAIL")
    )
    private static void encode(PatternEncodingTermMenu menu, ActionItems act, CallbackInfo ci) {
        if (!(menu instanceof ExpEncodingTerminalMenu expMenu)) return;
        // Round 18: the static Screen.hasShiftDown() helper is gone; modifier state is read from
        // the input event (InputWithModifiers) or, for a context-free query like this one, straight
        // from the window.
        var window = Minecraft.getInstance().getWindow();
        var shift = InputConstants.isKeyDown(window, InputConstants.KEY_LSHIFT)
                || InputConstants.isKeyDown(window, InputConstants.KEY_RSHIFT);
        expMenu.movePattern(shift);
    }
}
