package de.mwojt.spectrumcompat.mixin;

import de.mwojt.spectrumcompat.VariantLocked;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Cow counterpart of {@link ChickenVariantLockMixin}. See there for rationale. */
@Mixin(Cow.class)
public abstract class CowVariantLockMixin {

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
