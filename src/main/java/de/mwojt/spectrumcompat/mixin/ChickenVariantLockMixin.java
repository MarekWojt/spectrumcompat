package de.mwojt.spectrumcompat.mixin;

import de.mwojt.spectrumcompat.VariantLocked;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Cancels VanillaBackport's biome-based variant override at {@code @At("HEAD")}
 * of its {@code vb$finalizeSpawn} hook, iff the Chicken carries
 * the {@link VariantLocked} flag set by {@code LivingEntityVariantLockMixin}
 * from the {@code spectrumcompat_lock_variant} NBT marker.
 *
 * <p>Two injectors with explicit descriptors cover both VB generations (the
 * hook changed from an {@code @Inject} handler to a value-returning method in
 * VB 1.2); matching by name alone would hard-fail on a descriptor mismatch.
 * {@code require = 0} on each, so the non-matching one is skipped and a future
 * VB rename degrades silently (biome override resumes, memories revert to
 * double-spawn behaviour) rather than blocking load.
 */
@Mixin(Chicken.class)
public abstract class ChickenVariantLockMixin {

    // VB <= 1.1.x: vb$finalizeSpawn is itself an @Inject handler on finalizeSpawn.
    @Inject(method = "vb$finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;)V",
            at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void spectrumcompat$skipVbOverride(
            ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType type, SpawnGroupData data,
            CallbackInfoReturnable<SpawnGroupData> outerCir,
            CallbackInfo ci) {
        if (spectrumcompat$consumeLock()) {
            ci.cancel();
        }
    }

    // VB >= 1.2.x: vb$finalizeSpawn is a virtual hook returning the SpawnGroupData,
    // called by VB's MobMixin at RETURN of finalizeSpawn.
    @Inject(method = "vb$finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;)Lnet/minecraft/world/entity/SpawnGroupData;",
            at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void spectrumcompat$skipVbOverrideV2(
            ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType type, SpawnGroupData data,
            CallbackInfoReturnable<SpawnGroupData> cir) {
        if (spectrumcompat$consumeLock()) {
            cir.setReturnValue(data);
        }
    }

    @Unique
    private boolean spectrumcompat$consumeLock() {
        VariantLocked self = (VariantLocked) this;
        if (!self.spectrumcompat$isVariantLocked()) {
            return false;
        }
        self.spectrumcompat$setVariantLocked(false);
        return true;
    }
}
