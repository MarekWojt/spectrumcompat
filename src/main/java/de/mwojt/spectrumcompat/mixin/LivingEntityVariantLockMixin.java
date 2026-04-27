package de.mwojt.spectrumcompat.mixin;

import de.mwojt.spectrumcompat.VariantLocked;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Implements {@link VariantLocked} on every {@link LivingEntity} and captures
 * the {@code spectrumcompat_lock_variant} marker from the NBT passed to
 * {@code readAdditionalSaveData}. LivingEntity is the correct intercept point
 * because (a) every mob we care about inherits from it, (b) LivingEntity
 * itself defines the method we inject into, whereas {@link net.minecraft.world.entity.animal.Cow}
 * doesn't override it and so can't be a {@code @Mixin} target for that method.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityVariantLockMixin implements VariantLocked {

    @Unique
    private boolean spectrumcompat$variantLocked = false;

    @Override
    public boolean spectrumcompat$isVariantLocked() {
        return spectrumcompat$variantLocked;
    }

    @Override
    public void spectrumcompat$setVariantLocked(boolean locked) {
        spectrumcompat$variantLocked = locked;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void spectrumcompat$captureVariantLock(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("spectrumcompat_lock_variant")) {
            spectrumcompat$variantLocked = true;
        }
    }
}
