package io.github.hutuneko.psi_ex.compat;

import io.github.hutuneko.psi_ex.PsiEX;
import io.github.hutuneko.psi_ex.api.CadBehavior;
import io.github.hutuneko.psi_ex.api.KeyBindings;
import io.github.hutuneko.psi_ex.api.PsiEXAPI;
import io.github.hutuneko.psi_ex.api.menu.IndexMenu;
import io.github.hutuneko.psi_ex.block.IndexBlock;
import io.github.hutuneko.psi_ex.block.MultiPageProgrammer;
import io.github.hutuneko.psi_ex.block.MultiPageTileProgrammer;
import io.github.hutuneko.psi_ex.api.menu.GPTCADSettingMenu;
import io.github.hutuneko.psi_ex.client.gui.IndexGUI;
import io.github.hutuneko.psi_ex.effect.CastJammingEffect;
import io.github.hutuneko.psi_ex.entity.*;
import io.github.hutuneko.psi_ex.item.*;
import io.github.hutuneko.psi_ex.item.ItemNeedleDart;
import io.github.hutuneko.psi_ex.item.ItemPersonalTuner;
import io.github.hutuneko.psi_ex.item.PsiArrowItem;
import io.github.hutuneko.psi_ex.item.PsiKiller;
import io.github.hutuneko.psi_ex.net.C2SShutdown;
import io.github.hutuneko.psi_ex.net.Net;
import io.github.hutuneko.psi_ex.recipe.NbtAddRecipe;
import io.github.hutuneko.psi_ex.spell.operator.OperatorGetSeveNumber;
import io.github.hutuneko.psi_ex.spell.operator.OperatorGetSeveVector3;
import io.github.hutuneko.psi_ex.spell.selector.SelectorItemData;
import io.github.hutuneko.psi_ex.spell.trick.*;
import io.github.hutuneko.psi_ex.system.CuriosUtil;
import moffy.addonapi.AddonAPI;
import moffy.addonapi.AddonModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.common.item.ItemCAD;

public class DefaultCompatModule implements AddonModule {
    public DefaultCompatModule() {
//        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "pieceselector_data"), PieceSelector_data.class);
//        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "copy"), PieceTrick_copy.class);
//        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "eidos_renewal"), PieceTrick_Eidos_renewal.class);
        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "coordinate_eidos_renewal"), TrickCoordinateEidosRenewal.class);
        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "pieceselector_itemdata"), SelectorItemData.class);
        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_offhandattack"), TrickOffhandAttack.class);
        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_oredouble"), TrickOreDouble.class);
        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_ejection"), TrickEjection.class);
//        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_lunastrike"), PieceTrick_LunaStrike.class);
//        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "pieceoperator_dirchange"), PieceTrick_DirChange.class);
        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_seve_number"), TrickSeveNumber.class);
        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "pieceoperator_getseve_number"), OperatorGetSeveNumber.class);
        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_seve_vector"), TrickSeveVector.class);
        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "pieceoperator_getseve_vector3"), OperatorGetSeveVector3.class);
        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_poisonousbee"), TrickPoisonousBee.class);
//        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_compressedair"), PieceTrick_CompressedAir.class);
        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID,"piecetrick_selfbigexplosion"), TrickSelfBigExplosion.class);
        PsiEXAPI.pieceRegister(new ResourceLocation(PsiEX.MOD_ID,"piecetrick_railgun"), TrickRailgun.class);
//        PsiEXRegistry.STORAGE = PsiEXRegistry.ITEMS.register("storage", () ->
//                new ItemStorage(new Item.Properties().stacksTo(1))
//        );
        PsiEXRegistry.CAST_SCROLL = PsiEXRegistry.ITEMS.register("cast_scroll", () ->
                new Item(new Item.Properties().stacksTo(1))
        );
        PsiEXRegistry.CAD_PATCH = PsiEXRegistry.ITEMS.register("cad_patch", () ->
                new Item(new Item.Properties())
        );
        PsiEXRegistry.PERSONAL_TUNER = PsiEXRegistry.ITEMS.register("personal_tuner", () ->
                new ItemPersonalTuner(new Item.Properties().stacksTo(1))
        );
        PsiEXRegistry.PIECE_PROGRAM = PsiEXRegistry.ITEMS.register("piece_program", () ->
                new Item(new Item.Properties().stacksTo(1))
        );
        PsiEXRegistry.PSIKILLER = PsiEXRegistry.ITEMS.register("psikiller", () ->
                new PsiKiller(Tiers.WOOD,1,1,new Item.Properties().stacksTo(1).defaultDurability(Integer.MAX_VALUE))
        );
        PsiEXRegistry.PSI_ARROW = PsiEXRegistry.ITEMS.register("psi_arrow", () ->
                new PsiArrowItem(new Item.Properties()));
        PsiEXRegistry.PSI_NEEDLE_DART = PsiEXRegistry.ITEMS.register("psi_needle", () ->
                new ItemNeedleDart(new Item.Properties()));
        PsiEXRegistry.ITEMS.register("multipageprogrammer", () ->
                new BlockItem(PsiEXRegistry.MULTIPAGEPROGRAMMER.get(), new Item.Properties().stacksTo(1)));
        PsiEXRegistry.ITEMS.register("index", () ->
                new BlockItem(PsiEXRegistry.INDEX.get(), new Item.Properties()));
//        PsiEXRegistry.PSI_BOW = PsiEXRegistry.ITEMS.register("psi_bow", () ->
//                new PsiBow(new Item.Properties().stacksTo(1)));

        PsiEXRegistry.MULTIPAGEPROGRAMMER = PsiEXRegistry.BLOCKS.register("multipageprogrammer",() ->
                new MultiPageProgrammer(BlockBehaviour.Properties.of()));
        PsiEXRegistry.MULTI_PROGRAMMER =
                PsiEXRegistry.BLOCK_ENTITIES.register("multi_programmer",
                        () -> BlockEntityType.Builder.of(MultiPageTileProgrammer::new, PsiEXRegistry.MULTIPAGEPROGRAMMER.get())
                                .build(null));
        PsiEXRegistry.INDEX =
                PsiEXRegistry.BLOCKS.register("index",() ->
                        new IndexBlock(BlockBehaviour.Properties.of()));

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
        PsiEXRegistry.PSI_COMPRESSIONAIR_ENTITY =
                PsiEXRegistry.ENTITIES.register("psi_compressionair_entity",
                        () -> EntityType.Builder
                                .<PsiAirEntity>of(PsiAirEntity::new, MobCategory.MISC)
                                .sized(0.25F, 0.25F) // ヒットボックス
                                .clientTrackingRange(64)
                                .updateInterval(10)
                                .build(new ResourceLocation(PsiEX.MOD_ID, "psi_compressionair_entity").toString()));
        PsiEXRegistry.RAILGUN =
                PsiEXRegistry.ENTITIES.register("railgun",
                        () -> EntityType.Builder
                                .<Railgun>of(Railgun::new, MobCategory.MISC)
                                .sized(0.25F, 0.25F) // ヒットボックス
                                .clientTrackingRange(64)
                                .updateInterval(10)
                                .build(new ResourceLocation(PsiEX.MOD_ID, "railgun").toString()));
        PsiEXRegistry.NBT_ADDING_SERIALIZER =
                PsiEXRegistry.SERIALIZERS.register("nbt_adding", () -> new SimpleCraftingRecipeSerializer<>(NbtAddRecipe::new));
        PsiEXRegistry.CASTJAMMING =
                PsiEXRegistry.MOB_EFFECTS.register("castjamming",() -> new CastJammingEffect(MobEffectCategory.BENEFICIAL,0xFF0000));

        PsiEXRegistry.GPTCAD_SETTING_MENU =
                PsiEXRegistry.MENUS.register("gptcad_setting_menu",() ->
                        IForgeMenuType.create(GPTCADSettingMenu::new));
        PsiEXRegistry.INDEX_MENU =
                PsiEXRegistry.MENUS.register("index_menu",() ->
                        IForgeMenuType.create(IndexMenu::fromNetwork));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> this::cevents);
    }
    @OnlyIn(Dist.CLIENT)
    private void cevents(){
         var ctx = FMLJavaModLoadingContext.get().getModEventBus();
         ctx.addListener(this::onRegisterRenderers);
         ctx.addListener(this::onClientSetup);
         MinecraftForge.EVENT_BUS.addListener(this::shutdown);

    }
    @OnlyIn(Dist.CLIENT)
    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers e){
        e.registerEntityRenderer(
                PsiEXRegistry.PSI_ARROW_ENTITY.get(),
                PsiArrowRenderer::new
        );
        e.registerEntityRenderer(
                PsiEXRegistry.PSI_NEEDLE_DARTENTITY.get(),
                ThrownItemRenderer::new
        );
        e.registerEntityRenderer(
                PsiEXRegistry.PSI_COMPRESSIONAIR_ENTITY.get(),
                ThrownItemRenderer::new
        );
        e.registerEntityRenderer(
                PsiEXRegistry.RAILGUN.get(),
                RailgunRenderer::new
        );
    }
    @OnlyIn(Dist.CLIENT)
    public void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(PsiEXRegistry.INDEX_MENU.get(), IndexGUI::new);
        });
    }
    public void shutdown(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        if (mc.screen != null) return;

        while (KeyBindings.SHUTDOWN_KEY.consumeClick()) {

            if (AddonAPI.isModuleAvailable(new ResourceLocation(PsiEX.MOD_ID, "curioscompat"))) {
                var result = CuriosUtil.findFirst(player, s -> s.getItem() instanceof GeneralPurposeTypeCAD);
            }
            ItemStack stack = findCADToToggle(player);
            if (stack.isEmpty()) {
                return;
            }

            toggleCAD(stack, player);
        }
    }

    /** CADを探す：メインハンド優先、なければCurios */
    private ItemStack findCADToToggle(LocalPlayer player) {
        ItemStack mainHand = player.getMainHandItem();
        if (CadBehavior.isCAD(mainHand) || mainHand.getItem() instanceof ItemCAD) {
            return mainHand;
        }

        if (AddonAPI.isModuleAvailable(new ResourceLocation(PsiEX.MOD_ID, "curioscompat"))) {
            var result = CuriosUtil.findFirst(player,
                    itemStack -> itemStack.getItem() instanceof GeneralPurposeTypeCAD);
            if (result.isPresent()) {
                ItemStack curiosStack = result.get().stack();
                if (CadBehavior.isCAD(curiosStack) || curiosStack.getItem() instanceof ItemCAD) {
                    return curiosStack;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    /** CADのON/OFFを切り替え。true=電源OFF、false=電源ON */
    private void toggleCAD(ItemStack stack, LocalPlayer player) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("psi_ex.isshutdown")) {
            tag.putBoolean("psi_ex.isshutdown", false); // デフォルトはON（false）
        }

        boolean currentShutdown = tag.getBoolean("psi_ex.isshutdown");
        boolean newShutdown = !currentShutdown; // trueならfalseに、falseならtrueに

        Net.CHANNEL.sendToServer(new C2SShutdown(newShutdown));
        tag.putBoolean("psi_ex.isshutdown", newShutdown);

        String stateText = newShutdown ? "OFF" : "ON";
        player.displayClientMessage(
                Component.translatable("psi_ex.isshutdown")
                        .append(stateText)
                        .append(" ")
                        .append(stack.getDisplayName().getString()),
                true
        );
    }
}
