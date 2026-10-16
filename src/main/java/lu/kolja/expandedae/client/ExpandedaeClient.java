package lu.kolja.expandedae.client;

import appeng.client.InitScreens;
import appeng.client.gui.implementations.PatternProviderScreen;
import lu.kolja.expandedae.Expandedae;
import lu.kolja.expandedae.client.render.ExpCraftingUnitModelProvider;
import lu.kolja.expandedae.definition.ExpMenus;
import lu.kolja.expandedae.menu.ExpPatternProviderMenu;
import lu.kolja.expandedae.screen.ExpIOPortScreen;
import lu.kolja.expandedae.terminal.ExpEncodingTerminalMenu;
import lu.kolja.expandedae.terminal.ExpEncodingTerminalScreen;
import lu.kolja.expandedae.terminal.wtlib.ExpWETScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = Expandedae.MODID, dist = Dist.CLIENT)
public class ExpandedaeClient {
    public ExpandedaeClient(IEventBus modEventBus) {
        modEventBus.addListener(ExpandedaeClient::registerBlockStateModels);
        modEventBus.addListener(ExpandedaeClient::initScreens);
    }

    /**
     * Round 18: replaces the old {@code BuiltInModelHooks.addBuiltInModel(...)} loop. In 26.1.2
     * custom block-state models are registered once as a codec and referenced from the generated
     * blockstate JSON (see {@link lu.kolja.expandedae.datagen.ExpModelProvider}), instead of being
     * pushed into a static map keyed by model id.
     */
    private static void registerBlockStateModels(RegisterBlockStateModels event) {
        event.registerModel(
                ExpCraftingUnitModelProvider.Unbaked.ID,
                ExpCraftingUnitModelProvider.Unbaked.MAP_CODEC);
    }

    private static void initScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(
                event,
                ExpMenus.EXP_PATTERN_PROVIDER.get(),
                PatternProviderScreen<ExpPatternProviderMenu>::new,
                "/screens/exp_pattern_provider.json"
        );
        InitScreens.register(
                event,
                ExpMenus.EXP_ENCODING_TERMINAL.get(),
                ExpEncodingTerminalScreen<ExpEncodingTerminalMenu>::new,
                "/screens/terminals/exp_encoding_terminal.json"
        );
        InitScreens.register(
                event,
                ExpMenus.EXP_IO_PORT.get(),
                ExpIOPortScreen::new,
                "/screens/exp_io_port.json"
        );
        ExpWETScreen.register(event);
    }
}
