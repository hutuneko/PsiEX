package com.hutuneko.psi_ex.compat.curios;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.entity.PsiBarrierEntity;
import com.hutuneko.psi_ex.entity.PsiBarrierRenderer;
import com.hutuneko.psi_ex.item.CuriosItem;
import com.hutuneko.psi_ex.item.PsiCuriosbullet;
import com.hutuneko.psi_ex.spell.operator.PieceOperator_getSpell;
import com.hutuneko.psi_ex.spell.selector.PieceSelector_getEye;
import com.hutuneko.psi_ex.spell.trick.PieceTrick_ExecuteSpell;
import com.hutuneko.psi_ex.spell.trick.PieceTrick_EyeSave;
import com.hutuneko.psi_ex.spell.trick.PieceTrick_SummonBarrier;
import moffy.addonapi.AddonModule;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.psi.api.PsiAPI;

public class CuriosCompatModule implements AddonModule {
    public CuriosCompatModule() {
        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "pieceoperator_getspell"), PieceOperator_getSpell.class);
        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_executespell"), PieceTrick_ExecuteSpell.class);
        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_summonbarrier"), PieceTrick_SummonBarrier.class);
        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_eyesave"), PieceTrick_EyeSave.class);
        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_geteye"), PieceSelector_getEye.class);


        PsiEXRegistry.PSI_CURIO_BULLET = PsiEXRegistry.ITEMS.register("extended_cad_socket", () ->
                new PsiCuriosbullet(new Item.Properties().stacksTo(1))
        );
        PsiEXRegistry.PSI_SPIRITS_EYE = PsiEXRegistry.ITEMS.register("psi_spirits_eye", () ->
                new CuriosItem(new Item.Properties().stacksTo(1)));
        PsiEXRegistry.PSI_BRRIER_ENTITY = PsiEXRegistry.ENTITIES.register("barrier",
                () -> EntityType.Builder.of(PsiBarrierEntity::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .clientTrackingRange(32)
                        .updateInterval(1)
                        .build(new ResourceLocation(PsiEX.MOD_ID, "barrier").toString()));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> this::cevents);
    }
    @OnlyIn(Dist.CLIENT)
    private void cevents(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterRenderers);
    }
    @OnlyIn(Dist.CLIENT)
    public void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers e){
        e.registerEntityRenderer(PsiEXRegistry.PSI_BRRIER_ENTITY.get(), PsiBarrierRenderer::new);
    }
}

