package de.mwojt.spectrumcompat;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

/**
 * Which mod provides {@code minecraft:copper_golem} in the current modpack.
 *
 * <p>Both Copper Age Backport and VanillaBackport (from 1.2.0) register the
 * golem under the same entity id, but serialize {@code "weather_state"}
 * differently: CAB as an int ordinal ({@code putInt}), VB as the vanilla
 * {@code WeatherState} serialized name ({@code putString}, e.g.
 * {@code "exposed"}). Everything that writes or matches that NBT key has to
 * pick the format via {@link #get()}.
 *
 * <p>If both mods are loaded, CAB wins. Whether that combination boots at all
 * (two registrations of the same entity id) is untested.
 */
public enum CopperGolemSource {
    COPPER_AGE_BACKPORT,
    VANILLA_BACKPORT,
    NONE;

    public static final ResourceLocation ENTITY_ID =
            ResourceLocation.withDefaultNamespace("copper_golem");

    private static final ArtifactVersion VB_FIRST_GOLEM_VERSION = new DefaultArtifactVersion("1.2.0");

    private static CopperGolemSource cached;

    /** Not safe to call before {@code ModList} is populated (i.e. during class init of the mod class). */
    public static CopperGolemSource get() {
        if (cached == null) cached = detect();
        return cached;
    }

    private static CopperGolemSource detect() {
        ModList modList = ModList.get();
        if (modList.isLoaded("copperagebackport")) return COPPER_AGE_BACKPORT;
        boolean vbHasGolem = modList.getModContainerById("vanillabackport")
                .map(c -> c.getModInfo().getVersion().compareTo(VB_FIRST_GOLEM_VERSION) >= 0)
                .orElse(false);
        return vbHasGolem ? VANILLA_BACKPORT : NONE;
    }
}
