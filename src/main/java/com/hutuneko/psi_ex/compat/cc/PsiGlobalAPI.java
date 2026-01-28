// PsiPocketPeripheral.java
package com.hutuneko.psi_ex.compat.cc;

import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import com.hutuneko.psi_ex.api.CopyPlayerInventory;
import com.hutuneko.psi_ex.system.CuriosUtil;
import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotResult;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;

import java.util.Optional;
import java.util.UUID;

public class PsiGlobalAPI implements ILuaAPI {
    private final IComputerAccess computer;

    public PsiGlobalAPI(IComputerAccess computer) {
        this.computer = computer;
    }

    /** グローバルに生えるテーブル名（http や fs と同様に psi_ex.* で呼べる） */
    @Override public String[] getNames() { return new String[]{"psi_ex"}; }

    @LuaFunction(mainThread = true,value = "castCurioSpellAt")
    public final Object[] castCurioSpellAt(String playerId, int socketIndex) {
        return doCast(playerId, socketIndex, null, null, null);
    }

    // 座標あり（別名にする）
    @LuaFunction(mainThread = true,value = "castCurioSpellAtPos")
    public final Object[] castCurioSpellAtPos(String playerId, int socketIndex,
                                              double x, double y, double z) {
        return doCast(playerId, socketIndex, x, y, z);
    }

    public Object[] doCast(String playerId, int socketIndex, Double x, Double y, Double z) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerLevel level = server.overworld();
        ServerPlayer player = resolvePlayer(server, playerId);
        if (player == null) return new Object[]{ false, "player not found: " + playerId };
        Optional<SlotResult> found = CuriosUtil.findFirstByItem(player, PsiEXRegistry.PSI_CURIO_BULLET.get());
        if (found.isEmpty()) return new Object[]{ false, "psi_curio_bullet not equipped" };

        ItemStack curio = found.get().stack();
        final boolean hasAll = x != null && y != null && z != null;
        ISocketable sock = ISocketable.socketable(curio);
        if (sock == null) return new Object[]{ false, "no ISocketable capability on curio" };

        int slots = sock.getLastSlot();
        if (socketIndex < 0 || socketIndex > slots) {
            return new Object[]{ false, "socket index out of range: " + socketIndex + " / " + slots };
        }

        // 指定ソケットの弾取得
        ItemStack bullet = sock.getBulletInSocket(socketIndex);
        if (bullet.isEmpty()) return new Object[]{ false, "empty bullet in socket " + socketIndex };

        // 術式受け口
        ISpellAcceptor acc = ISpellAcceptor.acceptor(bullet);
        if (acc == null) return new Object[]{ false, "bullet has no ISpellAcceptor" };
        if (!ISpellAcceptor.hasSpell(bullet)) return new Object[]{ false, "no spell stored in bullet" };

        try {
            Spell s = acc.getSpell();
            SpellContext sc;
            if (hasAll) {
                GameProfile prof = new GameProfile(UUID.randomUUID(), "psi_fake");
                ServerPlayer fake = FakePlayerFactory.get(level,prof);
                fake.setPos(x, y, z);
                fake.setYRot(player.getYRot());
                fake.setXRot(player.getXRot());
                fake.setYHeadRot(player.getYHeadRot());
                CopyPlayerInventory.copyFeke(player, fake);
                sc = new SpellContext().setSpell(s).setPlayer(fake);
            }else {
                sc = new SpellContext().setSpell(s).setPlayer(player);
            }
            sc.cspell.safeExecute(sc);
            return new Object[]{ true, "ok" };
        } catch (Throwable t) {
            return new Object[]{ false, "cast failed: " + t.getMessage() };
        }
    }

    private static @Nullable ServerPlayer resolvePlayer(MinecraftServer server, String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return server.getPlayerList().getPlayer(uuid);
        } catch (IllegalArgumentException ignored) {
            // UUID でなければ名前で検索
            return server.getPlayerList().getPlayerByName(id);
        }
    }
}
