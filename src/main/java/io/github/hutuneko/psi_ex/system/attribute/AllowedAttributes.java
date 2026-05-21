package io.github.hutuneko.psi_ex.system.attribute;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.Set;

public final class AllowedAttributes {

    private static final Set<ResourceLocation> ALLOWED = Set.of(
            Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(PsiEXAttributes.PSI_ACCELERATION_POINT.get())),
            Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(PsiEXAttributes.PSI_VIBRATION_POINT.get())),
            Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(PsiEXAttributes.PSI_WEIGHTING_POINT.get())),
            Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(PsiEXAttributes.PSI_EMISSION_POINT.get())),
            Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(PsiEXAttributes.PSI_DIVERGENCE_POINT.get())),
            Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(PsiEXAttributes.PSI_MOVEMENT_POINT.get())),
            Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(PsiEXAttributes.PSI_CONVERGENCE_POINT.get())),
            Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(PsiEXAttributes.PSI_ABSORPTION_POINT.get())),
            Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(PsiEXAttributes.PSI_ANCIENTRITES_POINT.get()))
    );

    private AllowedAttributes() {}

    public static boolean isAllowed(ResourceLocation id) {
        return ALLOWED.contains(id);
    }
    public static MutableComponent getAttributeName(ResourceLocation attributeId) {
        String translationKey = String.format("attribute.name.%s.%s",
                attributeId.getNamespace(),
                attributeId.getPath());

        return Component.translatable(translationKey);
    }
}
