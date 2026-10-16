package lu.kolja.expandedae.datagen;

import appeng.api.util.AEColor;
import appeng.core.definitions.AEBlocks;
import com.glodblock.github.extendedae.common.EAESingletons;
import lu.kolja.expandedae.Expandedae;
import lu.kolja.expandedae.datagen.conditionals.ModNotLoadedCondition;
import lu.kolja.expandedae.definition.ExpBlocks;
import lu.kolja.expandedae.enums.ExpTiers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.concurrent.CompletableFuture;

import static appeng.core.definitions.AEBlocks.CRAFTING_UNIT;
import static appeng.core.definitions.AEBlocks.PATTERN_PROVIDER;
import static appeng.core.definitions.AEItems.*;
import static appeng.core.definitions.AEParts.PATTERN_ENCODING_TERMINAL;
import static appeng.core.definitions.AEParts.SMART_DENSE_CABLE;
import static lu.kolja.expandedae.definition.ExpBlocks.EXP_PATTERN_PROVIDER;
import static lu.kolja.expandedae.definition.ExpBlocks.UNIT;
import static lu.kolja.expandedae.definition.ExpItems.*;
import static lu.kolja.expandedae.enums.ExpTiers.*;
import static net.minecraft.data.recipes.RecipeCategory.MISC;

/**
 * Round 18: {@code RecipeProvider} is now constructed with
 * {@code (HolderLookup.Provider, RecipeOutput)}, exposes {@code shaped(...)}/{@code shapeless(...)}
 * as instance methods (the standalone {@code ShapedRecipeBuilder}/{@code ShapelessRecipeBuilder}
 * entry points are gone), takes recipe ids as {@code String}, and is driven by a
 * {@code RecipeProvider.Runner} rather than being added to the DataGenerator directly.
 */
public class ExpRecipeProvider extends RecipeProvider {
    public ExpRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        var out = this.output;
        shaped(MISC, EXP_PATTERN_PROVIDER)
                .pattern("ECE")
                .pattern("CPC")
                .pattern("ECE")
                .define('C', CAPACITY_CARD)
                .define('E', ENGINEERING_PROCESSOR)
                .define('P', PATTERN_PROVIDER)
                .unlockedBy("has_engineering_processor", has(ENGINEERING_PROCESSOR))
                .unlockedBy("has_pattern_provider", has(PATTERN_PROVIDER))
                .save(conditional(out, notLoaded("extendedae")), craftingId("exp_pattern_provider_ae"));
        shaped(MISC, EXP_PATTERN_PROVIDER)
                .pattern("CE")
                .pattern("EC")
                .define('E', EAESingletons.EX_PATTERN_PROVIDER)
                .define('C', EAESingletons.CONCURRENT_PROCESSOR)
                .unlockedBy("has_concurrent_processor", has(EAESingletons.CONCURRENT_PROCESSOR))
                .save(conditional(out, loaded("extendedae")), craftingId("exp_pattern_provider_ext"));

        shapeless(MISC, EXP_PATTERN_PROVIDER)
                .requires(EXP_PATTERN_PROVIDER_PART)
                .unlockedBy("has_exp_pattern_provider_part", has(EXP_PATTERN_PROVIDER_PART))
                .save(out, craftingId("exp_pattern_provider_alt"));
        shapeless(MISC, EXP_PATTERN_PROVIDER_PART)
                .requires(EXP_PATTERN_PROVIDER)
                .unlockedBy("has_exp_pattern_provider", has(EXP_PATTERN_PROVIDER))
                .save(out, craftingId("exp_pattern_provider_part"));

        shaped(MISC, EXP_PATTERN_PROVIDER_UPGRADE)
                .pattern("ECE")
                .pattern("C C")
                .pattern("ECE")
                .define('C', CAPACITY_CARD)
                .define('E', ENGINEERING_PROCESSOR)
                .unlockedBy("has_engineering_processor", has(ENGINEERING_PROCESSOR))
                .unlockedBy("has_capacity_card", has(CAPACITY_CARD))
                .save(conditional(out, notLoaded("extendedae")), craftingId("exp_pattern_provider_upgrade_ae"));
        shaped(MISC, EXP_PATTERN_PROVIDER_UPGRADE)
                .pattern("CE")
                .pattern("E ")
                .define('E', EAESingletons.EX_PATTERN_PROVIDER)
                .define('C', EAESingletons.CONCURRENT_PROCESSOR)
                .unlockedBy("has_concurrent_processor", has(EAESingletons.CONCURRENT_PROCESSOR))
                .unlockedBy("has_exp_pattern_provider", has(EAESingletons.EX_PATTERN_PROVIDER))
                .save(conditional(out, loaded("extendedae")), craftingId("exp_pattern_provider_upgrade_ext"));

        shapeless(MISC, AUTO_COMPLETE_CARD)
                .requires(ADVANCED_CARD)
                .requires(CRAFTING_CARD)
                .unlockedBy("has_advanced_card", has(ADVANCED_CARD))
                .unlockedBy("has_crafting_card", has(CRAFTING_CARD))
                .save(out, craftingId("auto_complete_card"));

        shaped(MISC, PATTERN_REFILLER_CARD)
                .pattern("AB")
                .pattern("BE")
                .define('A', ADVANCED_CARD)
                .define('B', BLANK_PATTERN)
                .define('E', ENGINEERING_PROCESSOR)
                .unlockedBy("has_advanced_card", has(ADVANCED_CARD))
                .unlockedBy("has_engineering_processor", has(ENGINEERING_PROCESSOR))
                .save(out, craftingId("pattern_refiller_card"));

        shaped(MISC, EXP_ENCODING_TERMINAL)
                .pattern("TC")
                .pattern("L ")
                .define('T', PATTERN_ENCODING_TERMINAL)
                .define('C', CALCULATION_PROCESSOR)
                .define('L', LOGIC_PROCESSOR)
                .unlockedBy("has_calculation_processor", has(CALCULATION_PROCESSOR))
                .unlockedBy("has_logic_processor", has(LOGIC_PROCESSOR))
                .save(out, craftingId("exp_encoding_terminal"));

        shaped(MISC, ExpBlocks.EXP_IO_PORT)
                .pattern("AAA")
                .pattern("AIA")
                .pattern("AAA")
                .define('A', GREATER_ACCEL_CARD)
                .define('I', AEBlocks.IO_PORT)
                .unlockedBy("has_speed_card", has(GREATER_ACCEL_CARD))
                .save(conditional(out, notLoaded("extendedae")), craftingId("exp_io_port_ae"));
        shaped(MISC, ExpBlocks.EXP_IO_PORT)
                .pattern("AAA")
                .pattern("AIA")
                .pattern("AAA")
                .define('A', GREATER_ACCEL_CARD)
                .define('I', EAESingletons.EX_IO_PORT)
                .unlockedBy("has_speed_card", has(GREATER_ACCEL_CARD))
                .save(conditional(out, loaded("extendedae")), craftingId("exp_io_port_ext"));

        shaped(MISC, GREATER_ACCEL_CARD)
                .pattern("AAA")
                .pattern("ACA")
                .pattern("AAA")
                .define('A', SPEED_CARD)
                .define('C', CALCULATION_PROCESSOR)
                .unlockedBy("has_speed_card", has(SPEED_CARD))
                .unlockedBy("has_calculation_processor", has(CALCULATION_PROCESSOR))
                .save(out, craftingId("greater_accel_card"));

        shaped(MISC, UNIT)
                .pattern("UPU")
                .pattern("CLC")
                .pattern("UPU")
                .define('U', CRAFTING_UNIT)
                .define('P', ENGINEERING_PROCESSOR)
                .define('C', SMART_DENSE_CABLE.item(AEColor.TRANSPARENT))
                .define('L', LOGIC_PROCESSOR)
                .unlockedBy("has_engineering_processor", has(ENGINEERING_PROCESSOR))
                .unlockedBy("has_logic_processor", has(LOGIC_PROCESSOR))
                .save(out, craftingId("exp_crafting_unit_ae"));

        // Round 18: the MEGA-Cells variant of this recipe ("exp_crafting_unit_mega", gated on
        // ModLoadedCondition("megacells")) is removed while MEGA Cells has no 26.1.2 artifact.
        // 62832/MEGACells has a `26.1` branch but its newest release is still 4.11.0 (1.21.x) and
        // modmaven serves no gripe._90:megacells metadata, so the definition classes it referenced
        // (MEGABlocks.MEGA_CRAFTING_UNIT, MEGAItems.ACCUMULATION_PROCESSOR) do not exist on any
        // classpath we can build against. The plain AE2 recipe above is therefore unconditional
        // rather than gated on notLoaded("megacells"). Restore both branches verbatim when a
        // 26.1.2 MEGA Cells artifact is published.

        shapeless(MISC, TIER_2.getDefinition())
                .requires(UNIT)
                .requires(ENGINEERING_PROCESSOR)
                .unlockedBy("has_engineering_processor", has(ENGINEERING_PROCESSOR))
                .save(out, craftingId("exp_crafting_accelerator_2"));

        upgrade(out, TIER_2, TIER_4);
        upgrade(out, TIER_4, TIER_8);
        upgrade(out, TIER_8, TIER_16);
        upgrade(out, TIER_16, TIER_32);
        upgrade(out, TIER_32, TIER_64);
        upgrade(out, TIER_64, TIER_128);
        upgrade(out, TIER_128, TIER_256);
        upgrade(out, TIER_256, TIER_512);
        upgrade(out, TIER_512, TIER_1K);
        upgrade(out, TIER_1K, TIER_2K);
        upgrade(out, TIER_2K, TIER_4K);
        upgrade(out, TIER_4K, TIER_8K);
        upgrade(out, TIER_8K, TIER_16K);
        upgrade(out, TIER_16K, TIER_32K);
        upgrade(out, TIER_32K, TIER_64K);
        upgrade(out, TIER_64K, TIER_128K);
        upgrade(out, TIER_128K, TIER_256K);
        upgrade(out, TIER_256K, TIER_512K);
        upgrade(out, TIER_512K, TIER_1M);
    }

    private String craftingId(String name) {
        return Expandedae.makeId("crafting/" + name).toString();
    }

    private void upgrade(RecipeOutput out, ExpTiers previousTier, ExpTiers tier) {
        shapeless(MISC, tier.getDefinition())
                .requires(previousTier.getDefinition(), 2)
                .unlockedBy("has_calculation_processor", has(CALCULATION_PROCESSOR))
                .save(out, craftingId(tier.getCpuAffix()));
    }

    private static RecipeOutput conditional(RecipeOutput output, ICondition... condition) {
        return output.withConditions(condition);
    }

    private static ICondition loaded(String mod) {
        return new ModLoadedCondition(mod);
    }
    private static ICondition notLoaded(String mod) {
        return new ModNotLoadedCondition(mod);
    }

    /** Round 18: DataGenerator entry point for the provider above. */
    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ExpRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "ExpandedAE Recipes";
        }
    }
}
