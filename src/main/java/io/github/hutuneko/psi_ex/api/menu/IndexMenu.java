package io.github.hutuneko.psi_ex.api.menu;

import io.github.hutuneko.psi_ex.api.SimpleSpellContainer;
import io.github.hutuneko.psi_ex.api.SpellIndex;
import io.github.hutuneko.psi_ex.compat.PsiEXRegistry;
import io.github.hutuneko.psi_ex.net.IndexMenuPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.Spell;

import java.util.ArrayList;
import java.util.List;

public class IndexMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    private static final int INV_SLOT_START = 2;
    private static final int INV_SLOT_END = 29;
    private static final int HOTBAR_START = 29;
    private static final int HOTBAR_END = 38;

    private final ContainerLevelAccess access;
    private final Player player;
    // 特殊スロット用の簡易コンテナ
    private final SimpleSpellContainer inputContainer = new SimpleSpellContainer(1,this);
    private final SimpleSpellContainer outputContainer = new SimpleSpellContainer(1);
    private boolean updatingOutput = false;

    private final SpellIndex spellIndex = SpellIndex.getInstance();

    public IndexMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(PsiEXRegistry.INDEX_MENU.get(), pContainerId);
        this.access = pAccess;
        this.player = pPlayerInventory.player;
        // 入力スロット (0)
        this.addSlot(new SpellInputSlot(inputContainer, 0, 169, 6));

        // 出力スロット (1)
        this.addSlot(new SpellOutputSlot(outputContainer, 0, 169, 37, this));

        // プレイヤーインベントリ (3x9) [2]〜[28]
        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(pPlayerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 100 + k * 18));
            }
        }

        // ホットバー [29]〜[37]
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(pPlayerInventory, l, 8 + l * 18, 158));
        }
    }

    public static IndexMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf buf) {
        return new IndexMenu(id, inv, ContainerLevelAccess.NULL);
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(this.access, pPlayer, PsiEXRegistry.INDEX.get());
    }

    @Override
    public void slotsChanged(@NotNull Container pInventory) {
        super.slotsChanged(pInventory);
        if (updatingOutput) return;
        if (pInventory == inputContainer) {
            updateOutputSlot();
        }
    }

    private void updateOutputSlot() {
        ItemStack input = inputContainer.getItem(0);

        if (input.isEmpty()) {
            outputContainer.setItem(0, ItemStack.EMPTY);
            broadcastChanges();
            return;
        }

        SpellIndex spellIndex = SpellIndex.getInstance();

        // 術式弾の判定
        if (!spellIndex.isValidBullet(input)) {
            outputContainer.setItem(0, ItemStack.EMPTY);
            broadcastChanges();
            return;
        }

        ItemStack result = input.copy();
        result.setCount(1);

        if (ISpellAcceptor.hasSpell(input)) {
            ISpellAcceptor acceptor = ISpellAcceptor.acceptor(result);
            if (acceptor != null) {
                acceptor.setSpell(this.player, null);
            }
        } else {
            Spell spell = spellIndex.getSelectedSpell(player);
            if (spell == null) {
                outputContainer.setItem(0, ItemStack.EMPTY);
                broadcastChanges();
                return;
            }

            ISpellAcceptor acceptor = ISpellAcceptor.acceptor(result);
            if (acceptor != null) {
                acceptor.setSpell(this.player, spell);
            }
        }

        updatingOutput = true;
        outputContainer.setItem(0, result);
        updatingOutput = false;
        broadcastChanges();
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // 特殊スロット (0, 1) → プレイヤーインベントリ (2〜37)
            if (pIndex < INV_SLOT_START) {
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }

                // アウトプット枠(1)から取り出した時 → インプット枠(0)を消費
                if (pIndex == OUTPUT_SLOT) {
                    consumeInputSlot();
                }

                slot.onQuickCraft(itemstack1, itemstack);
            }
            // プレイヤーインベントリ (2〜37) → 特殊スロット (0, 1)
            else {
                // 術式弾 → 入力スロット
                if (spellIndex.isValidBullet(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // その他は移動しない
                else {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
        }

        return itemstack;
    }

    /**
     * アウトプット枠からアイテムを取り出した時にインプット枠を消費する
     */
    void consumeInputSlot() {
        ItemStack input = inputContainer.getItem(0);
        if (!input.isEmpty()) {
            spellIndex.onBulletInserted(input);
            input.shrink(1);
            if (input.isEmpty()) {
                inputContainer.setItem(0, ItemStack.EMPTY);
            }
            // インプットが変わったのでアウトプットも更新
            updateOutputSlot();
        }
    }

    @Override
    public void removed(@NotNull Player pPlayer) {
        super.removed(pPlayer);
        // 閉じたときに特殊スロットのアイテムを返却
        this.access.execute((level, pos) -> {
            if (!inputContainer.getItem(0).isEmpty()) {
                pPlayer.drop(inputContainer.getItem(0), false);
                inputContainer.setItem(0, ItemStack.EMPTY);
            }
            // アウトプット枠のアイテムはプレビューなので消滅
            outputContainer.setItem(0, ItemStack.EMPTY);
            SpellIndex.getInstance().save();
        });
    }

    public int getSelectedSpellIndex() {
        return spellIndex.getSelectedIndex(player);
    }

    public void setSelectedSpellIndex(int index) {
        SpellIndex.getInstance().setSelectedIndex(index,player);
        // 選択が変わったらアウトプットを更新
        updateOutputSlot();
        broadcastChanges();
    }

    // サーバー側のスペルリスト取得
    public List<String> getSpellNames() {
        SpellIndex index = SpellIndex.getInstance();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < index.getSpellCount(); i++) {
            names.add(index.getSpellName(i));
        }
        return names;
    }

    public int getSpellCount() {
        return SpellIndex.getInstance().getSpellCount();
    }

    public String getSpellName(int index) {
        return SpellIndex.getInstance().getSpellName(index);
    }

    public String getSpellDescription(int index) {
        return SpellIndex.getInstance().getSpellDescription(index);
    }

    public void setSpellDescription(int index, String description) {
        SpellIndex.getInstance().setSpellDescription(index, description);
    }

    // パケット処理
    public void handlePacket(IndexMenuPacket packet) {
        switch (packet.action()) {
            case SET_SELECTED -> setSelectedSpellIndex(packet.data());
            case SET_DESCRIPTION -> {
                if (packet.data() >= 0) {
                    setSpellDescription(packet.data(), packet.text());
                }
            }
            case REGISTER_BULLET -> {
                // インプット枠にアイテムをセットしてアウトプットを更新
                if (packet.stack() != null) {
                    inputContainer.setItem(0, packet.stack());
                    updateOutputSlot();
                }
            }
            case WRITE_SPELL -> {
                // アウトプット枠から取り出し → インプット消費
                if (packet.stack() != null) {
                    consumeInputSlot();
                }
            }
        }
    }

    public static class SpellInputSlot extends Slot {

        public SpellInputSlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack pStack) {
            return SpellIndex.getInstance().isValidBullet(pStack);
        }
    }

    public static class SpellOutputSlot extends Slot {
        IndexMenu menu;

        public SpellOutputSlot(Container pContainer, int pSlot, int pX, int pY, IndexMenu menu) {
            super(pContainer, pSlot, pX, pY);
            this.menu = menu;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack pStack) {
            return false;
        }

        @Override
        public void onTake(@NotNull Player pPlayer, @NotNull ItemStack pStack) {
            super.onTake(pPlayer, pStack);
            menu.consumeInputSlot();
        }
    }
}