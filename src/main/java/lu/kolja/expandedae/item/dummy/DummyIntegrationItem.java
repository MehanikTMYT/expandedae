package lu.kolja.expandedae.item.dummy;

import appeng.items.AEBaseItem;
import lu.kolja.expandedae.enums.ADDONS;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

public class DummyIntegrationItem extends AEBaseItem {
    private final ADDONS addon;

    public DummyIntegrationItem(Item.Properties properties, ADDONS addon) {
        super(properties);
        this.addon = addon;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
            Consumer<Component> lines, TooltipFlag flag) {
        lines.accept(addon.getUnavailableTooltip());
    }

    @Override
    public void addToMainCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {}
}
