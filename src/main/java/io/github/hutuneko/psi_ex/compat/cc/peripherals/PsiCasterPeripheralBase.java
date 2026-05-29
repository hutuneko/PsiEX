package io.github.hutuneko.psi_ex.compat.cc.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import io.github.hutuneko.psi_ex.api.PsiEXAPI;
import io.github.hutuneko.psi_ex.api.UUIDDataHandler;
import io.github.hutuneko.psi_ex.block.GPTCADSettingTile;
import io.github.hutuneko.psi_ex.item.GeneralPurposeTypeCAD;
import io.github.hutuneko.psi_ex.system.CuriosUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotResult;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.item.ItemCAD;

import java.util.Optional;
import java.util.UUID;

public abstract class PsiCasterPeripheralBase implements IPeripheral {

    protected final IPeripheralOwner owner;
    protected float potency = 1.0f;
    protected int maxSocketIndex = Integer.MAX_VALUE;
    protected boolean consumesDurability = false;

    protected PsiCasterPeripheralBase(IPeripheralOwner owner) {
        this.owner = owner;
    }

    @Override
    public String getType() {
        return "psi_caster";
    }

    @LuaFunction(mainThread = true)
    public final MethodResult cast(int socketIndex) {
        return doCast(socketIndex, null, null, null, null);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult castAt(int socketIndex, double x, double y, double z) {
        return doCast(socketIndex, x, y, z, null);
    }

    
    @LuaFunction(mainThread = true)
    public final MethodResult castAtDim(int socketIndex, double x, double y, double z, String dimensionId) {
        return doCast(socketIndex, x, y, z, dimensionId);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult castHere(int socketIndex) {
        BlockPos pos = owner.getPos();
        return doCast(socketIndex, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), null);
    }

    @LuaFunction
    public final double getPotency() {
        return potency;
    }

    @LuaFunction
    public final int getMaxSocket() {
        return maxSocketIndex;
    }

    @LuaFunction
    public final boolean isHandheld() {
        return owner.isHandheld();
    }

    

    protected MethodResult doCast(int socketIndex, Double x, Double y, Double z, String dimensionId) {
        if (owner.isHandheld() && socketIndex > maxSocketIndex) {
            return MethodResult.of(false,
                    "socket " + socketIndex + " unavailable in handheld mode (max: " + maxSocketIndex + ")");
        }

        Player holder = owner.getPlayer();
        if (!(holder instanceof ServerPlayer player)) {
            return MethodResult.of(false, "server-side only");
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return MethodResult.of(false, "server not available");
        }

        
        ServerLevel targetLevel;
        if (dimensionId != null && !dimensionId.isEmpty()) {
            
            ResourceKey<Level> targetDim = parseDimensionId(dimensionId);
            if (targetDim == null) {
                return MethodResult.of(false, "invalid dimension: " + dimensionId);
            }
            targetLevel = server.getLevel(targetDim);
            if (targetLevel == null) {
                return MethodResult.of(false, "dimension not found: " + dimensionId);
            }
        } else {
            
            targetLevel = player.serverLevel();
        }

        Optional<SlotResult> found = CuriosUtil.findFirst(
                player, itemStack -> itemStack.getItem() instanceof GeneralPurposeTypeCAD
        );
        if (found.isEmpty()) {
            return MethodResult.of(false, "psi_curio_bullet not equipped");
        }
        ItemStack cad = found.get().stack();
        CompoundTag tag = cad.getOrCreateTag();
        if (!tag.contains("psi_ex:gpt_id")) return MethodResult.of(false, "GPTCAD not linked to tile");
        UUID tileId = tag.getUUID("psi_ex:gpt_id");
        CompoundTag blockNbt = UUIDDataHandler.loadCompoundTag(tileId, GPTCADSettingTile.DATA_ID);
        CompoundTag itemTag = PsiEXAPI.getItemBySlot(blockNbt, socketIndex);
        if (itemTag.isEmpty()) return MethodResult.of(false, "empty slot " + socketIndex);
        ItemStack bullet = ItemStack.of(itemTag);

        if (bullet.isEmpty()) {
            return MethodResult.of(false, "empty bullet in socket " + socketIndex);
        }

        ISpellAcceptor acc = ISpellAcceptor.acceptor(bullet);
        if (acc == null) {
            return MethodResult.of(false, "bullet has no ISpellAcceptor");
        }
        if (!ISpellAcceptor.hasSpell(bullet)) {
            return MethodResult.of(false, "no spell stored in bullet");
        }

        try {
            Spell spell = acc.getSpell();
            final boolean hasPos = x != null && y != null && z != null;

            PlayerDataHandler.PlayerData data = PlayerDataHandler.get(player);
            if (hasPos) {
                Vec3 pos = player.position();
                player.setPos(x, y, z);
                ItemCAD.cast(
                        targetLevel,
                        player,
                        data,
                        bullet,
                        cad,
                        5, 10, 0.05F,
                        spellContext -> spellContext.setSpell(spell).setPlayer(player)
                );
                player.setPos(pos);
            } else {
                ItemCAD.cast(
                        targetLevel,
                        player,
                        data,
                        bullet,
                        cad,
                        5, 10, 0.05F,
                        spellContext -> spellContext.setSpell(spell).setPlayer(player)
                );
            }

            if (consumesDurability && owner.isHandheld()) {
                owner.applyCost();
            }

            String dimName = targetLevel.dimension().location().toString();
            return MethodResult.of(true, "ok (potency: " + potency + ", dim: " + dimName + ")");

        } catch (Throwable t) {
            return MethodResult.of(false, "cast failed: " + t.getMessage());
        }
    }
    
    @Nullable
    private ResourceKey<Level> parseDimensionId(String dimensionId) {
        try {
            if (dimensionId.contains(":")) {
                String[] parts = dimensionId.split(":", 2);
                return ResourceKey.create(
                        net.minecraft.core.registries.Registries.DIMENSION,
                        new net.minecraft.resources.ResourceLocation(parts[0], parts[1])
                );
            } else {
                return ResourceKey.create(
                        net.minecraft.core.registries.Registries.DIMENSION,
                        new net.minecraft.resources.ResourceLocation("minecraft", dimensionId)
                );
            }
        } catch (Exception e) {
            return null;
        }
    }

    public IPeripheralOwner getOwner() {
        return owner;
    }
}