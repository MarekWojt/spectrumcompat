package de.mwojt.spectrumcompat.loot;

import de.mwojt.spectrumcompat.SpectrumCompat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.Map;

/**
 * Strips Spectrum's generic mob-head from the drops of a killed chicken / cow /
 * pig when the entity has a non-temperate VanillaBackport biome variant.
 *
 * <p>Runs on {@link LivingDropsEvent}, which fires <i>after</i> all
 * {@code IGlobalLootModifier}s have contributed to the drop list (both Spectrum's
 * {@code treasure_hunter} generic-head drop and our own variant-filtered entry
 * in {@code vanillabackport_heads.json}). This sidesteps the cross-mod loot-
 * modifier ordering problem entirely — by the time this handler fires, the
 * generic head is either in the list (and we remove it) or it isn't (and the
 * handler is effectively a no-op).
 *
 * <p>Gate: this class is auto-registered on the GAME bus by
 * {@link EventBusSubscriber}; the mobs themselves only carry the {@code variant}
 * NBT when VB is loaded, so behaviour is implicitly gated — no explicit
 * {@code ModList.isLoaded} check needed.
 */
@EventBusSubscriber(modid = SpectrumCompat.MODID)
public final class VariantHeadDedupeHandler {
    private VariantHeadDedupeHandler() {}

    // entityType → Spectrum's generic head item id. Keep in sync with Spectrum's
    // own memory/fusion recipes — those reference the generic-head items we strip.
    private static final Map<ResourceLocation, ResourceLocation> GENERIC_HEAD_BY_MOB = Map.of(
            ResourceLocation.fromNamespaceAndPath("minecraft", "chicken"),
            ResourceLocation.fromNamespaceAndPath("spectrum",  "chicken_head"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "cow"),
            ResourceLocation.fromNamespaceAndPath("spectrum",  "cow_head"),
            ResourceLocation.fromNamespaceAndPath("minecraft", "pig"),
            ResourceLocation.fromNamespaceAndPath("spectrum",  "pig_head")
    );

    private static final String TEMPERATE_VARIANT = "minecraft:temperate";

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        ResourceLocation entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        ResourceLocation genericHeadId = GENERIC_HEAD_BY_MOB.get(entityTypeId);
        if (genericHeadId == null) return;

        CompoundTag entityNbt = new CompoundTag();
        entity.saveWithoutId(entityNbt);
        if (!entityNbt.contains("variant")) return;
        if (TEMPERATE_VARIANT.equals(entityNbt.getString("variant"))) return;

        event.getDrops().removeIf(itemEntity -> {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(itemEntity.getItem().getItem());
            return genericHeadId.equals(id);
        });
    }
}
