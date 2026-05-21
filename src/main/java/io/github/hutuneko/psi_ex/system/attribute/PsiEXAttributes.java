package io.github.hutuneko.psi_ex.system.attribute;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

import static io.github.hutuneko.psi_ex.compat.PsiEXRegistry.ATTRIBUTES;

public final class PsiEXAttributes {


    public static final RegistryObject<Attribute> PSI_SPELL_RANGE =
            ATTRIBUTES.register("psi_spell_range",
                    () -> new RangedAttribute(
                            "attribute.name.psi_ex.psi_spell_range",
                            32.0D, 0.0D, 1024.0D
                    ).setSyncable(true));
    public static final RegistryObject<Attribute> PSI_PSION_POINT =
            ATTRIBUTES.register("psi_psion_point",
                    () -> new RangedAttribute(
                            "attribute.name.psi_ex.psi_psion_point",
                            100.0D, Double.MIN_VALUE, Double.MAX_VALUE
                    ).setSyncable(true));

    public static final RegistryObject<Attribute> PSI_ACCELERATION_POINT =
            ATTRIBUTES.register("psi_acceleration_point",
                    () -> new RangedAttribute(
                            "attribute.name.psi_ex.psi_acceleration_point",
                            100,Double.MIN_VALUE,Double.MAX_VALUE
                    ).setSyncable(true));//加速

    public static final RegistryObject<Attribute> PSI_WEIGHTING_POINT =
            ATTRIBUTES.register("psi_weighting_point",
                    () -> new RangedAttribute(
                            "attribute.name.psi_ex.psi_weighting_point",
                            100,Double.MIN_VALUE,Double.MAX_VALUE
                    ).setSyncable(true));//加重

    public static final RegistryObject<Attribute> PSI_MOVEMENT_POINT =
            ATTRIBUTES.register("psi_movement_point",
                    () -> new RangedAttribute(
                            "attribute.name.psi_ex.psi_movement_point",
                            100,Double.MIN_VALUE,Double.MAX_VALUE
                    ).setSyncable(true));//移動

    public static final RegistryObject<Attribute> PSI_VIBRATION_POINT =
            ATTRIBUTES.register("psi_vibration_point",
                    () -> new RangedAttribute(
                            "attribute.name.psi_ex.psi_vibration_point",
                            100,Double.MIN_VALUE,Double.MAX_VALUE
                    ).setSyncable(true));//振動

    public static final RegistryObject<Attribute> PSI_CONVERGENCE_POINT =
            ATTRIBUTES.register("psi_convergence_point",
                    () -> new RangedAttribute(
                            "attribute.name.psi_ex.psi_convergence_point",
                            100,Double.MIN_VALUE,Double.MAX_VALUE
                    ).setSyncable(true));//収束

    public static final RegistryObject<Attribute> PSI_DIVERGENCE_POINT =
            ATTRIBUTES.register("psi_divergence_point",
                    () -> new RangedAttribute(
                            "attribute.name.psi_ex.psi_divergence_point",
                            100,Double.MIN_VALUE,Double.MAX_VALUE
                    ).setSyncable(true));//発散

    public static final RegistryObject<Attribute> PSI_ABSORPTION_POINT =
            ATTRIBUTES.register("psi_absorption_point",
                    () -> new RangedAttribute(
                            "attribute.name.psi_ex.psi_absorption_point",
                            100,Double.MIN_VALUE,Double.MAX_VALUE
                    ).setSyncable(true));//吸収

    public static final RegistryObject<Attribute> PSI_EMISSION_POINT =
            ATTRIBUTES.register("psi_emission_point",
                    () -> new RangedAttribute(
                            "attribute.name.psi_ex.psi_emission_point",
                            100,Double.MIN_VALUE,Double.MAX_VALUE
                    ).setSyncable(true));//放出
    public static final RegistryObject<Attribute> PSI_ANCIENTRITES_POINT =
            ATTRIBUTES.register("psi_ancientrites_point",
                    () -> new RangedAttribute(
                            "attribute.name.psi_ex.psi_ancientrites_point",
                            100,Double.MIN_VALUE,Double.MAX_VALUE
                    ).setSyncable(true));//古式
    public static void register(IEventBus modBus) {
        ATTRIBUTES.register(modBus);
    }
}

