package lu.kolja.expandedae.block.block;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import java.util.List;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@ParametersAreNonnullByDefault
public class PlushieBlock extends AEBaseBlock {
    private static VoxelShape SHAPE = Shapes.create(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    // Round 18: MC 1.21.2+ requires the registry-supplied Properties to reach the Block
    // constructor (it carries the block's ResourceKey), so AEBaseBlock.defaultProps now takes the
    // incoming Properties as its first argument instead of creating them.
    public PlushieBlock(Properties props) {
        super(defaultProps(props, MapColor.NONE, SoundType.WOOL).noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    public PlushieBlock(Properties props, double height) {
        this(props);
        SHAPE = Shapes.create(0.0, 0.0, 0.0, 1.0, height, 1.0);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.horizontalFacing();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    protected boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    // Round 18: BlockBehaviour#getLightBlock no longer exists; light occlusion is a Properties
    // value now (BlockBehaviour.Properties#lightBlock). The plushie is noOcclusion() anyway, so
    // the old hard-coded "2" had no remaining effect worth reproducing.

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    // Round 18: BlockBehaviour#propagatesSkylightDown(BlockState, BlockGetter, BlockPos) is gone;
    // sky-light propagation is decided from the block's Properties/shape now, and the block is
    // already noOcclusion(), which is what the old "return true" was expressing.

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
            BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState,
            RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState,
                random);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public static class PlushieBlockItem extends AEBaseBlockItem {
        private final List<Component> lines;
        public PlushieBlockItem(Block id, Properties props, List<Component> lines) {
            super(id, props);
            this.lines = lines;
        }

        @Override
        public void addCheckedInformation(ItemStack itemStack, TooltipContext context,
                java.util.function.Consumer<Component> toolTip, TooltipFlag advancedTooltips) {
            lines.forEach(toolTip);
            super.addCheckedInformation(itemStack, context, toolTip, advancedTooltips);
        }
    }
}
