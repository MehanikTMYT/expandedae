package lu.kolja.expandedae.datagen;

import appeng.datagen.providers.models.AE2ModelProvider;
import lu.kolja.expandedae.Expandedae;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Round 18: {@code GatherDataEvent} is now split into {@code GatherDataEvent.Client} /
 * {@code .Server}, providers are added through {@code generator.getVanillaPack(...)} rather than
 * {@code generator.addProvider(include, provider)}, and there is no {@code ExistingFileHelper}
 * any more. The model provider is now a {@link appeng.datagen.providers.models.ModelSubProvider}
 * handed to AE2's {@link AE2ModelProvider} factory.
 */
@EventBusSubscriber(modid = Expandedae.MODID)
public class ExpDataGen {
    @SubscribeEvent
    public static void datagen(GatherDataEvent.Client event) {
        var generator = event.getGenerator();
        var pack = generator.getVanillaPack(true);

        pack.addProvider(ExpLangProvider::new);
        pack.addProvider(AE2ModelProvider.create(Expandedae.MODID, ExpModelProvider::new));

        var registries = event.getLookupProvider();
        pack.addProvider(output -> new ExpRecipeProvider.Runner(output, registries));
        pack.addProvider(output -> new ExpLootProvider(output, registries));
    }
}
