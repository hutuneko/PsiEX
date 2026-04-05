package com.hutuneko.psi_ex.item;

import com.hutuneko.psi_ex.api.PsiEXAPI;
import com.hutuneko.psi_ex.api.UUIDDataHandler;
import com.hutuneko.psi_ex.block.GPTCADSettingTile;
import com.hutuneko.psi_ex.compat.PsiEXRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.CADStatEvent;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.common.core.handler.ContributorSpellCircleHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.item.ItemCAD;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class GeneralPurposeTypeCAD extends ItemCAD implements ICurioItem {
    private static final String IDTAG = "psi_ex:gpt_id";

    public GeneralPurposeTypeCAD(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        ItemStack stack = ctx.getItemInHand();

        if (!(level.getBlockEntity(ctx.getClickedPos()) instanceof GPTCADSettingTile tile)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.putUUID(IDTAG, tile.getID());
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @NotNull InteractionHand hand) {
        return InteractionResultHolder.pass(playerIn.getItemInHand(hand));
    }
    public static ItemStack makeCADWithAssemblyCustom(ItemStack assembly, List<ItemStack> components) {
        ItemStack cad = new ItemStack(PsiEXRegistry.GPTCAD.get());
        for (ItemStack component : components) {
            ItemCAD.setComponent(cad, component);
        }
        return cad;
    }
    public static void spellCast(int index, Player caster, ItemStack cad) {
        if (index < 0 || index > 99) return;

        if (!(cad.getItem() instanceof GeneralPurposeTypeCAD caditem)) return;
        if (PsiAPI.getPlayerCAD(caster) == ItemStack.EMPTY){
            if (!caster.level().isClientSide) {
                caster.sendSystemMessage(Component.translatable("psimisc.multiple_cads").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
            }
            return;
        }
        CompoundTag tag = cad.getOrCreateTag();
        if (!tag.contains(IDTAG)) return;
        UUID tileId = tag.getUUID(IDTAG);
        CompoundTag blockNbt = UUIDDataHandler.loadCompoundTag(tileId, GPTCADSettingTile.DATA_ID);
        CompoundTag itemTag = PsiEXAPI.getItemBySlot(blockNbt, index);
        if (itemTag.isEmpty()) return;
        ItemStack bullet = ItemStack.of(itemTag);
        if (bullet.isEmpty()) return;
        PlayerDataHandler.PlayerData data = PlayerDataHandler.get(caster);
        if (!caditem.getComponentInSlot(cad, EnumCADComponent.DYE).isEmpty() && ContributorSpellCircleHandler.isContributor(caster.getName().getString().toLowerCase(Locale.ROOT))) {
            ItemStack dyeStack = caditem.getComponentInSlot(cad, EnumCADComponent.DYE);
            if (!((ICADColorizer)dyeStack.getItem()).getContributorName(dyeStack).equalsIgnoreCase(caster.getName().getString())) {
                ((ICADColorizer)dyeStack.getItem()).setContributorName(dyeStack, caster.getName().getString());
                caditem.setCADComponent(cad, dyeStack);
            }
        }
        ItemCAD.cast(
                caster.getCommandSenderWorld(),
                caster,
                data,
                bullet,
                cad,
                5,
                10,
                0.05F,
                spellContext -> {}
        );
    }
}