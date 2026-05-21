package io.github.hutuneko.psi_ex.item;

import io.github.hutuneko.psi_ex.system.MySpellAcceptor;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.entity.ManaBurstEntity;
import vazkii.botania.common.item.lens.Lens;
import vazkii.botania.common.item.lens.LensItem;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.item.ItemSpellDrive;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemPsiManaLens extends LensItem {
    public static final Lens MY_PSI_LENS_BEHAVIOR = new Lens() {
        @Override
        public boolean collideBurst(ManaBurst burst, HitResult pos,
                                    boolean isManaBlock, boolean shouldKill, ItemStack lens) {
            Level world = ((Entity) burst).level();
            if (pos.getType() != HitResult.Type.BLOCK || world.isClientSide()) {
                return shouldKill;
            }
            CompoundTag spellTag = lens.getTagElement("spell");
            if (spellTag != null && spellTag.getBoolean("validSpell")) {
                lens.getOrCreateTagElement("spell").putBoolean("validSpell", true);
            }
            ServerLevel serverWorld = (ServerLevel)((Entity) burst).level();

            BlockHitResult hit = (BlockHitResult) pos;
            Vector3 hitVec = new Vector3(
                    hit.getLocation().x,
                    hit.getLocation().y,
                    hit.getLocation().z
            );

            Spell spell = ItemSpellDrive.getSpell(lens);

            if (spell == null || !ISpellAcceptor.hasSpell(lens)) {
                return shouldKill;
            }
            Player caster;
            Entity raw = burst.entity();
            if (raw instanceof ManaBurstEntity mbe && mbe.getOwner() instanceof Player p) {
                caster = p;
            } else {
                caster = FakePlayerFactory.getMinecraft(serverWorld);
                double ox = hitVec.x, oy = hitVec.y, oz = hitVec.z;
                caster.setPos(ox, oy, oz);
            }

            SpellContext ctx = new SpellContext()
                    .setPlayer(caster)
                    .setSpell(spell);
            ctx.cspell.safeExecute(ctx);
            return shouldKill;
        }

    };
    public ItemPsiManaLens(Properties props) {
        super(props,
                MY_PSI_LENS_BEHAVIOR,
                0x4F9BF9
        );
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

