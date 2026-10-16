package lu.kolja.expandedae.xmod;

import lu.kolja.expandedae.enums.ADDONS;
import lu.kolja.expandedae.xmod.advancedae.AdvancedAE;
import lu.kolja.expandedae.xmod.appflux.AppFlux;
import lu.kolja.expandedae.xmod.extendedae.ExtendedAE;

public class XMod {
    public XMod() {
        for (var mod : ADDONS.values()) {
            if (!mod.isLoaded()) continue;
            switch (mod) {
                case EXT -> new ExtendedAE();
                // Round 18: MEGA Cells has no 26.1.2 artifact; lu.kolja.expandedae.xmod.megacells
                // is excluded from the source set (see build.gradle). Restore this case together
                // with that exclusion when a 26.1.2 MEGA Cells build exists.
                case MEGA -> { }
                case APPFLUX -> new AppFlux();
                case ADV -> new AdvancedAE();
            }
        }
    }
}
