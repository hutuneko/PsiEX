package io.github.hutuneko.psi_ex.item;

import io.github.hutuneko.psi_ex.entity.PsiArrowEntity;
import io.github.hutuneko.psi_ex.system.MySpellAcceptor;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.common.item.ItemSpellDrive;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PsiArrowItem extends ArrowItem {
    public PsiArrowItem(Properties p) { super(p); }

    @Override
    public @NotNull AbstractArrow createArrow(@NotNull Level level, @NotNull ItemStack ammo, @NotNull LivingEntity shooter) {
        ItemSpellDrive.getSpell(ammo);
        PsiArrowEntity arrow = new PsiArrowEntity(level, shooter, ammo);
        arrow.setStack(ammo);
        return arrow;
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
