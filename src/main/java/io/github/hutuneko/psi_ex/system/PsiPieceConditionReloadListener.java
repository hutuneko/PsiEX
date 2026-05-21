package io.github.hutuneko.psi_ex.system;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.hutuneko.psi_ex.system.attribute.AttributeValueCondition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PsiPieceConditionReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setLenient().create();

    public PsiPieceConditionReloadListener() {
        super(GSON, "psi_piece_conditions");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        PieceConditionRegistry.clear();
        object.forEach((id, element) -> {
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                PieceCondition cond = parseCondition(obj);
                if (cond != null && obj.has("piece")) {
                    ResourceLocation pieceId = new ResourceLocation(obj.get("piece").getAsString());
                    PieceConditionRegistry.register(pieceId, cond);
                }
            }
        });
    }

    @Nullable
    private PieceCondition parseCondition(JsonObject obj) {
        if (obj.has("all")) {
            List<PieceCondition> list = new ArrayList<>();
            obj.getAsJsonArray("all").forEach(e -> {
                if (e.isJsonObject()) {
                    PieceCondition sub = parseCondition(e.getAsJsonObject());
                    if (sub != null) list.add(sub);
                }
            });
            return PieceConditions.all(list);
        }

        if (obj.has("any")) {
            List<PieceCondition> list = new ArrayList<>();
            obj.getAsJsonArray("any").forEach(e -> {
                if (e.isJsonObject()) {
                    PieceCondition sub = parseCondition(e.getAsJsonObject());
                    if (sub != null) list.add(sub);
                }
            });
            return PieceConditions.any(list);
        }

        PieceCondition leaf = parseLeaf(obj);

        if (leaf != null && obj.has("message")) {
            return PieceConditions.withMessage(leaf, Component.literal(obj.get("message").getAsString()));
        }

        return leaf;
    }

    private PieceCondition parseLeaf(JsonObject leaf) {
        if (leaf.has("cond") && leaf.get("cond").isJsonObject()) {
            return parseCondition(leaf.getAsJsonObject("cond"));
        }
        JsonElement typeElement = leaf.get("type");
        if (typeElement == null) return null;
        String type = leaf.get("type").getAsString();
        switch (type) {
            case "attribute_at_least": {
                ResourceLocation attr = new ResourceLocation(leaf.get("attribute").getAsString());
                double val = leaf.get("value").getAsDouble();
                AttributeValueCondition.Mode mode = AttributeValueCondition.Mode.valueOf(leaf.get("mode").getAsString());
                return new AttributeValueCondition(attr, val, mode, true, null);
            }
            case "attribute_at_most": {
                ResourceLocation attr = new ResourceLocation(leaf.get("attribute").getAsString());
                double val = leaf.get("value").getAsDouble();
                AttributeValueCondition.Mode mode = AttributeValueCondition.Mode.valueOf(leaf.get("mode").getAsString());
                return new AttributeValueCondition(attr, val, mode, false, null);
            }
            case "attribute_range": {
                ResourceLocation attr = new ResourceLocation(leaf.get("attribute").getAsString());
                double min = leaf.get("min").getAsDouble();
                double max = leaf.get("max").getAsDouble();
                AttributeValueCondition.Mode mode = AttributeValueCondition.Mode.valueOf(leaf.get("mode").getAsString());
                return PieceConditions.and(
                        new AttributeValueCondition(attr, min, mode, true, null),
                        new AttributeValueCondition(attr, max, mode, false, null)
                );
            }
            default:
                return null;
        }
    }
}