package com.hutuneko.psi_ex.compat.apo;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.compat.apo.affix.PsiRecoveryAffix;
import com.hutuneko.psi_ex.compat.apo.affix.PsiReductionAffix;
import com.hutuneko.psi_ex.compat.apo.affix.PsiReplayAffix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import moffy.addonapi.AddonModule;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public class ApoCompatModule implements AddonModule {
    public static final DynamicHolder<PsiRecoveryAffix> PSI_RECOVERY = AffixRegistry.INSTANCE.holder(new ResourceLocation(PsiEX.MOD_ID,"cad/special/psi_recovery"));
    public static final DynamicHolder<PsiReductionAffix> PSI_REDUCTION = AffixRegistry.INSTANCE.holder(new ResourceLocation(PsiEX.MOD_ID,"cad/special/psi_reduction"));
    public static final DynamicHolder<PsiReductionAffix> PSI_REPLAY = AffixRegistry.INSTANCE.holder(new ResourceLocation(PsiEX.MOD_ID,"cad/special/psi_replay"));

    public ApoCompatModule() {
        PsiEXLootCategories.init();
        AffixRegistry.INSTANCE.registerCodec(new ResourceLocation(PsiEX.MOD_ID,"psi_recovery"),PsiRecoveryAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(new ResourceLocation(PsiEX.MOD_ID,"psi_reduction"), PsiReductionAffix.CODEC);
        AffixRegistry.INSTANCE.registerCodec(new ResourceLocation(PsiEX.MOD_ID,"psi_replay"), PsiReplayAffix.CODEC);
        MinecraftForge.EVENT_BUS.addListener(PsiRecoveryAffix::onLivingHurt);
        MinecraftForge.EVENT_BUS.addListener(PsiReductionAffix::onSpellCast);
        MinecraftForge.EVENT_BUS.addListener(PsiReplayAffix::onSpellCast);
    }
}

