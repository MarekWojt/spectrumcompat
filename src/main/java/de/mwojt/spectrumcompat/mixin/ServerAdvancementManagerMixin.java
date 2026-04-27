package de.mwojt.spectrumcompat.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.mwojt.spectrumcompat.HeadType;
import de.mwojt.spectrumcompat.SpectrumCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Injects per-head {@code minecraft:inventory_changed} criteria into Spectrum's
 * {@code spectrum:midgame/collect_all_mob_heads} advancement at datapack-load
 * time — one criterion per HeadType currently registered in
 * {@link SpectrumCompat#HEAD_ITEMS} (which already reflects the set of loaded
 * source mods, since registration itself is gated by
 * {@code HeadType.activeTypes()}).
 *
 * <p>Runtime JSON patching, not static file override: we mutate the raw
 * {@link JsonObject} in the map passed to {@code ServerAdvancementManager.apply}
 * <b>before</b> vanilla parses it. No {@code data/spectrum/...} override file
 * ships with our mod, so future Spectrum updates that add new vanilla heads
 * stay intact and pick up our additive criteria naturally.
 *
 * <p>Per-mod scaling is free: if e.g. VanillaBackport isn't loaded, no
 * VB heads are in {@code HEAD_ITEMS}, so no VB criteria get injected — the
 * advancement's completion set reflects exactly the heads that can actually
 * exist in this player's modpack.
 *
 * <p>Spectrum's existing criteria (all 128 vanilla/Spectrum heads) are
 * untouched. The advancement's original {@code requirements} field is absent
 * in Spectrum's JSON so vanilla generates a default "every criterion is its
 * own AND row" requirements list that automatically includes our additions.
 * If Spectrum ever adds an explicit {@code requirements} field we'd need to
 * augment it too; until then the injection is a pure append to {@code criteria}.
 *
 * <p>{@code require = 0}: if a future MC/NeoForge refactor changes the method
 * signature, our injection silently no-ops and the advancement reverts to
 * Spectrum's unmodified criteria set — not a crash, just a visible regression.
 */
@Mixin(ServerAdvancementManager.class)
public abstract class ServerAdvancementManagerMixin {

    private static final ResourceLocation SPECTRUM_COLLECT_ALL =
            ResourceLocation.fromNamespaceAndPath("spectrum", "midgame/collect_all_mob_heads");

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("HEAD"), require = 0)
    private void spectrumcompat$injectCompatHeads(
            Map<ResourceLocation, JsonElement> jsonMap,
            ResourceManager resourceManager,
            ProfilerFiller profilerFiller,
            CallbackInfo ci) {
        JsonElement element = jsonMap.get(SPECTRUM_COLLECT_ALL);
        if (element == null || !element.isJsonObject()) return;

        JsonObject root = element.getAsJsonObject();
        if (!root.has("criteria") || !root.get("criteria").isJsonObject()) return;
        JsonObject criteria = root.getAsJsonObject("criteria");

        // Group HeadTypes by their source entity — siblings of the same entity
        // (e.g. all three Moa colours) collapse into a single tag-based criterion
        // so the player only needs one variant to tick the pool. Single-variant
        // mobs still inject their own per-head criterion.
        Map<ResourceLocation, List<HeadType>> byEntity = new LinkedHashMap<>();
        for (HeadType type : SpectrumCompat.HEAD_ITEMS.keySet()) {
            byEntity.computeIfAbsent(type.entityId(), k -> new ArrayList<>()).add(type);
        }

        for (Map.Entry<ResourceLocation, List<HeadType>> entry : byEntity.entrySet()) {
            List<HeadType> types = entry.getValue();
            if (types.size() == 1) {
                HeadType single = types.get(0);
                String criterionName = "has_" + single.headBlockName();
                String itemId = SpectrumCompat.MODID + ":" + single.headBlockName();
                criteria.add(criterionName, makeInventoryChangedCriterion(itemId));
            } else {
                // Pool: tag-based criterion. For mobs where Spectrum already has
                // a singular `has_<path>_head` criterion (chicken / cow / pig —
                // their generic head is in our pool tag alongside warm+cold),
                // reuse that exact name so our tag-based entry overwrites
                // Spectrum's single-item one. Otherwise use a plural
                // `has_<path>_heads` name since no Spectrum criterion would
                // collide. Tag path matches entity path: `{entityPath}_heads`
                // under `spectrumcompat:mob_heads/`.
                String entityPath = entry.getKey().getPath();
                String spectrumName = "has_" + entityPath + "_head";
                String criterionName = criteria.has(spectrumName)
                        ? spectrumName
                        : "has_" + entityPath + "_heads";
                String tagRef = "#" + SpectrumCompat.MODID + ":mob_heads/" + entityPath + "_heads";
                criteria.add(criterionName, makeInventoryChangedCriterion(tagRef));
            }
        }
    }

    private static JsonObject makeInventoryChangedCriterion(String itemOrTagRef) {
        JsonObject criterion = new JsonObject();
        criterion.addProperty("trigger", "minecraft:inventory_changed");
        JsonObject conditions = new JsonObject();
        JsonArray itemsArray = new JsonArray();
        JsonObject itemPredicate = new JsonObject();
        itemPredicate.addProperty("items", itemOrTagRef);
        itemsArray.add(itemPredicate);
        conditions.add("items", itemsArray);
        criterion.add("conditions", conditions);
        return criterion;
    }
}
