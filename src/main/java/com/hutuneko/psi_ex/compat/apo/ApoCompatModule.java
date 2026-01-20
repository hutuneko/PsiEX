package com.hutuneko.psi_ex.compat.apo;

import moffy.addonapi.AddonModule;

public class ApoCompatModule implements AddonModule {
    public ApoCompatModule() {
        PsiEXLootCategories.init();
    }
}

