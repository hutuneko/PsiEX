// BarrierEntity.java
package io.github.hutuneko.psi_ex.entity;

import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;

public class PsiBarrierEntity extends Entity {
    private static final EntityDataAccessor<Integer> LIFETIME_TICKS =
            SynchedEntityData.defineId(PsiBarrierEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<CompoundTag> SPELL =
            SynchedEntityData.defineId(PsiBarrierEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final long COOLDOWN_TICKS = 20L;
    private final Int2LongOpenHashMap lastHit = new Int2LongOpenHashMap();

    public PsiBarrierEntity(EntityType<? extends PsiBarrierEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LIFETIME_TICKS, 20);
        this.entityData.define(SPELL, new CompoundTag());
    }

    public void init(int lifetimeTicks, float width, CompoundTag spell) {
        if (!level().isClientSide) {
            this.entityData.set(LIFETIME_TICKS, Math.max(1, lifetimeTicks));
            this.entityData.set(SPELL,spell);
        }
        this.setBoundingBox(getBoundingBox().inflate(0));
        this.setPos(getX(), getY(), getZ());
        this.setBoundingBox(this.getBoundingBox().inflate(width / 2f, width / 2f, width / 2f));
        this.setInvulnerable(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        int t = this.entityData.get(LIFETIME_TICKS) - 1;
        if (t <= 0) {
            discard();
        } else {
            this.entityData.set(LIFETIME_TICKS, t);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("life")) this.entityData.set(LIFETIME_TICKS, tag.getInt("life"));
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("life", this.entityData.get(LIFETIME_TICKS));
    }

    @Override
    public boolean isPickable() { return true; }
    @Override
    public void push(@NotNull Entity other) {
        super.push(other);

        if (!level().isClientSide) {
            if (!shouldAffect(other)) return;

            final int id = other.getId();
            final long now = level().getGameTime();
            final long prev = lastHit.getOrDefault(id, Long.MIN_VALUE);

            // クールダウン中ならスキップ
            if (now - prev < COOLDOWN_TICKS) return;

            // 記録更新
            lastHit.put(id, now);
            CompoundTag v = this.entityData.get(SPELL);
            System.out.println(v);
            Spell spell = new Spell();
            spell.readFromNBT(v);
            SpellContext sc = new SpellContext().setSpell(spell).setPlayer((Player) other).setFocalPoint(other);
            sc.cspell.safeExecute(sc);
        }
    }

    @Override
    public boolean isPushable() {
        return true; // ← 他エンティティから「押される」ようにする（衝突検出のため）
    }
    private boolean shouldAffect(Entity e) {
        if (e == this) return false;
        return e.isAlive();
    }
    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
