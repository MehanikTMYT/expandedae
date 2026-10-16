package lu.kolja.expandedae.datagen;

import lu.kolja.expandedae.definition.ExpBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import appeng.core.definitions.BlockDefinition;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ExpLootProvider extends LootTableProvider {
    public ExpLootProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(new SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)), registries);
    }

    private static class BlockLoot extends BlockLootSubProvider {
        protected BlockLoot(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
        }

        // Round 18: BlockLootSubProvider#map is private now; the base class already drives
        // generate() and writes the collected tables itself, provided getKnownBlocks() is correct.
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ExpBlocks.getBlocks().stream().map(BlockDefinition::block).map(b -> (Block) b).toList();
        }

        @Override
        public void generate() {
            for (var block : ExpBlocks.getBlocks()) {
                add(block.block(),
                        LootTable.lootTable().withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(block))
                                .when(ExplosionCondition.survivesExplosion())
                        )
                );
            }
        }
    }
}
