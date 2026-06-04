package io.github.hutuneko.psi_ex.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.item.ItemSpellBullet;

import java.util.*;

public class SpellIndex {

    private static final UUID GLOBAL_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String DATA_ID = "psi_ex_global_spell_index";
    private static SpellIndex INSTANCE;

    private static final String KEY_SPELLS = "spells";
    private static final String KEY_SPELL_NAME = "name";
    private static final String KEY_SPELL = "spell";
    private static final String KEY_SPELL_DESCRIPTION = "description";
    private static final String KEY_SPELL_ID = "id";
    private static final String KEY_SELECTED = "selected";

    private final List<SpellData> spells = new ArrayList<>();
    private int selectedIndex = -1;
    private final Map<UUID,Integer> indexMap = new HashMap<>();
    private boolean dirty = false;

    public record SpellData(String name, Spell spell, String description, UUID id) {
        public static SpellData newSpellData(Spell spell) {
            return new SpellData(spell.name, spell, "", UUID.randomUUID());
        }
    }

    private SpellIndex() {
        load();
    }

    public static SpellIndex getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SpellIndex();
        }
        return INSTANCE;
    }

    // ============================================================
    // 永続化
    // ============================================================

    public void load() {
        spells.clear();
        CompoundTag tag = UUIDDataHandler.loadCompoundTag(GLOBAL_UUID, DATA_ID);

        if (tag.contains(KEY_SPELLS, 9)) {
            ListTag list = tag.getList(KEY_SPELLS, 10);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                String name = entry.getString(KEY_SPELL_NAME);
                Spell spell = Spell.createFromNBT(entry.getCompound(KEY_SPELL));
                String description = entry.getString(KEY_SPELL_DESCRIPTION);
                UUID id = entry.hasUUID(KEY_SPELL_ID)
                        ? entry.getUUID(KEY_SPELL_ID)
                        : UUID.randomUUID();
                spells.add(new SpellData(name, spell, description, id));
            }
        }
        selectedIndex = tag.getInt(KEY_SELECTED);
        if (selectedIndex >= spells.size()) {
            selectedIndex = -1;
        }
    }

    public void save() {
        if (!dirty) return;

        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (SpellData data : spells) {
            CompoundTag entry = new CompoundTag();
            entry.putString(KEY_SPELL_NAME, data.name());
            CompoundTag t = new CompoundTag();
            data.spell().writeToNBT(t);
            entry.put(KEY_SPELL, t);
            entry.putString(KEY_SPELL_DESCRIPTION, data.description());
            entry.putUUID(KEY_SPELL_ID, data.id());
            list.add(entry);
        }

        tag.put(KEY_SPELLS, list);
        tag.putInt(KEY_SELECTED, selectedIndex);

        UUIDDataHandler.saveCompoundTag(GLOBAL_UUID, tag, DATA_ID);
        dirty = false;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public int getSpellCount() {
        return spells.size();
    }

    public String getSpellName(int index) {
        if (index < 0 || index >= spells.size()) return "";
        return spells.get(index).name();
    }

    public String getSpellDescription(int index) {
        if (index < 0 || index >= spells.size()) return "";
        return spells.get(index).description();
    }

    public void setSpellDescription(int index, String description) {
        if (index < 0 || index >= spells.size()) return;
        SpellData old = spells.get(index);
        spells.set(index, new SpellData(old.name(), old.spell(), description, old.id()));
        markDirty();
    }

    public void setSelectedIndex(int index, Player player) {
        if (index >= -1 && index < spells.size()) {
            this.indexMap.put(player.getUUID(), index);
            markDirty();
        }
    }
    public int getSelectedIndex(Player player) {
        indexMap.putIfAbsent(player.getUUID(), 0);
        return indexMap.get(player.getUUID());
    }

    public Spell getSpellByName(String name) {
        for (SpellData data : spells) {
            if (data.name().equals(name)) {
                return data.spell();
            }
        }
        return null;
    }
    public int getSpellIndex(String name) {
        int index = 0;
        for (SpellData data : spells) {
            if (data.name().equals(name)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * 選択中のSpellを取得
     */
    public Spell getSelectedSpell(Player player) {
        int selectedIndex = indexMap.get(player.getUUID());
        if (selectedIndex < 0 || selectedIndex >= spells.size()) {
            return null;
        }
        return spells.get(selectedIndex).spell();
    }

    public boolean isValidBullet(ItemStack stack) {
        return stack.getItem() instanceof ItemSpellBullet;
    }

    public boolean isValidEmptyBullet(ItemStack stack) {
        return !ISpellAcceptor.hasSpell(stack);
    }

    public boolean onBulletInserted(ItemStack bullet) {
        if (ISpellAcceptor.hasSpell(bullet)) {
            ISpellAcceptor acceptor = ISpellAcceptor.acceptor(bullet);
            Spell spell = acceptor.getSpell();
            spells.add(SpellData.newSpellData(spell));
            markDirty();
            return true;
        }
        return false;
    }

    public ItemStack onEmptyBulletInserted(ItemStack emptyBullet,Player player) {
        int selectedIndex = indexMap.get(player.getUUID());
        if (selectedIndex < 0 || selectedIndex >= spells.size()) {
            return null;
        }

        Spell spell = spells.get(selectedIndex).spell();
        if (spell == null) return null;

        ISpellAcceptor acceptor = ISpellAcceptor.acceptor(emptyBullet);
        if (acceptor != null) {
            acceptor.setSpell(null, spell);
            return emptyBullet;
        }
        return null;
    }
}