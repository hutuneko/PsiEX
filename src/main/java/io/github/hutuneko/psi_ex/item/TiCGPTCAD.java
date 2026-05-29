package io.github.hutuneko.psi_ex.item;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import io.github.hutuneko.psi_ex.compat.tic.TiCCompatModule;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import vazkii.psi.api.cad.CADStatEvent;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.EnumCADStat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static slimeknights.tconstruct.library.tools.stat.ToolStats.DURABILITY;

public class TiCGPTCAD extends GeneralPurposeTypeCAD implements IModifiableDisplay {
    private final ToolDefinition toolDefinition;
    protected ItemStack toolForRendering;

    public TiCGPTCAD(Properties properties) {
        super(properties);
        this.toolDefinition = ToolDefinition.create(TiCCompatModule.TICGPTCAD);
    }
    @Override
    public int getStatValue(ItemStack stack, EnumCADStat stat) {

        if (stack.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(stack);

            int baseStatValue = 0;

            if (stat == EnumCADStat.OVERFLOW) {
                float durability = tool.getStats().get(ToolStats.DURABILITY);
                baseStatValue = (int) (durability / 2);

            } else if (stat == EnumCADStat.EFFICIENCY) {
                float speed = tool.getStats().get(ToolStats.MINING_SPEED);
                baseStatValue = (int) (speed * 5);

            } else if (stat == EnumCADStat.POTENCY) {
                float durability = tool.getStats().get(DURABILITY);
                baseStatValue = (int) (durability / 3);
            } else if (stat == EnumCADStat.COMPLEXITY) {
                float attackSpeed = tool.getStats().get(ToolStats.ATTACK_SPEED);
                baseStatValue = (int) ((2.0f - attackSpeed) * 20);

            } else if (stat == EnumCADStat.PROJECTION) {
                float repairSpeed = tool.getStats().get(ToolStats.ATTACK_SPEED);
                baseStatValue = (int) (repairSpeed * 10);

            } else if (stat == EnumCADStat.BANDWIDTH) {
                int modifiers = tool.getStats().getInt(ToolStats.PROJECTILE_DAMAGE);
                baseStatValue = modifiers * 50;

            } else if (stat == EnumCADStat.SOCKETS) {
                int modifiers = tool.getStats().getInt(ToolStats.DURABILITY);
                baseStatValue = modifiers / 100 + 1;

            } else if (stat == EnumCADStat.SAVED_VECTORS) {
                float damage = tool.getStats().get(DURABILITY);
                baseStatValue = (int) (damage / 10);

            }

            CADStatEvent event = new CADStatEvent(stat, stack, ItemStack.EMPTY, baseStatValue);
            MinecraftForge.EVENT_BUS.post(event);

            return event.getStatValue();
        }
        return -1;
    }
    @Override
    public ItemStack getComponentInSlot(ItemStack stack, EnumCADComponent type) {
        if (!(stack.getItem() instanceof IModifiable))return ItemStack.EMPTY;
        IToolStackView tool = ToolStack.from(stack);

        int partIndex = -1;
        if (type == EnumCADComponent.CORE) partIndex = 0;
        else if (type == EnumCADComponent.ASSEMBLY) partIndex = 1;
        else if (type == EnumCADComponent.BATTERY) partIndex = 2;
        else if (type == EnumCADComponent.SOCKET) partIndex = 3;

        if (partIndex >= 0) {
            List<MaterialStatsId> materialStatsIds = ToolMaterialHook.stats(toolDefinition);
            MaterialNBT nbt = tool.getMaterials();
            Set<Component> nameMaterials = Sets.newLinkedHashSet();
            MaterialVariantId firstMaterial = null;
            IMaterialRegistry registry = MaterialRegistry.getInstance();
            for(int i = 0; i < materialStatsIds.size(); ++i) {
                if (i < nbt.size() && registry.canRepair(materialStatsIds.get(i))) {
                    MaterialVariantId material = nbt.get(i).getVariant();
                    if (!IMaterial.UNKNOWN_ID.equals(material)) {
                        if (firstMaterial == null) {
                            firstMaterial = material;
                        }
                        nameMaterials.add(MaterialTooltipCache.getDisplayName(material));
                    }
                }
            }
            ItemStack dummyStack;
            if (partIndex == 0) dummyStack = new ItemStack(TiCCompatModule.TIC_CAD_CORE);
            else if (partIndex == 1) dummyStack = new ItemStack(TiCCompatModule.TIC_CAD_ASSEMBLY);
            else if (partIndex == 2) dummyStack = new ItemStack(TiCCompatModule.TIC_CAD_BATTERY);
            else dummyStack = new ItemStack(TiCCompatModule.TIC_CAD_SOCKET);
            MutableComponent name = Component.literal("");
            List<Component> materialList = new ArrayList<>(nameMaterials);
            if (partIndex < materialList.size()) {
                Component nthMaterial;
                if (materialList.size() == 1){
                    nthMaterial = materialList.get(0);
                }else {
                    nthMaterial = materialList.get(partIndex);
                }
                name.append(nthMaterial);
            }
            dummyStack.setHoverName((name.append(dummyStack.getDisplayName())));

            return dummyStack;
        }
        return ItemStack.EMPTY;
    }
    @Override
    public @NotNull ToolDefinition getToolDefinition() {
        return toolDefinition;
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        TooltipUtil.addInformation(this, stack, level, tooltip, SafeClientAccess.getTooltipKey(), flag);
    }
    @Override
    public @NotNull ItemStack getRenderTool() {
        if (this.toolForRendering == null) {
            this.toolForRendering = ToolBuildHandler.buildToolForRendering(this, this.getToolDefinition());
        }
        return this.toolForRendering;
    }
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return getAttributeModifiers(ToolStack.from(stack), slot);
    }
    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull IToolStackView tool, @NotNull EquipmentSlot slot) {
        return AttributesModifierHook.getHeldAttributeModifiers(tool, slot);
    }
    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return TooltipUtil.getDisplayName(stack, toolDefinition);
    }
}
