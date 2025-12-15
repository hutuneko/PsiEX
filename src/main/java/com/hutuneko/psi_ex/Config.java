package com.hutuneko.psi_ex;

import com.hutuneko.psi_ex.compat.CompatModule;
import moffy.addonapi.AddonModuleRegistry;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    // ForgeConfigSpecはfinal（定数）に戻し、staticブロックで確実に初期化します。
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    // ビルダーもfinalで保持します。
    private static final ForgeConfigSpec.Builder BUILDER;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        BUILDER = builder;

        // 1. メインModの設定を追加
        COMMON = new Common(builder);

        // 2. AddonAPIのモジュールを、スペックをビルドする前にビルダーに追加します。
        //    （前回のクラッシュを防ぐための位置です）
        AddonModuleRegistry.INSTANCE.LoadModule(new CompatModule(), BUILDER);

        // 3. すべての設定が追加された後、COMMON_SPECをビルドします。
        //    これにより、Modコンストラクタで参照された際にnullではなくなります。
        COMMON_SPEC = BUILDER.build();
    }

    public static class Common {
        public final ForgeConfigSpec.BooleanValue spellgeat;
        Common(ForgeConfigSpec.Builder builder) {
            builder.push("features");

            spellgeat = builder
                    .define("spellgeat", true);
            builder.pop();
        }
    }
}