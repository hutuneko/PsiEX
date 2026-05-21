package io.github.hutuneko.psi_ex.compat.curios;

import moffy.addonapi.AddonMixinPlugin;

public class CuriosMixinPlugin extends AddonMixinPlugin {
    @Override
    public String[] getRequiredModIds() {
        return new String[]{"curios"};
    }
}
