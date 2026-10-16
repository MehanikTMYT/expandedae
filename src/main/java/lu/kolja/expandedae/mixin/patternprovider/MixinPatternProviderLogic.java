package lu.kolja.expandedae.mixin.patternprovider;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IGrid;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.IConfigManager;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.helpers.patternprovider.PatternProviderTarget;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.util.ConfigManager;
import com.llamalad7.mixinextras.sugar.Local;
import lu.kolja.expandedae.definition.ExpItems;
import lu.kolja.expandedae.definition.ExpSettings;
import lu.kolja.expandedae.enums.ADDONS;
import lu.kolja.expandedae.enums.BlockingMode;
import lu.kolja.expandedae.helper.pattern.IPatternProviderLogic;
import lu.kolja.expandedae.helper.pattern.PatternProviderTargetCache;
import lu.kolja.expandedae.mixin.accessor.AccessorCraftingCpuLogic;
import lu.kolja.expandedae.mixin.accessor.AccessorExecutingCraftingJob;
import lu.kolja.expandedae.xmod.advancedae.AdvancedAE;
import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(value = PatternProviderLogic.class, remap = false, priority = 1001)
public abstract class MixinPatternProviderLogic implements IUpgradeableObject, IPatternProviderLogic {
    @Unique
    private static final boolean AAE_LOADED = ADDONS.ADV.isLoaded();

    @Unique
    private PatternProviderTargetCache[] expandedae$targetCaches;

    @Shadow @Final private IActionSource actionSource;

    @Unique
    private IUpgradeInventory eae_$upgrades = UpgradeInventories.empty();

    @Final
    @Shadow
    private PatternProviderLogicHost host;

    @Final
    @Shadow
    private IManagedGridNode mainNode;

    @Shadow public abstract @Nullable IGrid getGrid();

    @Shadow @Final private IConfigManager configManager;

    @Unique
    private void eae_$onUpgradesChanged() {
        /*
        if (!eae_$upgrades.isInstalled(ExpItems.SMART_BLOCKING_CARD)) { //TODO: smart card unlocks extra blocking modes
            assert Minecraft.getInstance().screen != null;
            ((IBlockingMode) Minecraft.getInstance().screen).setVisible(false);
        } else {
            assert Minecraft.getInstance().screen != null;
            ((IBlockingMode) Minecraft.getInstance().screen).setVisible(true);
        }*/
        this.host.saveChanges();
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.eae_$upgrades;
    }

    @Inject(
            method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;I)V",
            at = @At("TAIL")
    )
    private void eae_$initUpgrade(IManagedGridNode mainNode, PatternProviderLogicHost host, int patternInventorySize, CallbackInfo ci) {
        eae_$upgrades = UpgradeInventories.forMachine(host.getTerminalIcon().getItem(), 1, this::eae_$onUpgradesChanged);
        this.expandedae$targetCaches = new PatternProviderTargetCache[6];
    }

    /*
     * Round 18 - the mixin Round 16 flagged as a guaranteed startup crash.
     *
     * 1.21.1 target: PatternProviderLogic#writeToNBT(CompoundTag, HolderLookup.Provider)
     * 26.1.2 target: PatternProviderLogic#writeToNBT(ValueOutput)
     *
     * Why the injection exists at all: this mixin gives every vanilla AE2 Pattern Provider an
     * extra IUpgradeInventory (eae_$upgrades) that AE2 knows nothing about. It has to be persisted
     * alongside the provider's own state, in the provider's own NBT subtree, or the installed
     * upgrade cards vanish on world reload. AE2 26.1.2 still offers no public
     * "extra addon data" persistence hook on PatternProviderLogic or PatternProviderLogicHost
     * (checked: PatternProviderLogicHost exposes only getLogic/getBlockEntity/getTargets/
     * saveChanges/config/priority), so per brief section 89 the correct option is (4) a narrow
     * updated mixin, not (5) an AE2 API extension and not a broad HEAD/TAIL hack. The injection
     * point is unchanged in kind - still TAIL of the same logical method - only the descriptor and
     * the persistence primitive change: IUpgradeInventory itself moved to
     * writeToNBT(ValueOutput, String) / readFromNBT(ValueInput, String).
     */
    @Inject(
            method = "writeToNBT(Lnet/minecraft/world/level/storage/ValueOutput;)V",
            at = @At("TAIL")
    )
    private void eae_$saveUpgrade(ValueOutput output, CallbackInfo ci) {
        this.eae_$upgrades.writeToNBT(output, "upgrades");
    }

    @Inject(
            method = "readFromNBT(Lnet/minecraft/world/level/storage/ValueInput;)V",
            at = @At("TAIL")
    )
    private void eae_$loadUpgrade(ValueInput input, CallbackInfo ci) {
        this.eae_$upgrades.readFromNBT(input, "upgrades");
    }

    @Inject(
            method = "addDrops",
            at = @At("TAIL")
    )
    private void eae_$dropUpgrade(List<ItemStack> drops, CallbackInfo ci) {
        for (var is : this.eae_$upgrades) if (!is.isEmpty()) drops.add(is);
    }

    @Inject(
            method = "clearContent",
            at = @At("TAIL")
    )
    private void eae_$clearUpgrade(CallbackInfo ci) {
        this.eae_$upgrades.clear();
    }

    @Inject(method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;I)V",
            at = @At("TAIL"),
            remap = false)
    private void PatternProviderLogic(IManagedGridNode mainNode, PatternProviderLogicHost host, int patternInventorySize, CallbackInfo ci) {
        eae$getConfigManager().registerSetting(ExpSettings.BLOCKING_MODE, BlockingMode.DEFAULT);
    }

    @Override
    public BlockingMode expandedae$getBlockingMode() {
        return configManager.getSetting(ExpSettings.BLOCKING_MODE);
    }

    /**
     * @author Kolja
     * @reason Better blocking modes without invasive overwrites
     */
    @Overwrite
    @Nullable
    private PatternProviderTarget findAdapter(Direction side) {
        if (this.expandedae$targetCaches[side.get3DDataValue()] == null) {
            BlockEntity thisBe = this.host.getBlockEntity();
            this.expandedae$targetCaches[side.get3DDataValue()] = new PatternProviderTargetCache(
                    (ServerLevel) thisBe.getLevel(),
                    thisBe.getBlockPos().relative(side),
                    side.getOpposite(),
                    this.actionSource,
                    eae$getConfigManager()
            );
        }
        return this.expandedae$targetCaches[side.get3DDataValue()].find();
    }

    @Inject(
            method = "pushPattern",
            at = @At("RETURN")
    )
    private void expandedae$onPushPatternSuccess(IPatternDetails patternDetails, KeyCounter[] inputHolder, CallbackInfoReturnable<Boolean> cir) {
        // Upstream 6f5a9d1: only after the pattern was actually pushed.
        if (cir.getReturnValue()) {
            expandedae$tryAutoCompleteCraft(patternDetails);
        }
    }

    @Unique
    private void expandedae$tryAutoCompleteCraft(IPatternDetails details) {
        if (!eae_$upgrades.isInstalled(ExpItems.AUTO_COMPLETE_CARD)) return;
        var cpus = getGrid().getCraftingService().getCpus();
        for (var cpu : cpus) {
            if (!cpu.isBusy()) continue;
            if (cpu instanceof CraftingCPUCluster cluster) {
                var task = ((AccessorExecutingCraftingJob) ((AccessorCraftingCpuLogic) cluster.craftingLogic).getJob()).getTasks().get(details);
                if (task != null && task.getValue() <= 1) {
                    ((AccessorCraftingCpuLogic) cluster.craftingLogic).eae$finishJob(true);
                    return;
                }
                continue;
            }
            if (!AAE_LOADED) continue;
            AdvancedAE.handleCpu(cpu, details);
        }
    }

    @ModifyArg(
            method = "pushPattern",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/helpers/patternprovider/PatternProviderTarget;containsPatternInput(Ljava/util/Set;)Z"
            )
    )
    private Set<AEKey> modifiedContainsPatternInput(Set<AEKey> patternInputs, @Local(argsOnly = true) IPatternDetails patternDetails) {
        if (expandedae$getBlockingMode() != BlockingMode.SMART) return patternInputs;
        // This is more efficient than streams, even tho it's a minimal difference,
        // since this is a high-frequency call I'd rather do it like this
        var result = new HashSet<AEKey>();
        for (var input : patternDetails.getInputs()) {
            result.add(input.getPossibleInputs()[0].what());
        }
        return result;
    }

    @Unique
    private ConfigManager eae$getConfigManager() {
        return (ConfigManager) configManager;
    }
}