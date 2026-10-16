package lu.kolja.expandedae.terminal.wtlib;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.InitScreens;
import de.mari_023.ae2wtlib.api.gui.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.api.terminal.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ExpWETScreen extends PatternEncodingTermScreen<ExpWETMenu> implements IUniversalTerminalCapable {
    private final ScrollingUpgradesPanel upgradesPanel;

    public ExpWETScreen(ExpWETMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        // Round 18: IUniversalTerminalCapable no longer exposes cycleTerminalButton(); AE2WTLib
        // 26.1.x offers the terminal switcher as a panel (see de.mari_023.ae2wtlib.wet.WETScreen).
        if (menu.isWUT())
            addTerminalSelectionPanel(widgets);

        upgradesPanel = addUpgradePanel(widgets, getMenu());
    }

    @Override
    public void init() {
        super.init();
        upgradesPanel.setMaxRows(Math.max(2, getVisibleRows()));
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        boolean value = super.keyPressed(keyEvent);
        if (!value)
            return checkForTerminalKeys(keyEvent);
        return true;
    }

    public static void register(RegisterMenuScreensEvent event) {
        InitScreens.register(
                event,
                ExpWETMenu.TYPE,
                ExpWETScreen::new,
                "/screens/expandedae/wireless_exp_encoding_terminal.json"
        );
    }

    @Override
    public WTMenuHost getHost() {
        return (WTMenuHost) getMenu().getHost();
    }
}