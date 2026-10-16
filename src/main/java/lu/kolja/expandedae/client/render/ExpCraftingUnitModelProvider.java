package lu.kolja.expandedae.client.render;

import appeng.client.render.crafting.AbstractCraftingUnitModelProvider;
import appeng.client.render.crafting.LightBakedModel;
import appeng.client.render.crafting.UnitBakedModel;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lu.kolja.expandedae.Expandedae;
import lu.kolja.expandedae.enums.ExpTiers;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

/**
 * Round 18 rewrite.
 *
 * <p>In 1.21.1 this class implemented the old {@code getMaterials()} / {@code getBakedModel(
 * Function&lt;Material, TextureAtlasSprite&gt;)} contract and its models were injected at client
 * construction time through {@code appeng.hooks.BuiltInModelHooks}. Both of those are gone in
 * 26.1.2:
 *
 * <ul>
 *   <li>{@code net.minecraft.client.resources.model.Material} moved to
 *       {@code net.minecraft.client.resources.model.sprite.Material} and no longer carries an
 *       atlas id — baking now goes through {@link MaterialBaker#get(Material, ModelDebugName)}
 *       and yields a {@link Material.Baked}.</li>
 *   <li>{@link AbstractCraftingUnitModelProvider} now declares a single
 *       {@code BlockStateModel bake(MaterialBaker)}.</li>
 *   <li>{@code ChunkRenderTypeSet} / {@code ModelData} render-type overrides are gone; render
 *       type selection is handled by the {@code CraftingCubeModel} base class itself.</li>
 *   <li>Built-in models are no longer injected by id — a mod registers a
 *       {@link CustomUnbakedBlockStateModel} codec and references it from its generated
 *       blockstate JSON. AE2's own {@code CraftingCubeModel.Unbaked} is hard-bound to AE2's
 *       {@code CraftingUnitType} enum, so an addon with its own tier enum needs its own
 *       {@link Unbaked} record — that is {@link Unbaked} below.</li>
 * </ul>
 */
public class ExpCraftingUnitModelProvider extends AbstractCraftingUnitModelProvider<ExpTiers>
        implements ModelDebugName {

    protected static final Material RING_CORNER = texture("ring_corner");
    protected static final Material RING_SIDE_HOR = texture("ring_side_hor");
    protected static final Material RING_SIDE_VER = texture("ring_side_ver");
    protected static final Material UNIT_BASE = texture("unit_base");
    protected static final Material LIGHT_BASE = texture("light_base");

    public ExpCraftingUnitModelProvider(ExpTiers cpu) {
        super(cpu);
    }

    /**
     * The per-tier light overlay. Texture names are derived from the tier's own CPU affix, which
     * is exactly how the 1.21.1 version's hand-written switch was spelled
     * ({@code exp_crafting_accelerator_<affix>_light}), so no texture had to be renamed.
     */
    private Material getLightMaterial() {
        if (!type.isCPU()) {
            throw new IllegalArgumentException(
                    "Crafting unit type " + type + " does not use a light texture.");
        }
        return texture(type.getCpuAffix() + "_light");
    }

    @Override
    public BlockStateModel bake(MaterialBaker materialBaker) {
        var ringCorner = materialBaker.get(RING_CORNER, this);
        var ringSideHor = materialBaker.get(RING_SIDE_HOR, this);
        var ringSideVer = materialBaker.get(RING_SIDE_VER, this);

        if (type == ExpTiers.UNIT) {
            return new UnitBakedModel(ringCorner, ringSideHor, ringSideVer,
                    materialBaker.get(UNIT_BASE, this));
        }
        return new LightBakedModel(ringCorner, ringSideHor, ringSideVer,
                materialBaker.get(LIGHT_BASE, this),
                materialBaker.get(getLightMaterial(), this));
    }

    private static Material texture(String name) {
        return new Material(Expandedae.makeId("block/crafting/" + name));
    }

    @Override
    public String debugName() {
        return getClass() + "[" + type + "]";
    }

    /**
     * Data-driven replacement for the old {@code BuiltInModelHooks.addBuiltInModel(...)} call.
     * Registered in {@code ExpandedaeClient} against {@code RegisterBlockStateModels} and
     * referenced from the blockstate JSON emitted by {@code ExpModelProvider}.
     */
    public record Unbaked(ExpTiers type) implements CustomUnbakedBlockStateModel {
        public static final Identifier ID = Expandedae.makeId("exp_crafting_cube");

        private static final Codec<ExpTiers> TYPE_CODEC = Codec.STRING.xmap(
                name -> ExpTiers.valueOf(name.toUpperCase(java.util.Locale.ROOT)),
                type -> type.name().toLowerCase(java.util.Locale.ROOT));

        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        TYPE_CODEC.fieldOf("unit_type").forGetter(Unbaked::type))
                        .apply(instance, Unbaked::new));

        @Override
        public BlockStateModel bake(ModelBaker baker) {
            return new ExpCraftingUnitModelProvider(type).bake(baker.materials());
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
        }

        @Override
        public MapCodec<Unbaked> codec() {
            return MAP_CODEC;
        }
    }
}
