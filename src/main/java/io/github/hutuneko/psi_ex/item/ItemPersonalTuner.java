package io.github.hutuneko.psi_ex.item;

import io.github.hutuneko.psi_ex.system.attribute.AttrRequest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.PsiAPI;

public class ItemPersonalTuner extends Item {

    public ItemPersonalTuner(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
            if (world.isClientSide) {
                for (ResourceLocation id : PsiAPI.getAllPieceKeys()) System.out.println(id);
                AttrRequest.requestOpen();
            }
            return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }
}
