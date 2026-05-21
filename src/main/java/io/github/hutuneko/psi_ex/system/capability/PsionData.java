package io.github.hutuneko.psi_ex.system.capability;

import io.github.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class PsionData implements IPsionData {
    private double psion = 100;

    @Override public double getPsion(){ return psion; }
    @Override public boolean isPsion(){ return psion <= 0.0; }
    @Override public void setPsion(double v){ psion = v; }
    @Override public void add(double v){ psion = psion + v; }
    @Override public void hurt(double v){ psion = psion - v; }

    @Override public void tickRegain(Player p){
        if (p.level().getGameTime() % 10 == 0) {
            double max = Objects.requireNonNull(p.getAttribute(PsiEXAttributes.PSI_PSION_POINT.get())).getValue();
            if (psion < max) psion = Math.min(max, psion + 1.0);
        }
    }
    @Override public void save(CompoundTag tag){ tag.putDouble("cur", psion); }
    @Override public void load(CompoundTag tag){ psion = tag.getDouble("cur"); }
}