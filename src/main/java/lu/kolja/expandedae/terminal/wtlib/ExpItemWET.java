package lu.kolja.expandedae.terminal.wtlib;

import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class ExpItemWET extends ItemWT {
    // Round 18: ItemWT (like every Item in 1.21.2+) must receive the registry-created Properties,
    // which carry the item's ResourceKey.
    public ExpItemWET(Properties properties) {
        super(properties);
    }

    @Override
    public MenuType<?> getMenuType(ItemMenuHostLocator locator, Player player) {
        return ExpWETMenu.TYPE;
    }
}