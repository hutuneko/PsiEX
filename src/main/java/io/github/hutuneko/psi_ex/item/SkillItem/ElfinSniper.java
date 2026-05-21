package io.github.hutuneko.psi_ex.item.SkillItem;

import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.net.SniperTargetPacket;
import io.github.hutuneko.psi_ex.net.Net;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.SlotContext;

import java.util.*;

public class ElfinSniper extends SkillItem{
    public ElfinSniper(Properties pProperties) {
        super(pProperties, UUID.fromString("2dd214f1-a3fc-5057-9299-88ba606d9475"));
    }
    private final Map<UUID, Integer> tickCounters = new WeakHashMap<>();
    private static final int CHECK_INTERVAL = 4;
    private static final double RADIUS = 32.0;

    private final Map<UUID,List<Integer>> lastTargets = new WeakHashMap<>();

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity wearer = slotContext.entity();
        if (!(wearer.level() instanceof ServerLevel level)) return;
        if (!(wearer instanceof ServerPlayer player)) return;

        UUID wearerId = wearer.getUUID();

        int tick = tickCounters.getOrDefault(wearerId, 0) + 1;
        tickCounters.put(wearerId, tick);
        if (tick % CHECK_INTERVAL != 0) return;

        Vec3 center = wearer.position();
        AABB area = new AABB(
                center.x - RADIUS, center.y - RADIUS, center.z - RADIUS,
                center.x + RADIUS, center.y + RADIUS, center.z + RADIUS
        );

        List<LivingEntity> nearby = level.getEntitiesOfClass(
                LivingEntity.class, area,
                e -> e != wearer && e.distanceToSqr(center) <= RADIUS * RADIUS
        );

        // エンティティIDリストを作成
        List<Integer> targetIds = nearby.stream()
                .map(Entity::getId)
                .toList();

        if (!targetIds.equals(lastTargets.get(wearer.getUUID()))) {
            Net.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new SniperTargetPacket(targetIds)
            );
            lastTargets.put(wearer.getUUID(),targetIds);
        }
    }

    @Override
    protected void initializeAttributes() {
        for (RegistryObject<Attribute> attr : PsiEXRegistry.ATTRIBUTES.getEntries()) {
            Attribute targetAttr = attr.get();
            setAttributeAddition(targetAttr,100);
        }
    }
}
