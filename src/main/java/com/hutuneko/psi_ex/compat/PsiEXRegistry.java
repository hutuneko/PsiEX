package com.hutuneko.psi_ex.compat;

import com.hutuneko.psi_ex.PsiEX;
import com.hutuneko.psi_ex.block.MultiPageTileProgrammer;
import com.hutuneko.psi_ex.cliant.menu.GPTCADSettingMenu;
import com.hutuneko.psi_ex.entity.*;
import com.hutuneko.psi_ex.recipe.NbtAddRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PsiEXRegistry {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PsiEX.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PsiEX.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PsiEX.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, PsiEX.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, PsiEX.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PsiEX.MOD_ID);
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, PsiEX.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB,PsiEX.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, PsiEX.MOD_ID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "psi_ex");

    public static RegistryObject<Item> PIECE_PROGRAM = null;
    public static RegistryObject<Item> PERSONAL_TUNER = null;
    public static RegistryObject<Item> PSI_ARROW = null;
    public static RegistryObject<Item> PSI_NEEDLE_DART = null;
    public static RegistryObject<Item> CAD_PATCH = null;

    public static RegistryObject<Item> PSI_MANA_LENS = null;

    public static RegistryObject<Item> PSI_SPELLBOOK = null;

    public static RegistryObject<Item> GPTCAD = null;
    public static RegistryObject<Item> PSI_SPIRITS_EYE = null;
    public static RegistryObject<Item> RANGEZERO = null;
    public static RegistryObject<Item> ECLAIR = null;
    public static RegistryObject<Item> PHANTOM = null;
    public static RegistryObject<Item> ELFINSNIPER = null;
    public static RegistryObject<Item> SWORDMAJIAM = null;
    public static RegistryObject<Item> OROCHIMARU = null;
    public static RegistryObject<Item> PSI_CURIO_BULLET = null;

    public static RegistryObject<Item> PSI_BOW = null;
    public static RegistryObject<Item> STORAGE = null;
    public static RegistryObject<Item> CAST_SCROLL = null;

    public static RegistryObject<Item> GPTCAD_ASSEMBLY_IRON = null;
    public static RegistryObject<Item> GPTCAD_ASSEMBLY_GOLD = null;
    public static RegistryObject<Item> GPTCAD_ASSEMBLY_PSIMETAL = null;
    public static RegistryObject<Item> GPTCAD_ASSEMBLY_EBONY = null;
    public static RegistryObject<Item> GPTCAD_ASSEMBLY_IVORY = null;
    public static RegistryObject<Item> GPTCAD_ASSEMBLY_CREATIVE = null;

    public static RegistryObject<Item> PSIKILLER = null;

    public static RegistryObject<Block> MULTIPAGEPROGRAMMER = null;
    public static RegistryObject<Block> GPTCADSETTINGBLOCK = null;

    public static RegistryObject<EntityType<PsiArrowEntity>> PSI_ARROW_ENTITY = null;
    public static RegistryObject<EntityType<PsiNeedleDartEntity>> PSI_NEEDLE_DARTENTITY = null;
    public static RegistryObject<EntityType<PsiBarrierEntity>> PSI_BRRIER_ENTITY = null;
    public static RegistryObject<EntityType<PsiAirEntity>> PSI_COMPRESSIONAIR_ENTITY = null;
    public static RegistryObject<EntityType<Railgun>> RAILGUN = null;

    public static  RegistryObject<BlockEntityType<MultiPageTileProgrammer>> MULTI_PROGRAMMER = null;
    public static RegistryObject<BlockEntityType<?>> GPTCADSETTINGTILE = null;


    public static RegistryObject<MobEffect> CASTJAMMING = null;
    public static RegistryObject<MobEffect> ECLAIREFFECT = null;

    public static RegistryObject<MenuType<GPTCADSettingMenu>> GPTCAD_SETTING_MENU = null;

    public static final ResourceKey<DamageType> PSI_FAKE_DAMAGE = ResourceKey.create(
            Registries.DAMAGE_TYPE, new ResourceLocation(PsiEX.MOD_ID, "psi_fake_damage")
    );

    public static RegistryObject<RecipeSerializer<NbtAddRecipe>> NBT_ADDING_SERIALIZER = null;

    public static RegistryObject<CreativeModeTab> CREATIVE_TAB_ITEMS = PsiEXRegistry.TABS.register(PsiEX.MOD_ID, () ->
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
}
