package lu.kolja.expandedae.part;

import appeng.api.parts.IPartItem;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.crafting.PatternProviderPart;
import lu.kolja.expandedae.block.block.ExpPatternProviderBlock;
import lu.kolja.expandedae.definition.ExpItems;
import lu.kolja.expandedae.definition.ExpMenus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Round 18 note on models: AE2 26.1.x removed {@code appeng.api.parts.IPartModel},
 * {@code appeng.parts.PartModel} and the {@code @PartModels} annotation. Part appearance is now
 * data-driven — the model is declared once in {@link lu.kolja.expandedae.datagen.ExpModelProvider}
 * via {@code PartModelOutput} and resolved by {@code appeng.client.api.model.parts.*}. The
 * on/off/has-channel distinction that {@code getStaticModels()} used to make by hand is now made
 * by {@code StatusIndicatorPartModel}, driven by {@code AEBasePart.collectModelData(...)}, which
 * the inherited {@link PatternProviderPart} already populates. Nothing to override here.
 */
public class ExpPatternProviderPart extends PatternProviderPart {

    public ExpPatternProviderPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public PatternProviderLogic createLogic() {
        return ExpPatternProviderBlock.createLogic(this.getMainNode(), this);
    }

    @Override
    public boolean onUseWithoutItem(Player p, Vec3 pos) {
        if (!p.level().isClientSide()) {
            MenuOpener.open(ExpMenus.EXP_PATTERN_PROVIDER.get(), p, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(ExpMenus.EXP_PATTERN_PROVIDER.get(), player, subMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return ExpItems.EXP_PATTERN_PROVIDER_PART.stack();
    }
}
