package io.github.hutuneko.psi_ex.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UUIDDataHandler {

    public static class UUIDSavedData extends SavedData {

        private final Map<UUID, CompoundTag> dataMap = new HashMap<>();

        public UUIDSavedData() {
        }

        public static UUIDSavedData load(CompoundTag tag) {
            UUIDSavedData savedData = new UUIDSavedData();

            CompoundTag dataTag = tag.getCompound("data");
            for (String key : dataTag.getAllKeys()) {
                try {
                    UUID uuid = UUID.fromString(key);
                    CompoundTag compoundTag = dataTag.getCompound(key);
                    savedData.dataMap.put(uuid, compoundTag);
                } catch (IllegalArgumentException ignored) {
                }
            }

            return savedData;
        }

        @Override
        public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
            CompoundTag dataTag = new CompoundTag();

            for (Map.Entry<UUID, CompoundTag> entry : dataMap.entrySet()) {
                dataTag.put(entry.getKey().toString(), entry.getValue());
            }

            tag.put("data", dataTag);
            return tag;
        }

        public CompoundTag getCompoundTag(UUID uuid) {
            return dataMap.getOrDefault(uuid, new CompoundTag());
        }

        public void setCompoundTag(UUID uuid, CompoundTag compoundTag) {
            dataMap.put(uuid, compoundTag);
            setDirty();
        }
    }

    private static UUIDSavedData getSavedData(String dataId) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null;
        }

        DimensionDataStorage storage = server.overworld().getDataStorage();
        return storage.computeIfAbsent(UUIDSavedData::load, UUIDSavedData::new, dataId);
    }

    public static void saveCompoundTag(UUID uuid, CompoundTag compoundTag,String dataId) {
        UUIDSavedData savedData = getSavedData(dataId);
        if (savedData != null) {
            savedData.setCompoundTag(uuid, compoundTag);
        }
    }

    public static CompoundTag loadCompoundTag(UUID uuid,String dataId) {
        UUIDSavedData savedData = getSavedData(dataId);
        if (savedData != null) {
            return savedData.getCompoundTag(uuid);
        }
        return new CompoundTag();
    }
}