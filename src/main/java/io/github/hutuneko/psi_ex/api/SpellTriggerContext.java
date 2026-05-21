package io.github.hutuneko.psi_ex.api;

import vazkii.psi.api.spell.SpellContext;

public class SpellTriggerContext {
    private static final ThreadLocal<SpellContext> CURRENT_CONTEXT = new ThreadLocal<>();

    public static void set(SpellContext context) { CURRENT_CONTEXT.set(context); }
    public static void remove() { CURRENT_CONTEXT.remove(); }
    public static SpellContext getCurrent() { return CURRENT_CONTEXT.get(); }
    public static boolean isCasting() { return CURRENT_CONTEXT.get() != null; }
}