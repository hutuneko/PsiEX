package io.github.hutuneko.psi_ex.spell.trick;

import io.github.hutuneko.psi_ex.api.spellparam.ParamCompoundTag;
import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.compat.iron.IronsEvent;
import io.github.hutuneko.psi_ex.system.attribute.PsiEXAttributes;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import vazkii.psi.api.internal.MathHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

import static io.github.hutuneko.psi_ex.compat.iron.IronsEvent.activeFakePlayers;

public class TrickCastScroll extends PieceTrick {
    private ParamVector dirParam;
    private ParamCompoundTag dataParam;
    private ParamEntity targetParam;

    public TrickCastScroll(vazkii.psi.api.spell.Spell spell) {
        super(spell);
    }

    @Override
    public void initParams() {
        addParam(dirParam = new ParamVector(SpellParam.GENERIC_NAME_VECTOR, SpellParam.RED, false, false));
        addParam(dataParam = new ParamCompoundTag("scrollData"));
        addParam(targetParam = new ParamEntity(SpellParam.GENERIC_NAME_TARGET, SpellParam.BLUE, true, false));
    }

    @Override
    public void addToMetadata(vazkii.psi.api.spell.SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        meta.addStat(EnumSpellStat.POTENCY, 20);
        meta.addStat(EnumSpellStat.COST, 50);
    }

    @Override
    public EnumPieceType getPieceType() {
        return EnumPieceType.TRICK;
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException {
        Player p = context.caster;
        Level world = p.level();
        if (world.isClientSide) return null;

        ServerLevel sWorld = (ServerLevel) world;
        ServerPlayer player = (ServerPlayer) p;
        
        Vector3 vv = getParamValue(context, dirParam);
        double ox = vv.x, oy = vv.y, oz = vv.z;
        
        Vec3 playerOriginalPos = player.position();
        float originalYaw = player.getYRot();
        float originalPitch = player.getXRot();
        
        Vector3 casterPos = Vector3.fromEntity(context.caster);
        AttributeInstance instance = player.getAttribute(PsiEXAttributes.PSI_SPELL_RANGE.get());
        if (instance != null) {
            double dist = MathHelper.pointDistanceSpace(vv.x, vv.y, vv.z, casterPos.x, casterPos.y, casterPos.z);
            if (dist >= instance.getValue()) {
                throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
            }
        }
        Item scrollItem = PsiEXRegistry.CAST_SCROLL.get();
        ItemStack scrollStack = new ItemStack(scrollItem);
        CompoundTag scrollTag = (CompoundTag) getParamValue(context, dataParam);
        scrollStack.setTag(scrollTag);
        if (scrollStack.isEmpty()) {
            throw new SpellRuntimeException("NBTから復元したスクロールが無効です");
        }

        SpellData data = ISpellContainer.getOrCreate(scrollStack.copy()).getSpellAtIndex(0);
        AbstractSpell spell = data.getSpell();
        if (spell == null) throw new SpellRuntimeException("psi_ex.spellerror.nospell");

        int spellLevel = spell.getLevelFor(data.getLevel(), player);
        
        ServerPlayer castExecutor;
        boolean isContinuous = spell.getCastType() == CastType.CONTINUOUS;

        if (isContinuous) {
            FakePlayer fakePlayer = FakePlayerFactory.getMinecraft(sWorld);

            fakePlayer.setPos(ox, oy, oz);

            fakePlayer.setYRot(originalYaw);
            fakePlayer.setXRot(originalPitch);
            fakePlayer.setYHeadRot(originalYaw);

            MagicData fakeMagicData = new MagicData(fakePlayer);

            castExecutor = fakePlayer;

            Entity entity = getParamValue(context, targetParam);
            if (entity instanceof LivingEntity target) {
                fakeMagicData.setAdditionalCastData(new TargetEntityCastData(target));
            }

            int duration = spell.getCastTime(spellLevel);
            if (duration <= 0) duration = 100;

            fakeMagicData.initiateCast(spell, spellLevel, duration, CastSource.SCROLL, spell.getSpellId());

            Vec3 returnPos = new Vec3(0, -1000, 0);
            IronsEvent.FakePlayerTracker.newFakePlayer(fakePlayer, duration, returnPos);
        } else {
            player.setPos(ox, oy, oz);
            castExecutor = player;

            Entity entity = getParamValue(context, targetParam);
            if (entity instanceof LivingEntity target) {
                MagicData magicData = MagicData.getPlayerMagicData(player);
                magicData.setAdditionalCastData(new TargetEntityCastData(target));
            }
        }

        try {
            spell.castSpell(sWorld, spellLevel, castExecutor, CastSource.SCROLL, false);
        } catch (Exception e) {
            if (isContinuous && castExecutor instanceof FakePlayer fp) {
                fp.setPos(0, -1000, 0); 
                activeFakePlayers.remove(fp.getUUID());
            } else {
                player.setPos(playerOriginalPos);
            }
            throw new SpellRuntimeException("魔法の発動に失敗: " + e.getMessage());
        }
        if (!isContinuous) {
            player.setPos(playerOriginalPos);
            MagicData.getPlayerMagicData(player).setAdditionalCastData(null);
        }

        return null;
    }
}