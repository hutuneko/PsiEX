package io.github.hutuneko.psi_ex.compat.botania;

import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import moffy.addonapi.AddonModule;
import net.minecraft.world.item.Item;
import io.github.hutuneko.psi_ex.item.ItemPsiManaLens;

public class BotaniaCompatModule implements AddonModule {
    public BotaniaCompatModule() {
        PsiEXRegistry.PSI_MANA_LENS = PsiEXRegistry.ITEMS.register("psi_mana_lens", () ->
                new ItemPsiManaLens(new Item.Properties().stacksTo(1))
        );
    }
}

