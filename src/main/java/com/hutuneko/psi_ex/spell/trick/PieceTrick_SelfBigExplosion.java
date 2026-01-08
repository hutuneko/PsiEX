package com.hutuneko.psi_ex.spell.trick;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceTrick;

import java.util.List;

public class PieceTrick_SelfBigExplosion extends PieceTrick {
    public PieceTrick_SelfBigExplosion(Spell spell) {
        super(spell);
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        Player player = context.caster;
        player.setHealth(0);
        Level level = player.level();
        BlockPos center = player.getOnPos();
        if (!(level instanceof ServerLevel serverLevel)) return null;

        int radius = 8;
        int r2 = radius * radius;
        AABB area = new AABB(center).inflate(radius);
        serverLevel.playSound(null, center, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 10.0F, 0.5F);
        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.getX(), center.getY(), center.getZ(), 100, 5.0, 5.0, 5.0, 0.1);
        List<LivingEntity> livingEntities = level.getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity living : livingEntities){
            living.setHealth(0);
        }
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    if (x * x + y * y + z * z <= r2) {
                        BlockPos targetPos = center.offset(x, y, z);

                        if (!serverLevel.isEmptyBlock(targetPos)) {
                            serverLevel.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 2 | 16);
                        }
                    }
                }

            }
        }
        return null;
    }
}