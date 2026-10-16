package lu.kolja.expandedae.terminal.wtlib;

import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.helpers.IPatternTerminalLogicHost;
import appeng.helpers.IPatternTerminalMenuHost;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.parts.encoding.PatternEncodingLogic;
import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public class ExpWETMenuHost extends WTMenuHost
        implements IViewCellStorage, IPatternTerminalMenuHost, IPatternTerminalLogicHost {
    private final PatternEncodingLogic logic = new PatternEncodingLogic(this);

    public ExpWETMenuHost(ItemWT item, Player player, ItemMenuHostLocator locator,
                          BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        // Round 18: PatternEncodingLogic persistence is ValueInput/ValueOutput based. The AE2WTLib
        // data component is still a CompoundTag, so it is wrapped rather than replaced.
        logic.readFromNBT(TagValueInput.create(
                ProblemReporter.DISCARDING,
                player.registryAccess(),
                getItemStack().getOrDefault(AE2wtlibComponents.PATTERN_ENCODING_LOGIC, new CompoundTag())));
    }

    @Override
    public PatternEncodingLogic getLogic() {
        return logic;
    }

    @Override
    public Level getLevel() {
        return getPlayer().level();
    }

    @Override
    public void markForSave() {
        var output = TagValueOutput.createWithContext(
                ProblemReporter.DISCARDING, getPlayer().registryAccess());
        logic.writeToNBT(output);
        getItemStack().set(AE2wtlibComponents.PATTERN_ENCODING_LOGIC, output.buildResult());
    }
}