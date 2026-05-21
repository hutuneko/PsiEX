package io.github.hutuneko.psi_ex.spell.trick;

import io.github.hutuneko.psi_ex.api.CopyPlayerInventory;
import io.github.hutuneko.psi_ex.api.spellparam.ParamCompoundTag;
import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import com.mojang.authlib.GameProfile;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayerFactory;
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

        Item scrollItem = PsiEXRegistry.CAST_SCROLL.get();
        ItemStack scrollStack = new ItemStack(scrollItem);
        CompoundTag scrollTag = (CompoundTag) getParamValue(context, dataParam);
        scrollStack.setTag(scrollTag);
        fake.setItemInHand(InteractionHand.OFF_HAND, scrollStack);
        if (scrollStack.isEmpty()) {
            throw new SpellRuntimeException("NBTから復元したスクロールが無効です");
        }

        ISpellContainer container = ISpellContainer.get(scrollStack);

        ItemStack scrollCopy = scrollStack.copy();
        Vector3 casterPos = Vector3.fromEntity(context.caster);
        AttributeInstance instance = player.getAttribute(PsiEXAttributes.PSI_SPELL_RANGE.get());
        boolean isR = false;
        if (instance != null) {
            isR = MathHelper.pointDistanceSpace(vv.x, vv.y, vv.z, casterPos.x, casterPos.y, casterPos.z) >= instance.getValue();
        }
        if (isR) {
            throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
        }
        SpellData data = ISpellContainer.getOrCreate(scrollCopy).getSpellAtIndex(0);
        AbstractSpell spell = data.getSpell();
        if (spell == null) throw new SpellRuntimeException("psi_ex.spellerror.nospell");
        sWorld.addFreshEntity(fake);
        spell.castSpell(
                sWorld,
                spell.getLevelFor(data.getLevel(), player),
                fake,
                CastSource.SCROLL,
                false
        );

        return null;
    }
}
