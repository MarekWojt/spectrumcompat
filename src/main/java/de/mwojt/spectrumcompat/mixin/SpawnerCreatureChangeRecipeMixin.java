package de.mwojt.spectrumcompat.mixin;

import de.dafuqs.spectrum.recipe.InstanceRecipeInput;
import de.dafuqs.spectrum.recipe.spirit_instiller.dynamic.spawner_manipulation.SpawnerCreatureChangeRecipe;
import de.mwojt.spectrumcompat.HeadLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds variant-identifying NBT to the {@code SpawnData.entity} compound that
 * Spectrum's Spawner-Creature-Change recipe writes into the target spawner.
 *
 * <p>Without this, the recipe builds a bare {@code {id: "aether:moa"}} — the
 * spawner then rolls a random/default Moa type on each spawn. By re-reading the
 * skull stack's {@link de.mwojt.spectrumcompat.HeadType} and applying its
 * variant NBT (MoaType / Variant / weather_state), the changed spawner produces
 * the variant the player actually supplied as the head ingredient.
 *
 * <p>Runs at {@code RETURN} with {@code require = 0} so that if Spectrum
 * refactors {@code getSpawnerResultNbt} the variant addition silently degrades
 * (spawner change still works, variant defaults to random) rather than blocking
 * load.
 */
@Mixin(value = SpawnerCreatureChangeRecipe.class, remap = false)
public class SpawnerCreatureChangeRecipeMixin {

    @Inject(method = "getSpawnerResultNbt", at = @At("RETURN"), remap = false, require = 0)
    private void spectrumcompat$applyVariantNbt(
            CompoundTag spawnerTag,
            ItemStack ingredient1,
            ItemStack ingredient2,
            InstanceRecipeInput<?> input,
            CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag result = cir.getReturnValue();
        if (result == null || !result.contains("SpawnData")) return;
        CompoundTag spawnData = result.getCompound("SpawnData");
        if (!spawnData.contains("entity")) return;
        CompoundTag entity = spawnData.getCompound("entity");

        HeadLookup.fromItemStack(ingredient1)
                .or(() -> HeadLookup.fromItemStack(ingredient2))
                .ifPresent(type -> HeadLookup.applyVariantNbt(type, entity));
    }
}
