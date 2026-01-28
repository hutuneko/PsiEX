package com.hutuneko.psi_ex.mixin;

import com.hutuneko.psi_ex.api.CadBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.*;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.common.core.handler.capability.CADData;
import vazkii.psi.common.item.ItemCAD;

@Mixin(Item.class)
@Implements(@Interface(iface = ICAD.class, prefix = "icad$"))
public abstract class ItemMixin {

    @Unique
    private static final CadBehavior psi_ex_1_20_1$cad = new CadBehavior();
    public ItemStack icad$getComponentInSlot(ItemStack stack, EnumCADComponent type) {
        if (!CadBehavior.isCAD(stack)) return ItemStack.EMPTY;
        return psi_ex_1_20_1$cad.getCad().getComponentInSlot(stack, type);
    }

    public int icad$getStatValue(ItemStack stack, EnumCADStat stat) {
        if (!CadBehavior.isCAD(stack)) return -1;
        return psi_ex_1_20_1$cad.getStatValue(stack, stat);
    }

    public int icad$getTime(ItemStack stack) {
        if (!CadBehavior.isCAD(stack)) return 0;
        return psi_ex_1_20_1$cad.getCad().getTime(stack);
    }

    public void icad$incrementTime(ItemStack stack) {
        if (!CadBehavior.isCAD(stack)) return;
        psi_ex_1_20_1$cad.getCad().incrementTime(stack);
    }

    public int icad$getStoredPsi(ItemStack stack) {
        if (!CadBehavior.isCAD(stack)) return 0;
        return psi_ex_1_20_1$cad.getCad().getStoredPsi(stack);
    }

    public void icad$regenPsi(ItemStack stack, int psi) {
        if (!CadBehavior.isCAD(stack)) return;
        psi_ex_1_20_1$cad.getCad().regenPsi(stack, psi);
    }

    public int icad$consumePsi(ItemStack stack, int psi) {
        if (!CadBehavior.isCAD(stack)) return 0;
        return psi_ex_1_20_1$cad.getCad().consumePsi(stack, psi);
    }

    public int icad$getMemorySize(ItemStack stack) {
        if (!CadBehavior.isCAD(stack)) return 0;
        return psi_ex_1_20_1$cad.getCad().getMemorySize(stack);
    }

    public void icad$setStoredVector(ItemStack stack, int memorySlot, Vector3 vec) throws Exception {
        if (!CadBehavior.isCAD(stack)) return;
        psi_ex_1_20_1$cad.getCad().setStoredVector(stack, memorySlot, vec);
    }

    public Vector3 icad$getStoredVector(ItemStack stack, int memorySlot) throws Exception {
        if (!CadBehavior.isCAD(stack)) return Vector3.zero;
        return psi_ex_1_20_1$cad.getCad().getStoredVector(stack, memorySlot);
    }
    public int icad$getSpellColor(ItemStack stack) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            if (CadBehavior.isCAD(stack)) {
                return psi_ex_1_20_1$cad.getCad().getSpellColor(stack);
            }
        }
        return 1295871;
    }
    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void onInventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected, CallbackInfo ci) {
        if (((Item)(Object)this) instanceof ItemCAD) {
            CompoundTag nbt = stack.getOrCreateTag();
            if (!nbt.contains("psiex_iscad")) {
                nbt.putBoolean("psiex_iscad", true);
            }
        }
        if (CadBehavior.isCAD(stack)) {
            psi_ex_1_20_1$cad.getCad().inventoryTick(stack, world, entity, itemSlot, isSelected);
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void psi_ex$use(Level pLevel, Player pPlayer, InteractionHand pUsedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (CadBehavior.isCAD(stack)) {
            InteractionResultHolder<ItemStack> result = psi_ex_1_20_1$cad.use(pLevel, pPlayer, pUsedHand);
            if (result != null) {
                cir.setReturnValue(result);
            }
        }
    }
}