package com.hutuneko.psi_ex.recipe;

import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class NbtAddRecipe extends CustomRecipe {

    public NbtAddRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, @NotNull Level level) {
        boolean hasTagItem = false;
        boolean hasTargetItem = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() == PsiEXRegistry.CAD_PATCH.get()) {
                if (hasTagItem) return false;
                hasTagItem = true;
            } else {
                if (hasTargetItem) return false;
                hasTargetItem = true;
            }
        }

        return hasTagItem && hasTargetItem;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, @NotNull RegistryAccess access) {
        ItemStack targetStack = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.getItem() != PsiEXRegistry.CAD_PATCH.get()) {
                targetStack = stack;
                break;
            }
        }

        if (targetStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = targetStack.copy();

        CompoundTag tag = result.getOrCreateTag();

        tag.putBoolean("psiex_iscad", true);


        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return PsiEXRegistry.NBT_ADDING_SERIALIZER.get();
    }
}