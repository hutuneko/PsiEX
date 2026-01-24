package com.hutuneko.psi_ex.spell.trick;

import com.hutuneko.psi_ex.api.CopyPlayerInventory;
import com.hutuneko.psi_ex.api.spellparam.ParamCompoundTag;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import com.mojang.authlib.GameProfile;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.internal.MathHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

import java.util.UUID;

public class PieceTrick_CastScroll extends PieceTrick {
    private ParamVector dirParam;
    private ParamCompoundTag dataParam;

    public PieceTrick_CastScroll(vazkii.psi.api.spell.Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(dirParam = new ParamVector(SpellParam.GENERIC_NAME_VECTOR,SpellParam.RED,false,false
        ));
        addParam(dataParam = new ParamCompoundTag(
                "scrollData"
        ));
    }

    @Override
    public void addToMetadata(vazkii.psi.api.spell.SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        meta.addStat(EnumSpellStat.POTENCY, 20);
        meta.addStat(EnumSpellStat.COST,   50);
    }

    @Override
    public EnumPieceType getPieceType() {
        return EnumPieceType.TRICK;
    }

    @Override
    public Class<?> getEvaluationType() {
        return Void.class;
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        Player player = context.caster;
        Level world = player.level();
        if (world.isClientSide) return null;
        ServerLevel sWorld = (ServerLevel) world;

        Vector3 vv = getParamValue(context, dirParam);
        double ox = vv.x, oy = vv.y, oz = vv.z;

        GameProfile prof = new GameProfile(UUID.randomUUID(), "psi_fake");
        ServerPlayer fake = FakePlayerFactory.get(sWorld, prof);
        fake.setPos(ox, oy, oz);
        fake.setYRot(player.getYRot());
        fake.setXRot(player.getXRot());
        fake.setYHeadRot(player.getYHeadRot());
        CopyPlayerInventory.copyFeke((ServerPlayer) player, fake);
        Object removalListener = getevent(sWorld, fake);

        Item scrollItem = PsiEXRegistry.CAST_SCROLL.get();
        ItemStack scrollStack = new ItemStack(scrollItem);
        CompoundTag scrollTag = (CompoundTag) getParamValue(context, dataParam);
        scrollStack.setTag(scrollTag);

        if (scrollStack.isEmpty()) {
            throw new SpellRuntimeException("NBTから復元したスクロールが無効です");
        }


        ISpellContainer container = ISpellContainer.get(scrollStack);

        ItemStack scrollCopy = scrollStack.copy();
        Vector3 casterPos = Vector3.fromEntity(context.caster);
        Vector3 diff = vv.copy().subtract(casterPos);
        AttributeInstance instance = player.getAttribute(PsiEXAttributes.PSI_SPELL_RANGE.get());
        boolean isR = false;
        if (instance != null) {
            isR = MathHelper.pointDistanceSpace(vv.x, vv.y, vv.z, casterPos.x, casterPos.y, casterPos.z) <= instance.getValue();
        }
        if (isR) {
            throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
        }
        System.out.println(container);
        SpellData data = ISpellContainer.getOrCreate(scrollStack).getSpellAtIndex(0);
        AbstractSpell spell = data.getSpell();
        if (spell == null) throw new SpellRuntimeException("spellがありません");
        sWorld.addFreshEntity(fake);
        spell.attemptInitiateCast(
                scrollCopy,
                spell.getLevelFor(data.getLevel(), player),
                sWorld,
                fake,
                CastSource.SCROLL,
                false,
                SpellSelectionManager.MAINHAND
        );

        MinecraftForge.EVENT_BUS.register(removalListener);
        return null;
    }

    private @NotNull Object getevent(ServerLevel sWorld, ServerPlayer fake) {
        long currentTick = sWorld.getGameTime();
        long removeTick = currentTick + 20 * 10;

        return new Object() {
            @SubscribeEvent(priority = EventPriority.NORMAL)
            public void onServerTick(TickEvent.ServerTickEvent evt) {
                if (evt.phase == TickEvent.Phase.END && sWorld.getGameTime() >= removeTick) {
                    if (fake.isAlive()) {
                        fake.remove(Entity.RemovalReason.DISCARDED);
                    }
                    MinecraftForge.EVENT_BUS.unregister(this);
                }
            }
        };
    }
}
