package io.github.hutuneko.psi_ex.spell.operator;

import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.system.CuriosUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotResult;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceOperator;

import java.util.Objects;
import java.util.Optional;

public class PieceOperator_getSpell extends PieceOperator {
    private SpellParam<Number> slotParam;

    public PieceOperator_getSpell(Spell spell) { super(spell); }

    @Override
    public void initParams() {
        slotParam = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER, SpellParam.GREEN, false, false);
        addParam(slotParam);
    }

    @Override
    public Class<?> getEvaluationType() { return CompoundTag.class; }

    public Object execute(SpellContext ctx) throws SpellRuntimeException {
        Player p = ctx.caster;
        if (p == null) throw new SpellRuntimeException("No caster");

        Optional<SlotResult> res = CuriosUtil.findFirstByItem(p, PsiEXRegistry.PSI_CURIO_BULLET.get());
        if (res.isEmpty()) throw new SpellRuntimeException("キュリオスから取得できません");

        ItemStack charm = res.get().stack();
        if (charm.isEmpty()) throw new SpellRuntimeException("キュリオスから取得できません");

        ISocketable sock = ISocketable.socketable(charm);
        if (sock == null ) throw new SpellRuntimeException("ソケットが見つかりません");
        Number n = this.getParamValue(ctx, slotParam);
        int idx = (n != null) ? Math.max(0, n.intValue()) : sock.getSelectedSlot();
        if (idx < 0 || idx > sock.getLastSlot()) throw new SpellRuntimeException("スロット番号が不正です");

        ItemStack bullet = sock.getBulletInSocket(idx);
        ISpellAcceptor acc = ISpellAcceptor.acceptor(bullet);
        if (acc == null || !ISpellAcceptor.hasSpell(bullet)) throw new SpellRuntimeException("弾に術式が入っていません");

        Spell raw = acc.getSpell();
        CompoundTag out = new CompoundTag();
        Objects.requireNonNull(raw).writeToNBT(out);
        return out;
    }
}
