package io.github.hutuneko.psi_ex.api.spellparam;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import vazkii.psi.api.spell.SpellParam;

public class ParamCompoundTag extends SpellParam {
    public static final ResourceLocation ID = new ResourceLocation("psi_ex", "compound_tag");
    public static final ParamCompoundTag TAG_PARAM = new ParamCompoundTag("psi_ex:compound_tag");

    public ParamCompoundTag(String name) {
        super(name, SpellParam.CYAN, false);
    }

    @Override
    protected Class<?> getRequiredType() {
        return CompoundTag.class;
    }
}
