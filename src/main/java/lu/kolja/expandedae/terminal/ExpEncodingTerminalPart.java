package lu.kolja.expandedae.terminal;

import appeng.api.parts.IPartItem;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.encoding.PatternEncodingTerminalPart;
import lu.kolja.expandedae.definition.ExpItems;
import lu.kolja.expandedae.definition.ExpMenus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Round 18 note on models: see {@link lu.kolja.expandedae.part.ExpPatternProviderPart}. The
 * off/on/has-channel {@code IPartModel} triple this class used to build from
 * {@code MODEL_BASE}/{@code MODEL_STATUS_*} no longer exists in AE2 26.1.x; the equivalent is a
 * {@code StaticPartModel} + {@code StatusIndicatorPartModel} composite declared in
 * {@link lu.kolja.expandedae.datagen.ExpModelProvider}.
 */
public class ExpEncodingTerminalPart extends PatternEncodingTerminalPart {

    public ExpEncodingTerminalPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public boolean onUseWithoutItem(Player p, Vec3 pos) {
        if (!p.level().isClientSide()) {
            MenuOpener.open(ExpMenus.EXP_ENCODING_TERMINAL.get(), p, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(ExpMenus.EXP_ENCODING_TERMINAL.get(), player, subMenu.getLocator());
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return ExpMenus.EXP_ENCODING_TERMINAL.get();
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return ExpItems.EXP_ENCODING_TERMINAL.stack();
    }
}
