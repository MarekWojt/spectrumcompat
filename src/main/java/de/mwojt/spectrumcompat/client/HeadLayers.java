package de.mwojt.spectrumcompat.client;

import de.mwojt.spectrumcompat.SpectrumCompat;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public final class HeadLayers {
    private HeadLayers() {}

    // Aether
    public static final ModelLayerLocation PHYG = layer("phyg");
    public static final ModelLayerLocation FLYING_COW = layer("flying_cow");
    public static final ModelLayerLocation SHEEPUFF = layer("sheepuff");
    public static final ModelLayerLocation AERBUNNY = layer("aerbunny");
    public static final ModelLayerLocation MOA_LIKE = layer("moa_like");
    public static final ModelLayerLocation SWET = layer("swet");
    public static final ModelLayerLocation AECHOR_PLANT = layer("aechor_plant");
    public static final ModelLayerLocation ZEPHYR = layer("zephyr");
    public static final ModelLayerLocation MIMIC = layer("mimic");
    public static final ModelLayerLocation SENTRY = layer("sentry");
    public static final ModelLayerLocation HUMANOID_SMALL = layer("humanoid_small");
    public static final ModelLayerLocation SUN_SPIRIT = layer("sun_spirit");
    public static final ModelLayerLocation AERWHALE = layer("aerwhale");

    // Deep Aether
    public static final ModelLayerLocation QUAIL = layer("quail");
    public static final ModelLayerLocation AERGLOW_FISH = layer("aerglow_fish");
    public static final ModelLayerLocation VENOMITE = layer("venomite");
    public static final ModelLayerLocation BABY_ZEPHYR = layer("baby_zephyr");
    public static final ModelLayerLocation EOTS_CONTROLLER = layer("eots_controller");

    // VanillaBackport
    public static final ModelLayerLocation HAPPY_GHAST = layer("happy_ghast");
    // VB ships warm and cold chicken on the same 64×32 atlas, but the cold
    // chicken's head carries an extra feather-crest cube (6×3×4 at
    // texOffs(44,0)) sitting on top of the main head — sampled from a region
    // of the atlas the warm chicken's head never touches. Separate layer
    // because the geometry differs.
    public static final ModelLayerLocation WARM_CHICKEN = layer("warm_chicken");
    public static final ModelLayerLocation COLD_CHICKEN = layer("cold_chicken");
    // VB ships separate warm/cold cow models with 64×64 atlases and different horn
    // geometries (warm: short straight horns; cold: long forward-tilted yak horns
    // with their own texture regions). We can't share one layer across the two.
    public static final ModelLayerLocation WARM_COW = layer("warm_cow");
    public static final ModelLayerLocation COLD_COW = layer("cold_cow");
    // VB ships warm_pig.png at 64×32 (vanilla atlas) but cold_pig.png at 64×64
    // (extra mantle/fluff region in the lower half). Head geometry itself is
    // identical, but a single 64×32 LayerDefinition would mis-normalise the
    // cold-pig V coordinates, so the two need separate layers with matching
    // atlas sizes.
    public static final ModelLayerLocation WARM_PIG = layer("warm_pig");
    public static final ModelLayerLocation COLD_PIG = layer("cold_pig");

    // CopperAgeBackport
    public static final ModelLayerLocation COPPER_GOLEM = layer("copper_golem");

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(
                ResourceLocation.fromNamespaceAndPath(SpectrumCompat.MODID, "head_" + name),
                "main");
    }
}
