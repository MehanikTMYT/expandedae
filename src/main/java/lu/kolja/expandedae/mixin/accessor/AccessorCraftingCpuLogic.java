package lu.kolja.expandedae.mixin.accessor;

import appeng.crafting.execution.CraftingCpuLogic;
import appeng.crafting.execution.ExecutingCraftingJob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = CraftingCpuLogic.class, remap = false)
public interface AccessorCraftingCpuLogic {
    @Accessor("job")
    ExecutingCraftingJob getJob();

    // Upstream 6f5a9d1: finish (not cancel) the job when the auto-complete card fires.
    @Invoker("finishJob")
    void eae$finishJob(boolean success);
}