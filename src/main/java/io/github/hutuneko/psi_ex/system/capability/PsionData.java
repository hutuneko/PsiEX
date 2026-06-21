package io.github.hutuneko.psi_ex.system.capability;

import io.github.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class PsionData implements IPsionData {
    private double psion = 100;

    @Override public double getPsion(){ return psion; }
    @Override public boolean isPsion(){ return psion <= 0.0; }
    @Override public void setPsion(double v){ psion = v; }
    @Override public void add(double v){ psion = psion + v; }
    @Override public void hurt(double v){ psion = psion - v; }

    @Override public void tickRegain(LivingEntity p){
        if (p.isDeadOrDying())return;
        if (p.level().isClientSide)return;
        if (psion <= 0){
            p.setHealth(0);
            if (p.isDeadOrDying()){
                p.die(p.level().damageSources().magic());
            }
        }
        if (p.level().getGameTime() % 10 == 0) {
            var attr = p.getAttribute(PsiEXAttributes.PSI_PSION_POINT.get());
            double max = attr != null ? attr.getValue() : -1;
            if (psion < max) psion = Math.min(max, psion + 1.0);
        }
    }
    @Override public void save(CompoundTag tag){ tag.putDouble("cur", psion); }
    @Override public void load(CompoundTag tag){ psion = tag.getDouble("cur"); }
}