package de.mwojt.spectrumcompat.mixin;

import de.dafuqs.spectrum.blocks.mob_head.SpectrumSkullBlock;
import de.mwojt.spectrumcompat.HeadLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = SpectrumSkullBlock.class, remap = false)
public class SpectrumSkullBlockMixin {

    // Maps our compat-registered mob heads back to their source entity type so
    // Spectrum's Spirit-Instiller Spawner-Creature-Change recipe works with them.
    //
    // We deliberately do NOT inject into getSkullType(EntityType) — that would
    // enable MemoryToHeadRecipe.canCraftWithStacks for our memories, but
    // SpectrumDamageTypes.mobHeadDrop() has no weaponItem, so
    // TreasureHunterLootModifier bails out before our head can be emitted. Result
    // would be an empty-output recipe that silently eats the player's ingredients.
    @Inject(method = "getEntityTypeOfSkullStack", at = @At("HEAD"), cancellable = true, remap = false)
    private static void spectrumcompat$entityTypeOfSkullStack(
            ItemStack itemStack,
            CallbackInfoReturnable<Optional<EntityType<?>>> cir) {
        HeadLookup.fromItemStack(itemStack).ifPresent(type -> {
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(type.entityId());
            if (entityType != null) {
                cir.setReturnValue(Optional.of(entityType));
            }
        });
    }
}
