package de.mwojt.spectrumcompat;

/**
 * Marker interface mixed into {@link net.minecraft.world.entity.LivingEntity}
 * via {@code LivingEntityVariantLockMixin}. Exposes a one-shot "variant lock"
 * flag that is set when an entity is loaded from an NBT compound containing
 * the {@code spectrumcompat_lock_variant} key — typically an entity spawned
 * from a Spectrum {@code spirit_instiller:memory} whose recipe we overrode to
 * mark the entity's variant as authoritative.
 *
 * <p>Read and cleared by the per-species mixins on {@code vb$finalizeSpawn}
 * (Chicken/Cow/Pig) to cancel VanillaBackport's biome-based variant override
 * for that one spawn call. One-shot semantics ensure a save/load round-trip
 * of a previously-locked entity doesn't suppress biome selection on future
 * respawns.
 *
 * <p>Why an interface: we can't share a {@code @Unique} field across mixins
 * that target different classes. Mixing this interface into
 * {@code LivingEntity} makes every subclass (Chicken / Cow / Pig included)
 * implement it, so the per-species mixins can cast {@code this} to
 * {@link VariantLocked} and reach the flag. This also lets the flag sit on
 * the base class, which is the correct class to intercept
 * {@code readAdditionalSaveData} on (Cow doesn't override it, only inherits
 * — so {@code @Mixin(Cow.class)} can't target that method).
 */
public interface VariantLocked {
    boolean spectrumcompat$isVariantLocked();
    void spectrumcompat$setVariantLocked(boolean locked);
}
