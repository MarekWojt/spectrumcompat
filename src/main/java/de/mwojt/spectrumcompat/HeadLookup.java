package de.mwojt.spectrumcompat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Shared lookup utilities used by the Spectrum interop mixins and by anywhere
 * else that needs to go from a head {@link ItemStack} back to its {@link HeadType}
 * or to apply the variant's NBT to a spawn compound.
 *
 * <p>The variant-NBT appliers live here — not on {@code HeadType} itself — so the
 * enum stays a lean descriptor. A {@code HeadType} with no entry in
 * {@link #VARIANT_APPLIERS} is treated as non-variant (no-op).
 *
 * <p><b>When adding a new variant:</b> register its applier here alongside the
 * enum entry. Mismatches between the applier's NBT key/value and the mob's
 * actual {@code addAdditionalSaveData} serialization produce silent "default
 * variant" spawns (no crash, wrong color) — verify from the upstream bytecode
 * when uncertain.
 */
public final class HeadLookup {
    private HeadLookup() {}

    private static final Map<HeadType, Consumer<CompoundTag>> VARIANT_APPLIERS =
            new EnumMap<>(HeadType.class);

    static {
        // Moa — "MoaType" string (ResourceLocation of a moa_type registry entry).
        VARIANT_APPLIERS.put(HeadType.BLUE_MOA,  t -> t.putString("MoaType", "aether:blue"));
        VARIANT_APPLIERS.put(HeadType.WHITE_MOA, t -> t.putString("MoaType", "aether:white"));
        VARIANT_APPLIERS.put(HeadType.BLACK_MOA, t -> t.putString("MoaType", "aether:black"));

        // Quail — "Variant" int ordinal (QuailVariants enum).
        VARIANT_APPLIERS.put(HeadType.OLD_GREEN_QUAIL,     t -> t.putInt("Variant", 0));
        VARIANT_APPLIERS.put(HeadType.PINK_QUAIL,          t -> t.putInt("Variant", 1));
        VARIANT_APPLIERS.put(HeadType.PURPLE_QUAIL,        t -> t.putInt("Variant", 2));
        VARIANT_APPLIERS.put(HeadType.TROPICAL_BLUE_QUAIL, t -> t.putInt("Variant", 3));
        VARIANT_APPLIERS.put(HeadType.FADED_YELLOW_QUAIL,  t -> t.putInt("Variant", 4));
        VARIANT_APPLIERS.put(HeadType.LIGHT_BLUE_QUAIL,    t -> t.putInt("Variant", 5));
        VARIANT_APPLIERS.put(HeadType.COPPER_QUAIL,        t -> t.putInt("Variant", 6));

        // Vanilla animal variants (chicken / cow / pig from 1.21.2 backport).
        // NBT key "variant" holds a ResourceLocation string (VariantUtils
        // pattern in VB). Only warm / cold registered; temperate is Spectrum's
        // own default head.
        VARIANT_APPLIERS.put(HeadType.WARM_CHICKEN, t -> t.putString("variant", "minecraft:warm"));
        VARIANT_APPLIERS.put(HeadType.COLD_CHICKEN, t -> t.putString("variant", "minecraft:cold"));
        VARIANT_APPLIERS.put(HeadType.WARM_COW,     t -> t.putString("variant", "minecraft:warm"));
        VARIANT_APPLIERS.put(HeadType.COLD_COW,     t -> t.putString("variant", "minecraft:cold"));
        VARIANT_APPLIERS.put(HeadType.WARM_PIG,     t -> t.putString("variant", "minecraft:warm"));
        VARIANT_APPLIERS.put(HeadType.COLD_PIG,     t -> t.putString("variant", "minecraft:cold"));

        // Copper Golem — "weather_state" int ordinal (WeatheringCopper$WeatherState).
        // Waxed variants spawn as their unwaxed oxidation-state equivalent: waxed
        // golem entities don't exist (wax turns the killable entity into a permanent
        // statue block) so we drop the wax bit and keep only the oxidation match.
        // Each Copper Golem applier also sets "next_weather_age" to -1L
        // (UNSET_WEATHERING_TICK) — without it the tick defaults to 0, causing
        // CopperGolemEntity.updateWeathering to advance the oxidation one step on
        // the first tick after spawn (so {weather_state:0} would spawn as EXPOSED,
        // not UNAFFECTED). -1L tells updateWeathering to initialize a proper
        // random cooldown on its first pass instead of advancing immediately.
        VARIANT_APPLIERS.put(HeadType.COPPER_GOLEM,                  t -> { t.putInt("weather_state", 0); t.putLong("next_weather_age", -1L); });
        VARIANT_APPLIERS.put(HeadType.EXPOSED_COPPER_GOLEM,          t -> { t.putInt("weather_state", 1); t.putLong("next_weather_age", -1L); });
        VARIANT_APPLIERS.put(HeadType.WEATHERED_COPPER_GOLEM,        t -> { t.putInt("weather_state", 2); t.putLong("next_weather_age", -1L); });
        VARIANT_APPLIERS.put(HeadType.OXIDIZED_COPPER_GOLEM,         t -> { t.putInt("weather_state", 3); t.putLong("next_weather_age", -1L); });
        VARIANT_APPLIERS.put(HeadType.WAXED_COPPER_GOLEM,            t -> { t.putInt("weather_state", 0); t.putLong("next_weather_age", -1L); });
        VARIANT_APPLIERS.put(HeadType.WAXED_EXPOSED_COPPER_GOLEM,    t -> { t.putInt("weather_state", 1); t.putLong("next_weather_age", -1L); });
        VARIANT_APPLIERS.put(HeadType.WAXED_WEATHERED_COPPER_GOLEM,  t -> { t.putInt("weather_state", 2); t.putLong("next_weather_age", -1L); });
        VARIANT_APPLIERS.put(HeadType.WAXED_OXIDIZED_COPPER_GOLEM,   t -> { t.putInt("weather_state", 3); t.putLong("next_weather_age", -1L); });
    }

    /**
     * Reverse the HEAD_ITEMS registration to find the HeadType behind a placed
     * head's block item. Returns empty if the stack isn't one of our heads.
     */
    public static Optional<HeadType> fromItemStack(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) return Optional.empty();
        Block block = blockItem.getBlock();
        for (Map.Entry<HeadType, DeferredBlock<HeadBlock>> entry
                : SpectrumCompat.FLOOR_BLOCKS.entrySet()) {
            if (entry.getValue().get() == block) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    /**
     * Mutate {@code entityCompound} to add the variant-identifying NBT for the
     * given {@link HeadType}. No-op for non-variant heads. Called by the
     * Spawner-Creature-Change mixin so the changed spawner produces the correct
     * variant, not a random default — matching the same NBT the treasure_hunter
     * loot modifier filters on.
     */
    public static void applyVariantNbt(HeadType type, CompoundTag entityCompound) {
        Consumer<CompoundTag> applier = VARIANT_APPLIERS.get(type);
        if (applier != null) applier.accept(entityCompound);
    }
}
