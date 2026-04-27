package de.mwojt.spectrumcompat.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

/**
 * Vanilla {@link BlockEntityType#validBlocks} is an immutable set baked at mod
 * init, so we cannot append our head blocks through a public API. This accessor
 * lets us swap the set for a mutable copy plus our blocks during common setup.
 */
@Mixin(BlockEntityType.class)
public interface BlockEntityTypeAccessor {
    @Accessor("validBlocks")
    Set<Block> spectrumcompat$getValidBlocks();

    @Accessor("validBlocks")
    @Mutable
    void spectrumcompat$setValidBlocks(Set<Block> validBlocks);
}
