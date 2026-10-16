package lu.kolja.expandedae.block;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.client.gui.AEBaseScreen;
import appeng.util.InteractionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class ExpBlockBaseScreen<T extends AEBaseBlockEntity> extends AEBaseEntityBlock<T> {
    public ExpBlockBaseScreen(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        var be = this.getBlockEntity(level, pos);
        if (be != null) {
            if (!level.isClientSide()) {
                this.openScreen(be, player);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var parent = super.useItemOn(heldItem, state, level, pos, player, hand, hit);
        if (!(parent instanceof InteractionResult.Pass)) {
            return parent;
        }
        if (InteractionUtil.isInAlternateUseMode(player)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        } else {
            var be = this.getBlockEntity(level, pos);
            if (be != null) {
                if (!level.isClientSide()) {
                    this.openScreen(be, player);
                }
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    public abstract void openScreen(T be, Player player);

    @Nullable
    private InteractionResult check(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return null;
    }
}
