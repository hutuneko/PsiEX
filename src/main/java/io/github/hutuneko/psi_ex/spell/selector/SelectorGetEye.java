package io.github.hutuneko.psi_ex.spell.selector;

import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.system.CuriosUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotResult;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceSelector;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SelectorGetEye extends PieceSelector {


    public SelectorGetEye(Spell spell) {
        super(spell);
    }

    @Override
    public Class<?> getEvaluationType() {
        return Entity.class;
    }

    @Override
    public Entity execute(SpellContext context) throws SpellRuntimeException {
        Player p = context.caster;
        if (p == null) throw new SpellRuntimeException("No caster");
        Optional<SlotResult> res = CuriosUtil.findFirstByItem(p, PsiEXRegistry.PSI_SPIRITS_EYE.get());
        if (res.isEmpty()) throw new SpellRuntimeException("キュリオスから取得できません");
        ItemStack stack = res.get().stack();
        if (stack.isEmpty()) throw new SpellRuntimeException("キュリオスから取得できません");
        UUID uuid = Objects.requireNonNull(stack.getTag()).getUUID("psi_ex:eyeuuid");
        ServerLevel level = (ServerLevel) p.level();
        return level.getEntity(uuid);
    }
}



