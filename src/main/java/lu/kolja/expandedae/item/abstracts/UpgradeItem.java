package lu.kolja.expandedae.item.abstracts;

import appeng.blockentity.AEBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class UpgradeItem extends Item {
    public UpgradeItem(Properties pProperties) {
        super(pProperties);
    }

    protected void replaceTile(Level world, BlockPos pos, BlockEntity oldTile, BlockEntity newTile, BlockState newBlock) {
        // Round 18: BlockEntity persistence moved from (CompoundTag, HolderLookup.Provider) to the
        // ValueInput/ValueOutput abstraction. saveWithFullMetadata(HolderLookup.Provider) still
        // exists as a CompoundTag-producing convenience, but the load side is now
        // loadWithComponents(ValueInput), so the tag has to be wrapped in a TagValueInput.
        var contents = oldTile.saveWithFullMetadata(world.registryAccess());
        world.removeBlockEntity(pos);
        world.removeBlock(pos, false);
        world.setBlock(pos, newBlock, 3);
        world.setBlockEntity(newTile);
        newTile.loadWithComponents(
                TagValueInput.create(ProblemReporter.DISCARDING, world.registryAccess(), contents));
        if (newTile instanceof AEBaseBlockEntity aeTile) {
            aeTile.markForUpdate();
        } else {
            newTile.setChanged();
        }
    }
}
