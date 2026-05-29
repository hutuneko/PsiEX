package io.github.hutuneko.psi_ex.compat.cc;

import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import io.github.hutuneko.psi_ex.PsiEX;
import io.github.hutuneko.psi_ex.block.PsiCasterBlock;
import io.github.hutuneko.psi_ex.block.PsiCasterBlockEntity;
import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import moffy.addonapi.AddonModule;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CCCuriosModule implements AddonModule {
    public static final DeferredRegister<PocketUpgradeSerialiser<?>> POCKET_SERIALIZER =
            DeferredRegister.create(PocketUpgradeSerialiser.registryId(), PsiEX.MOD_ID);
    public static RegistryObject<BlockEntityType<PsiCasterBlockEntity>> PSI_CASTER_BE = null;
    public static RegistryObject<PocketUpgradeSerialiser<PsiCasterPocketUpgrade>> PSI_CASTER_POCKET = null;
    public CCCuriosModule(){
        PsiEXRegistry.PSI_CASTER_POCKET_UPGRADE = PsiEXRegistry.ITEMS.register("psi_caster_pocket",
                () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE))
        );

        PsiEXRegistry.PSI_CASTER_BLOCK = PsiEXRegistry.BLOCKS.register("psi_caster",
                () -> new PsiCasterBlock(BlockBehaviour.Properties.of()
                        .strength(2.0f)
                        .requiresCorrectToolForDrops()
                )
        );

        PSI_CASTER_BE = PsiEXRegistry.BLOCK_ENTITIES.register("psi_caster",
                () -> BlockEntityType.Builder.of(PsiCasterBlockEntity::new, PsiEXRegistry.PSI_CASTER_BLOCK.get())
                        .build(null)
        );

        PsiEXRegistry.ITEMS.register("psi_caster",
                () -> new BlockItem(PsiEXRegistry.PSI_CASTER_BLOCK.get(), new Item.Properties())
        );
        PSI_CASTER_POCKET = POCKET_SERIALIZER.register("psi_caster",
                () -> PocketUpgradeSerialiser.simple(PsiCasterPocketUpgrade::new)
        );
    }
}

