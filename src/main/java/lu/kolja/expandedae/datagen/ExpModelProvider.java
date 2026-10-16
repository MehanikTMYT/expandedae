package lu.kolja.expandedae.datagen;

import appeng.api.util.AEColor;
import appeng.api.util.AEColorVariant;
import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.client.api.model.parts.StaticPartModel;
import appeng.client.model.StatusIndicatorPartModel;
import appeng.client.render.AEColorItemTintSource;
import appeng.datagen.providers.models.ModelSubProvider;
import appeng.api.orientation.BlockOrientation;
import appeng.block.crafting.PatternProviderBlock;
import appeng.core.definitions.BlockDefinition;
import appeng.datagen.providers.models.PartModelOutput;
import com.mojang.math.Quadrant;
import lu.kolja.expandedae.block.block.ExpIOPortBlock;
import lu.kolja.expandedae.definition.ExpBlocks;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import lu.kolja.expandedae.Expandedae;
import lu.kolja.expandedae.client.render.ExpCraftingUnitModelProvider;
import lu.kolja.expandedae.enums.ExpTiers;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

import static lu.kolja.expandedae.definition.ExpItems.AUTO_COMPLETE_CARD;
import static lu.kolja.expandedae.definition.ExpItems.EXP_ENCODING_TERMINAL;
import static lu.kolja.expandedae.definition.ExpItems.EXP_PATTERN_PROVIDER_PART;
import static lu.kolja.expandedae.definition.ExpItems.EXP_PATTERN_PROVIDER_UPGRADE;
import static lu.kolja.expandedae.definition.ExpItems.GREATER_ACCEL_CARD;
import static lu.kolja.expandedae.definition.ExpItems.PATTERN_REFILLER_CARD;
import static lu.kolja.expandedae.definition.ExpItems.WIRELESS_EXP_ENCODING_TERMINAL;

/**
 * Round 18 rewrite.
 *
 * <p>1.21.1 used {@code appeng.datagen.providers.models.AE2BlockStateProvider}, which was built on
 * NeoForge's {@code net.neoforged.neoforge.client.model.generators} + {@code ExistingFileHelper}.
 * Both the NeoForge generator package and AE2's wrapper around it are gone in 26.1.2. The current
 * mechanism is vanilla's {@code ModelProvider} with {@code BlockModelGenerators} /
 * {@code ItemModelGenerators}, which AE2 exposes to addons as
 * {@link appeng.datagen.providers.models.AE2ModelProvider} + {@link ModelSubProvider}, and part
 * appearance now comes from {@link PartModelOutput} instead of the removed {@code @PartModels}
 * annotation on the part class.
 */
public class ExpModelProvider extends ModelSubProvider {

    public ExpModelProvider(BlockModelGenerators blockModels, ItemModelGenerators itemModels,
            PartModelOutput partModels) {
        super(blockModels, itemModels, partModels);
    }

    @Override
    protected void register() {
        basicItem(PATTERN_REFILLER_CARD);
        basicItem(AUTO_COMPLETE_CARD);
        basicItem(GREATER_ACCEL_CARD);
        basicItem(EXP_PATTERN_PROVIDER_UPGRADE);
        basicItem(WIRELESS_EXP_ENCODING_TERMINAL);

        for (var tier : ExpTiers.values()) {
            craftingUnitModel(tier);
        }

        // Round 18: these four blockstates used to be hand-written JSON under src/main/resources in
        // the 1.21.1 blockstate schema (bare {"model": "..."} variants plus AE2's old "ae2:z" key).
        // 26.1.2 requires typed variants ({"type":"ae2:spinnable_variant", ..., "z": n}) and the
        // vanilla ModelProvider now *validates* that every registered block has a generated
        // definition, so they are generated here instead of being migrated by hand. The referenced
        // block models themselves are unchanged and still ship in src/main/resources.
        expPatternProvider();
        expIoPort();
        horizontalFacingBlock(ExpBlocks.MIKU);
        horizontalFacingBlock(ExpBlocks.TETO);

        // Parts: the on/off/has-channel triple that ExpPatternProviderPart and
        // ExpEncodingTerminalPart used to build by hand as IPartModel constants.
        partModels.composite(EXP_PATTERN_PROVIDER_PART,
                new StaticPartModel.Unbaked(Expandedae.makeId("part/exp_pattern_provider_base")),
                partStatusIndicator(appeng.core.AppEng.makeId("part/interface")));
        customPartItemModel(EXP_PATTERN_PROVIDER_PART.asItem());

        partModels.composite(EXP_ENCODING_TERMINAL,
                new StaticPartModel.Unbaked(appeng.core.AppEng.makeId("part/display_base")),
                new StatusIndicatorPartModel.Unbaked(
                        Expandedae.makeId("part/exp_encoding_terminal_on"),
                        Expandedae.makeId("part/exp_encoding_terminal_on"),
                        Expandedae.makeId("part/exp_encoding_terminal_off")),
                partStatusIndicator(appeng.core.AppEng.makeId("part/display_status")));
        customPartItemModel(EXP_ENCODING_TERMINAL.asItem());
    }

    private void expPatternProvider() {
        var allModel = Expandedae.makeId("block/exp_pattern_provider_all");
        var orientedModel = Expandedae.makeId("block/exp_pattern_provider_oriented");

        blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ExpBlocks.EXP_PATTERN_PROVIDER.block())
                        .with(PropertyDispatch.initial(PatternProviderBlock.PUSH_DIRECTION)
                                .generate(pushDirection -> {
                                    var forward = pushDirection.getDirection();
                                    if (forward == null) {
                                        return plainVariant(allModel);
                                    }
                                    var orientation = BlockOrientation.get(forward);
                                    // +90: the model is authored pointing UP while BlockOrientation
                                    // assumes NORTH - identical to AE2's own patternProvider().
                                    return plainVariant(orientedModel).with(applyRotation(
                                            orientation.getAngleX() + 90,
                                            orientation.getAngleY()));
                                })));
        blockModels.registerSimpleItemModel(ExpBlocks.EXP_PATTERN_PROVIDER.asItem(), allModel);
    }

    private void expIoPort() {
        var offModel = Expandedae.makeId("block/exp_io_port");
        var onModel = Expandedae.makeId("block/exp_io_port_on");

        blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ExpBlocks.EXP_IO_PORT.block())
                        .with(PropertyDispatch.initial(ExpIOPortBlock.POWERED)
                                .select(false, plainSpinnableVariant(offModel))
                                .select(true, plainSpinnableVariant(onModel)))
                        .withUnbaked(createFacingSpinDispatch()));
        blockModels.registerSimpleItemModel(ExpBlocks.EXP_IO_PORT.asItem(), offModel);
    }

    private void horizontalFacingBlock(BlockDefinition<?> def) {
        var model = Expandedae.makeId("block/" + def.id().getPath());
        blockStateOutput.accept(
                MultiVariantGenerator.dispatch(def.block(), plainVariant(model))
                        .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                                .select(Direction.NORTH, VariantMutator.Y_ROT.withValue(Quadrant.R0))
                                .select(Direction.EAST, VariantMutator.Y_ROT.withValue(Quadrant.R90))
                                .select(Direction.SOUTH, VariantMutator.Y_ROT.withValue(Quadrant.R180))
                                .select(Direction.WEST, VariantMutator.Y_ROT.withValue(Quadrant.R270))));
        blockModels.registerSimpleItemModel(def.asItem(), model);
    }

    private void craftingUnitModel(ExpTiers tier) {
        var block = tier.getDefinition().block();
        var name = tier.isCPU() ? tier.getCpuAffix() : tier.getAffix();

        var unformedModel = ModelTemplates.CUBE_ALL.create(
                Expandedae.makeId("block/crafting/" + name),
                TextureMapping.cube(expMaterial("block/crafting/" + name)),
                modelOutput);
        var formedModel = customBlockStateModel(new ExpCraftingUnitModelProvider.Unbaked(tier));

        blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(AbstractCraftingUnitBlock.FORMED)
                                .select(false, plainVariant(unformedModel))
                                .select(true, formedModel)));

        blockModels.registerSimpleItemModel(block.asItem(), unformedModel);
    }

    private static StatusIndicatorPartModel.Unbaked partStatusIndicator(Identifier baseModel) {
        return new StatusIndicatorPartModel.Unbaked(
                baseModel.withSuffix("_has_channel"),
                baseModel.withSuffix("_on"),
                baseModel.withSuffix("_off"));
    }

    /**
     * Mirrors AE2's own {@code PartModelProvider#customPartItemModel}: the part item model is the
     * hand-written geometry under {@code models/item/}, tinted with the AE colour tint sources the
     * shared AE2 parent models expect.
     */
    private void customPartItemModel(Item item) {
        itemModels.itemModelOutput.accept(
                item,
                ItemModelUtils.tintedModel(
                        ModelLocationUtils.getModelLocation(item),
                        new Constant(-1),
                        new AEColorItemTintSource(AEColor.TRANSPARENT, AEColorVariant.DARK),
                        new AEColorItemTintSource(AEColor.TRANSPARENT, AEColorVariant.MEDIUM),
                        new AEColorItemTintSource(AEColor.TRANSPARENT, AEColorVariant.BRIGHT),
                        new AEColorItemTintSource(AEColor.TRANSPARENT, AEColorVariant.MEDIUM_BRIGHT)));
    }

    private void basicItem(ItemLike item) {
        itemModels.generateFlatItem(item.asItem(), ModelTemplates.FLAT_ITEM);
    }

    private static net.minecraft.client.resources.model.sprite.Material expMaterial(String texture) {
        return new net.minecraft.client.resources.model.sprite.Material(Expandedae.makeId(texture));
    }
}
