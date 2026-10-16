package lu.kolja.expandedae.mixin.compat.advancedae;

import net.pedroksl.advanced_ae.common.logic.AdvCraftingCPULogic;
import net.pedroksl.advanced_ae.common.logic.ExecutingCraftingJob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AdvCraftingCPULogic.class, remap = false)
public interface AAEAccessorAdvCraftingCPULogic {
    @Accessor("job")
    ExecutingCraftingJob getJob();

    // Upstream 6f5a9d1: finish (not cancel) the job when the auto-complete card fires.
    @Invoker("finishJob")
    void eae$finishJob(boolean success);
}
