package com.hutuneko.psi_ex.item;

import vazkii.psi.common.item.component.ItemCADAssembly;

public class ItemGPTCADAssembly extends ItemCADAssembly {
    private final int color;

    public ItemGPTCADAssembly(Properties props, int color) {
        super(props, "test");
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
