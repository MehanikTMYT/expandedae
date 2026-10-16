package lu.kolja.expandedae.block.block;

import appeng.api.networking.IManagedGridNode;
import appeng.block.AEBaseBlock;
import appeng.block.AEBaseEntityBlock;
import appeng.block.crafting.PatternProviderBlock;
import appeng.block.crafting.PushDirection;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import appeng.util.Platform;
import lu.kolja.expandedae.block.ExpBlockBaseScreen;
import lu.kolja.expandedae.block.entity.ExpPatternProviderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class ExpPatternProviderBlock extends ExpBlockBaseScreen<ExpPatternProviderBlockEntity> {
    private static final EnumProperty<PushDirection> PUSH_DIRECTION = PatternProviderBlock.PUSH_DIRECTION;

    public ExpPatternProviderBlock(Properties props) {
        super(AEBaseBlock.metalProps(props));
        registerDefaultState(defaultBlockState().setValue(PUSH_DIRECTION, PushDirection.ALL));
    }

    public static PatternProviderLogic createLogic(IManagedGridNode mainNode, PatternProviderLogicHost host) {
        return new PatternProviderLogic(mainNode, host, 72);
    }

    @Override
    public void openScreen(ExpPatternProviderBlockEntity be, Player player) {
        be.openMenu(player, MenuLocators.forBlockEntity(be));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PUSH_DIRECTION);
    }

    @Override
    protected void neighborChanged(
            BlockState state, Level level, BlockPos pos, Block block,
            @org.jetbrains.annotations.Nullable net.minecraft.world.level.redstone.Orientation orientation,
            boolean movedByPiston) {
        var be = getBlockEntity(level, pos);

        if (be != null) {
            be.getLogic().updateRedstoneState();
        }
    }

    @Override
    protected @NotNull InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (InteractionUtil.canWrenchRotate(heldItem)) {
            this.setSide(level, pos, hit.getDirection());
            return InteractionResult.SUCCESS;
        } else {
            return super.useItemOn(heldItem, state, level, pos, player, hand, hit);
        }
    }

    private void setSide(Level level, BlockPos pos, Direction facing) {
        var currentState = level.getBlockState(pos);
        var pushSide = currentState.getValue(PUSH_DIRECTION).getDirection();
        PushDirection newPushDirection;
        if (pushSide == facing.getOpposite()) {
            newPushDirection = PushDirection.fromDirection(facing);
        } else if (pushSide == facing) {
            newPushDirection = PushDirection.ALL;
        } else if (pushSide == null) {
            newPushDirection = PushDirection.fromDirection(facing.getOpposite());
        } else {
            newPushDirection = PushDirection.fromDirection(Platform.rotateAround(pushSide, facing));
        }
        level.setBlockAndUpdate(pos, currentState.setValue(PUSH_DIRECTION, newPushDirection));
    }
}
