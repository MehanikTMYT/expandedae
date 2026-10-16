package lu.kolja.expandedae.xmod.ae2wtlib;

import appeng.api.config.Actionable;
import appeng.api.features.GridLinkables;
import appeng.items.tools.powered.WirelessTerminalItem;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;
import lu.kolja.expandedae.terminal.wtlib.ExpItemWET;
import lu.kolja.expandedae.terminal.wtlib.ExpWETMenu;
import lu.kolja.expandedae.terminal.wtlib.ExpWETMenuHost;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.RegisterEvent;

import static lu.kolja.expandedae.client.gui.widgets.ExpIcon.EXP_PATTERN_ENCODING;
import static lu.kolja.expandedae.client.gui.widgets.ExpIcon.TEXTURE;

public class WTLibIntegration {
    /**
     * Round 18: MC 1.21.2+ requires every Item to be constructed with the registry-created
     * {@link Item.Properties} (they carry the item's {@code ResourceKey}), so the terminal can no
     * longer be a {@code static final} built at class-init with no arguments. It is now created by
     * {@link #createTerminal(Item.Properties)}, which the DeferredRegister supplier in
     * {@code ExpItems.WIRELESS_EXP_ENCODING_TERMINAL} invokes. The AE2WTLib terminal registration
     * and the grid-linkable registration move with it, so they still happen exactly once and at the
     * same point in the lifecycle as before (first touch of this class from the item supplier).
     */
    public static ExpItemWET TERMINAL;

    public static ExpItemWET createTerminal(Item.Properties properties) {
        if (TERMINAL != null) {
            return TERMINAL;
        }
        TERMINAL = new ExpItemWET(properties);
        var icon = new Icon(EXP_PATTERN_ENCODING.x, EXP_PATTERN_ENCODING.y, 16, 16, TEXTURE);
        AddTerminalEvent.register(
                event -> event.builder(
                        "exp_encoding",
                        ExpWETMenuHost::new,
                        ExpWETMenu.TYPE,
                        TERMINAL,
                        icon
                ).addTerminal()
        );
        GridLinkables.register(TERMINAL, WirelessTerminalItem.LINKABLE_HANDLER);
        return TERMINAL;
    }

    public static ItemStack getChargedTerminal() {
        var stack = new ItemStack(TERMINAL);
        TERMINAL.injectAEPower(stack, TERMINAL.getAEMaxPower(stack), Actionable.MODULATE);
        return stack;
    }

    public static void registerMenu(RegisterEvent e) {
        if (e.getRegistryKey().equals(Registries.MENU)) {
            e.register(Registries.MENU, ExpWETMenu.ID, () -> ExpWETMenu.TYPE);
        }
    }
}
