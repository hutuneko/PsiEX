package io.github.hutuneko.psi_ex.system.capability;

import net.minecraft.nbt.CompoundTag;
import vazkii.psi.api.internal.Vector3;

import java.util.HashMap;
import java.util.Map;

public class PlayerData implements IPlayerData {
    // Vector3 用
    private final Map<String, Vector3> values = new HashMap<>();
    // double 用
    private final Map<String, Double> d = new HashMap<>();

    @Override
    public void setVector(String key, Vector3 vec) {
        if (vec != null) values.put(key, vec);
    }

    @Override
    public void setDouble(String key, double val) {
        d.put(key, val);
    }

    /** 任意値の取得（Vector3 を返す想定） */
    @Override
    public Vector3 getValue(String key) {
        return values.get(key);
    }

    /** double の取得 */
    @Override
    public Double getD(String key) {
        return d.get(key);
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        // Vector3 は CompoundTag で x,y,z を持たせて <key>_vec で保存
        for (Map.Entry<String, Vector3> e : values.entrySet()) {
            CompoundTag v = new CompoundTag();
            Vector3 vec = e.getValue();
            v.putDouble("x", vec.x);
            v.putDouble("y", vec.y);
            v.putDouble("z", vec.z);
            tag.put(e.getKey() + "_vec", v);
            System.out.println(e.getKey() + "_vec" + "kk");
        }

        // double は <key>_dbl で保存
        for (Map.Entry<String, Double> e : d.entrySet()) {
            tag.putDouble(e.getKey() + "_dbl", e.getValue());
        }

        return tag;
    }

    /** NBT からの復元 */
    @Override
    public void load(CompoundTag tag) {
        values.clear();
        d.clear();

        for (String key : tag.getAllKeys()) {
            String substring = key.substring(0, key.length() - 4);
            if (key.endsWith("_vec") && tag.contains(key, 10)) { // 10 = CompoundTag
                CompoundTag v = tag.getCompound(key);
                double x = v.getDouble("x");
                double y = v.getDouble("y");
                double z = v.getDouble("z");
                values.put(substring, new Vector3(x, y, z));
            }
            // double
            else if (key.endsWith("_dbl")) {
                d.put(substring, tag.getDouble(key));
            }
        }
    }
}
