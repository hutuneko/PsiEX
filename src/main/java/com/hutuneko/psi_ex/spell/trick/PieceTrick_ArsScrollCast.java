package com.hutuneko.psi_ex.spell.trick;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.api.CopyPlayerInventory;
import com.mojang.authlib.GameProfile;
import com.hutuneko.psi_ex.api.spellparam.ParamCompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.FakePlayerFactory;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.api.internal.Vector3;

import java.util.Objects;
import java.util.UUID;

public class PieceTrick_ArsScrollCast extends PieceTrick {
    static {
        ResourceKey.createRegistryKey(new ResourceLocation("ars_nouveau", "spell"));
    }

    private final ParamVector originParam;
    private final ParamCompoundTag parchmentParam;

    public PieceTrick_ArsScrollCast(vazkii.psi.api.spell.Spell psiSpell) {
        super(psiSpell);
        originParam = new ParamVector(SpellParam.GENERIC_NAME_VECTOR, SpellParam.BLUE, false, false);
        parchmentParam = new ParamCompoundTag("parchmentData");
        addParam(originParam);
        addParam(parchmentParam);
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
    public Object execute(SpellContext psiCtx) throws SpellRuntimeException {
        Player player = psiCtx.caster;
        if (player == null || player.level().isClientSide) {
            return null;
        }
        ServerLevel world = (ServerLevel) player.level();

        CompoundTag tag = (CompoundTag) getParamValue(psiCtx, parchmentParam);
        ItemStack parchmentStack = new ItemStack(PsiEXRegistry.CAST_SCROLL.get());
        parchmentStack.setTag(tag);
        if (parchmentStack.isEmpty()) {
            throw new SpellRuntimeException("NBTから復元した羊皮紙が無効です");
        }

        var caster = CasterUtil.getCaster(parchmentStack);
        if (caster == null) {
            throw new SpellRuntimeException("有効な Ars Nouveau の羊皮紙を渡してください");
        }
        Spell arsSpell = caster.getSpell();
        if (arsSpell.isEmpty()) {
            throw new SpellRuntimeException("羊皮紙にスペルが書かれていません");
        }

        Vector3 v = getParamValue(psiCtx, originParam);
        ServerPlayer fake = FakePlayerFactory.get(world, new GameProfile(UUID.randomUUID(), "psi_ars_fake"));
        fake.setPos(v.x, v.y, v.z);
        fake.setYRot(player.getYRot());
        fake.setXRot(player.getXRot());
        CopyPlayerInventory.copyFeke((ServerPlayer) player,fake);

        com.hollingsworth.arsnouveau.api.spell.SpellContext arsCtx =
                com.hollingsworth.arsnouveau.api.spell.SpellContext.fromEntity(
                        arsSpell, fake, parchmentStack
                );
        SpellResolver resolver = new SpellResolver(arsCtx);

        SpellStats stats = new SpellStats.Builder()
                .addItemsFromEntity(fake)
                .build();
        AbstractCastMethod castMethod = arsSpell.getCastMethod();
        CastResolveType result = Objects.requireNonNull(castMethod).onCast(
                parchmentStack,
                fake,
                world,
                stats,
                arsCtx,
                resolver
        );
        if (result != CastResolveType.SUCCESS) {
            throw new SpellRuntimeException("スペルの発動に失敗しました");
        }
        return null;
    }
}



