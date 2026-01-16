package com.hutuneko.psi_ex.compat.tic;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.item.TiCCAD;
import com.hutuneko.psi_ex.spell.trick.PieceTrick_TiCAttack;
import moffy.addonapi.AddonModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import vazkii.psi.api.PsiAPI;

import java.util.function.Supplier;

public class TiCCompatModule implements AddonModule {
    public static ItemObject<ToolPartItem> TIC_CAD_CORE = null;
    public static ItemObject<ToolPartItem> TIC_CAD_SOCKET = null;
    public static ItemObject<ToolPartItem> TIC_CAD_ASSEMBLY = null;
    public static ItemObject<ToolPartItem> TIC_CAD_BATTERY = null;
    public static ItemObject<TiCCAD> TICCAD = null;
    public static CastItemObject TIC_CAD_CORE_CAST;
    public static CastItemObject TIC_CAD_SOCKET_CAST;
    public static CastItemObject TIC_CAD_ASSEMBLY_CAST;
    public static CastItemObject TIC_CAD_BATTERY_CAST;
    public TiCCompatModule(){
        
        final MaterialStatsId PSIEX_MATERIAL_ID = new MaterialStatsId(PsiEX.MOD_ID, "psiex_material");

        TIC_CAD_CORE = new ItemObject<>(PsiEXRegistry.ITEMS.register("tic_cad_core",
                () -> new ToolPartItem(new Item.Properties(), HeadMaterialStats.ID)));
        TIC_CAD_ASSEMBLY = new ItemObject<>(PsiEXRegistry.ITEMS.register("tic_cad_assembly",
                () -> new ToolPartItem(new Item.Properties(),HeadMaterialStats.ID)));
        TIC_CAD_BATTERY = new ItemObject<>(PsiEXRegistry.ITEMS.register("tic_cad_battery",
                () -> new ToolPartItem(new Item.Properties(),HeadMaterialStats.ID)));
        TIC_CAD_SOCKET = new ItemObject<>(PsiEXRegistry.ITEMS.register("tic_cad_socket",
                () -> new ToolPartItem(new Item.Properties(),HeadMaterialStats.ID)));
        TICCAD = new ItemObject<>(PsiEXRegistry.ITEMS.register("tic_cad",
                () -> new TiCCAD(new Item.Properties())));
        TIC_CAD_CORE_CAST = castItemObject("tic_cad_core",() -> new Item(new Item.Properties()));
        TIC_CAD_ASSEMBLY_CAST = castItemObject("tic_cad_assembly",() -> new Item(new Item.Properties()));
        TIC_CAD_BATTERY_CAST = castItemObject("tic_cad_battery",() -> new Item(new Item.Properties()));
        TIC_CAD_SOCKET_CAST = castItemObject("tic_cad_socket",() -> new Item(new Item.Properties()));
        MinecraftForge.EVENT_BUS.addListener(TiCEvent::onDamage);
        MinecraftForge.EVENT_BUS.addListener(TiCEvent::onLightningStrike);
        MinecraftForge.EVENT_BUS.addListener(TiCEvent::tick);
        PsiAPI.registerSpellPieceAndTexture(new ResourceLocation(PsiEX.MOD_ID, "piecetrick_ticattack"), PieceTrick_TiCAttack.class);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> this::cevents);
    }
    public CastItemObject castItemObject(String name, Supplier<? extends Item> constructor) {
        ItemObject<Item> cast = new ItemObject<>(PsiEXRegistry.ITEMS.register(name + "_cast", constructor));
        ItemObject<Item> sandCast = new ItemObject<>(PsiEXRegistry.ITEMS.register(name + "_sand_cast", constructor));
        ItemObject<Item> redSandCast = new ItemObject<>(PsiEXRegistry.ITEMS.register(name + "_red_sand_cast", constructor));
        return new CastItemObject(new ResourceLocation(PsiEX.MOD_ID,name), cast, sandCast, redSandCast);
    }
    @OnlyIn(Dist.CLIENT)
    private void cevents(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemColors itemColors = Minecraft.getInstance().getItemColors();
            itemColors.register(new TiCCADColorProvider(), TICCAD.get());
        });
    }
}
