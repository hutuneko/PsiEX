package io.github.hutuneko.psi_ex.item;

import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.entity.PsiNeedleDartEntity;
import io.github.hutuneko.psi_ex.system.MySpellAcceptor;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.ISpellAcceptor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemNeedleDart extends Item {
    public ItemNeedleDart(Properties properties) {
        super(properties);
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Level level = player.level();
        if (!level.isClientSide) {
            PsiNeedleDartEntity needle = new PsiNeedleDartEntity(PsiEXRegistry.PSI_NEEDLE_DARTENTITY.get(), level, player);
            needle.setItem(stack);
            needle.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 0F);
            level.addFreshEntity(needle);
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1); // 消費
        }

        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag unusedNbt) {
        LazyOptional<ISpellAcceptor> cap = LazyOptional.of(() -> new MySpellAcceptor(stack));
        return new ICapabilityProvider() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> requested, @Nullable Direction side) {
                return PsiAPI.SPELL_ACCEPTOR_CAPABILITY.orEmpty(requested, cap);
            }
        };
    }
}
