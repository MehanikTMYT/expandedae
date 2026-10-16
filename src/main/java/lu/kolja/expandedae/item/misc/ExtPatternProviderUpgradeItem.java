package lu.kolja.expandedae.item.misc;

import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.AEBasePart;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.parts.PartExPatternProvider;
import com.glodblock.github.extendedae.common.tileentities.TileExPatternProvider;
import lu.kolja.expandedae.item.abstracts.UpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ExtPatternProviderUpgradeItem extends UpgradeItem {
    public ExtPatternProviderUpgradeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
            Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.accept(Component.translatable("item.expandedae.upgrade.tooltip",
                        "an Extended Pattern Provider\n to an Expanded Pattern Provider")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, tooltipFlag);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        var pos = context.getClickedPos();
        var world = context.getLevel();
        var entity = world.getBlockEntity(pos);
        if (entity != null) {
            var ctx = new BlockPlaceContext(context);
            var tClazz = entity.getClass();
            if (tClazz == TileExPatternProvider.class) {

                var originState = world.getBlockState(pos);
                // Round 18: ExtendedAE 26.1.2 exposes these as DeferredBlock/DeferredItem holders.
                var state = EAESingletons.EX_PATTERN_PROVIDER.get().getStateForPlacement(ctx);
                //var state = ExpBlocks.EXP_PATTERN_PROVIDER.block().getStateForPlacement(ctx);
                if (state == null) {
                    return InteractionResult.PASS;
                }
                // Round 18: BlockState#getValues() now returns Stream<Property.Value<?>> instead of
                // an ImmutableMap<Property<?>, Comparable<?>>.
                for (var sp : originState.getValues().toList()) {
                    var pt = sp.property();
                    var va = sp.value();
                    try {
                        if (state.hasProperty(pt)) {
                            state = state.<Comparable, Comparable>setValue((Property) pt, va);
                        }
                    } catch (Exception ignore) {
                        // NO-OP
                    }
                }

                BlockEntityType<?> tileType = EAESingletons.EX_PATTERN_PROVIDER.get().getBlockEntityType();
                BlockEntity te = tileType.create(pos, state);
                replaceTile(world, pos, entity, te, state);
                context.getItemInHand().shrink(1);
                return InteractionResult.CONSUME;

            } else if (entity instanceof CableBusBlockEntity cable) {
                Vec3 hitVec = context.getClickLocation();
                Vec3 hitInBlock = new Vec3(hitVec.x - pos.getX(), hitVec.y - pos.getY(), hitVec.z - pos.getZ());
                var part = cable.getCableBus().selectPartLocal(hitInBlock).part;
                if (part instanceof AEBasePart basePart
                        && (part.getClass() == PartExPatternProvider.class)) {
                    var side = basePart.getSide();
                    var partItem = EAESingletons.EX_PATTERN_PROVIDER_PART.get();

                    // Round 18: IPart persistence is ValueOutput/ValueInput based now.
                    var output = TagValueOutput.createWithContext(
                            ProblemReporter.DISCARDING, world.registryAccess());
                    part.writeToNBT(output);
                    var contents = output.buildResult();
                    var p = cable.replacePart(partItem, side, context.getPlayer(), (net.minecraft.world.InteractionHand) null);
                    if (p != null) {
                        p.readFromNBT(TagValueInput.create(
                                ProblemReporter.DISCARDING, world.registryAccess(), contents));
                    }
                } else {
                    return InteractionResult.PASS;
                }
                context.getItemInHand().shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}