package com.hutuneko.psi_ex.compat;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.entity.*;
import com.hutuneko.psi_ex.item.*;
import com.hutuneko.psi_ex.spell.selector.PieceSelector_ItemData;
import com.hutuneko.psi_ex.spell.trick.*;
import moffy.addonapi.AddonModule;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import vazkii.psi.api.PsiAPI;

public class DefaultCompatModule extends AddonModule {
    public DefaultCompatModule() {
//        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "pieceselector_data"), PieceSelector_data.class);
//        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "copy"), PieceTrick_copy.class);
//        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "eidos_renewal"), PieceTrick_Eidos_renewal.class);
        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "coordinate_eidos_renewal"), PieceTrick_coordinate_eidos_renewal.class);
        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "pieceselector_itemdata"), PieceSelector_ItemData.class);
        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_offhandattack"), PieceTrick_OffhandAttack.class);
        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_oredouble"), PieceTrick_OreDouble.class);
        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_ejection"), PieceTrick_Ejection.class);
//        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_lunastrike"), PieceTrick_LunaStrike.class);
//        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "pieceoperator_dirchange"), PieceTrick_DirChange.class);
//        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_seve_number"), PieceTrick_Seve_Number.class);
//        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "pieceoperator_getseve_number"), PieceOperator_getSeve_Number.class);
//        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_poisonousbee"), PieceTrick_PoisonousBee.class);
//        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_compressedair"), PieceTrick_CompressedAir.class);

//        PsiEXRegistry.STORAGE = PsiEXRegistry.ITEMS.register("storage", () ->
//                new ItemStorage(new Item.Properties().stacksTo(1))
//        );
        PsiEXRegistry.CAST_SCROLL = PsiEXRegistry.ITEMS.register("cast_scroll", () ->
                new Item(new Item.Properties().stacksTo(1))
        );
        PsiEXRegistry.PERSONAL_TUNER = PsiEXRegistry.ITEMS.register("personal_tuner", () ->
                new ItemPersonalTuner(new Item.Properties().stacksTo(1))
        );
        PsiEXRegistry.PSI_ARROW = PsiEXRegistry.ITEMS.register("psi_arrow", () ->
                new PsiArrowItem(new Item.Properties()));
        PsiEXRegistry.PSI_NEEDLE_DART = PsiEXRegistry.ITEMS.register("psi_needle", () ->
                new ItemNeedleDart(new Item.Properties()));
//        PsiEXRegistry.PSI_BOW = PsiEXRegistry.ITEMS.register("psi_bow", () ->
//                new PsiBow(new Item.Properties().stacksTo(1)));

        PsiEXRegistry.PSI_ARROW_ENTITY = PsiEXRegistry.ENTITIES.register("psi_arrow_entity", () ->
                EntityType.Builder.<PsiArrowEntity>of(PsiArrowEntity::new, MobCategory.MISC)
                        .sized(0.5F, 0.5F)
                        .clientTrackingRange(4)
                        .updateInterval(20)
                        .build("psi_arrow_entity"));

        PsiEXRegistry.PSI_NEEDLE_DARTENTITY = PsiEXRegistry.ENTITIES.register("psi_needle_dartentity", () ->
                EntityType.Builder.<PsiNeedleDartEntity>of(PsiNeedleDartEntity::new, MobCategory.MISC)
                        .sized(0.1F, 0.1F)
                        .clientTrackingRange(4)
                        .updateInterval(20)
                        .build(new ResourceLocation(PsiEX.MOD_ID, "psi_needle_dartentity").toString()));

//        PsiEXRegistry.PSI_TEST_ENTITY = PsiEXRegistry.ENTITIES.register("dummy_villager",
//                () -> EntityType.Builder.of(PsiTestEntity::new, MobCategory.MISC)
//                        .sized(0.6f, 1.95f)      // 村人サイズ
//                        .clientTrackingRange(8)
//                        .build(new ResourceLocation(PsiEX.MOD_ID, "dummy_villager").toString()));
        PsiEXRegistry.CREATIVE_TAB_ITEMS = PsiEXRegistry.TABS.register(PsiEX.MOD_ID, () ->
                CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.tab." + PsiEX.MOD_ID))
                        .icon(() -> new ItemStack(PsiEXRegistry.PSI_ARROW.get()))
                        .displayItems((params, output) -> {
                            for (RegistryObject<Item> regObj : PsiEXRegistry.ITEMS.getEntries()) {
                                if (!(regObj == PsiEXRegistry.CAST_SCROLL)){
                                    output.accept(regObj.get());
                                }
                            }
                        })
                        .build()
        );
        PsiEXRegistry.PSI_COMPRESSIONAIR_ENTITY =
                PsiEXRegistry.ENTITIES.register("needle_projectile",
                        () -> EntityType.Builder
                                .<PsiAirEntity>of(PsiAirEntity::new, MobCategory.MISC)
                                .sized(0.25F, 0.25F) // ヒットボックス
                                .clientTrackingRange(64)
                                .updateInterval(10)
                                .build(new ResourceLocation(PsiEX.MOD_ID, "needle_projectile").toString()));
        PsiEXRegistry.PSI_FAKE_DAMAGE = ResourceKey.create(
                Registries.DAMAGE_TYPE, new ResourceLocation(PsiEX.MOD_ID, "psi_fake_damage")
        );
    }
}
