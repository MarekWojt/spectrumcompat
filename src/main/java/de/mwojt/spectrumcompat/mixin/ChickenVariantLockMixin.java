package de.mwojt.spectrumcompat.mixin;

import de.mwojt.spectrumcompat.VariantLocked;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Cancels VanillaBackport's biome-based variant override at {@code @At("HEAD")}
 * of its injected {@code vb$finalizeSpawn} callback, iff the Chicken carries
 * the {@link VariantLocked} flag set by {@code LivingEntityVariantLockMixin}
 * from the {@code spectrumcompat_lock_variant} NBT marker.
 *
 * <p>{@code require = 0} so a future VB rename degrades silently (biome
 * override resumes, memories revert to double-spawn behaviour) rather than
 * blocking load.
 */
@Mixin(Chicken.class)
public abstract class ChickenVariantLockMixin {

    @Inject(method = "vb$finalizeSpawn", at = @At("HEAD"),
            cancellable = true, remap = false, require = 0)
    private void spectrumcompat$skipVbOverride(
            ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType type, SpawnGroupData data,
            CallbackInfoReturnable<SpawnGroupData> outerCir,
            CallbackInfo ci) {
        VariantLocked self = (VariantLocked) this;
        if (self.spectrumcompat$isVariantLocked()) {
            self.spectrumcompat$setVariantLocked(false);
            ci.cancel();
        }
    }
}
