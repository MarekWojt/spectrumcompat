package de.mwojt.spectrumcompat.client;

import de.mwojt.spectrumcompat.HeadType;
import de.mwojt.spectrumcompat.SpectrumCompat;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = SpectrumCompat.MODID, value = Dist.CLIENT)
public final class SpectrumCompatClient {
    private SpectrumCompatClient() {}

    /**
     * Maps each {@link HeadType} to the {@link ModelLayerLocation} whose
     * geometry matches that mob's head. Multiple mobs can share a layer when
     * their head geometries (in their source entity models) are identical —
     * e.g. Moa and Cockatrice both use {@code BipedBirdModel}, Blue Swet and
     * Golden Swet share a slime-cube shape, the cloud-wind entities share a
     * cloud cube.
     */
    private static final Map<HeadType, ModelLayerLocation> LAYER_FOR =
            new EnumMap<>(HeadType.class);

    /**
     * Uniform scale factor applied when baking a layer into a {@link ScaledHeadModel}.
     * Layers absent from this map bake into a {@link ConfigurableHeadModel} at 1:1 scale.
     */
    private static final Map<ModelLayerLocation, Float> SCALE_FOR = new HashMap<>();

    static {
        LAYER_FOR.put(HeadType.PHYG, HeadLayers.PHYG);
        LAYER_FOR.put(HeadType.FLYING_COW, HeadLayers.FLYING_COW);
        LAYER_FOR.put(HeadType.SHEEPUFF, HeadLayers.SHEEPUFF);
        LAYER_FOR.put(HeadType.BLUE_MOA, HeadLayers.MOA_LIKE);
        LAYER_FOR.put(HeadType.WHITE_MOA, HeadLayers.MOA_LIKE);
        LAYER_FOR.put(HeadType.BLACK_MOA, HeadLayers.MOA_LIKE);
        LAYER_FOR.put(HeadType.COCKATRICE, HeadLayers.MOA_LIKE);
        LAYER_FOR.put(HeadType.AERBUNNY, HeadLayers.AERBUNNY);
        LAYER_FOR.put(HeadType.AERWHALE, HeadLayers.AERWHALE);
        LAYER_FOR.put(HeadType.BLUE_SWET, HeadLayers.SWET);
        LAYER_FOR.put(HeadType.GOLDEN_SWET, HeadLayers.SWET);
        LAYER_FOR.put(HeadType.AECHOR_PLANT, HeadLayers.AECHOR_PLANT);
        LAYER_FOR.put(HeadType.ZEPHYR, HeadLayers.ZEPHYR);
        LAYER_FOR.put(HeadType.MIMIC, HeadLayers.MIMIC);
        LAYER_FOR.put(HeadType.SENTRY, HeadLayers.SENTRY);
        LAYER_FOR.put(HeadType.VALKYRIE, HeadLayers.HUMANOID_SMALL);
        LAYER_FOR.put(HeadType.VALKYRIE_QUEEN, HeadLayers.HUMANOID_SMALL);
        LAYER_FOR.put(HeadType.SUN_SPIRIT, HeadLayers.SUN_SPIRIT);
        LAYER_FOR.put(HeadType.FIRE_MINION, HeadLayers.SUN_SPIRIT);
        LAYER_FOR.put(HeadType.AERGLOW_FISH, HeadLayers.AERGLOW_FISH);
        LAYER_FOR.put(HeadType.OLD_GREEN_QUAIL, HeadLayers.QUAIL);
        LAYER_FOR.put(HeadType.PINK_QUAIL, HeadLayers.QUAIL);
        LAYER_FOR.put(HeadType.PURPLE_QUAIL, HeadLayers.QUAIL);
        LAYER_FOR.put(HeadType.TROPICAL_BLUE_QUAIL, HeadLayers.QUAIL);
        LAYER_FOR.put(HeadType.FADED_YELLOW_QUAIL, HeadLayers.QUAIL);
        LAYER_FOR.put(HeadType.LIGHT_BLUE_QUAIL, HeadLayers.QUAIL);
        LAYER_FOR.put(HeadType.COPPER_QUAIL, HeadLayers.QUAIL);
        LAYER_FOR.put(HeadType.VENOMITE, HeadLayers.VENOMITE);
        LAYER_FOR.put(HeadType.BABY_ZEPHYR, HeadLayers.BABY_ZEPHYR);
        LAYER_FOR.put(HeadType.EOTS_CONTROLLER, HeadLayers.EOTS_CONTROLLER);
        LAYER_FOR.put(HeadType.HAPPY_GHAST, HeadLayers.HAPPY_GHAST);
        LAYER_FOR.put(HeadType.WARM_CHICKEN, HeadLayers.WARM_CHICKEN);
        LAYER_FOR.put(HeadType.COLD_CHICKEN, HeadLayers.COLD_CHICKEN);
        LAYER_FOR.put(HeadType.WARM_COW, HeadLayers.WARM_COW);
        LAYER_FOR.put(HeadType.COLD_COW, HeadLayers.COLD_COW);
        LAYER_FOR.put(HeadType.WARM_PIG, HeadLayers.WARM_PIG);
        LAYER_FOR.put(HeadType.COLD_PIG, HeadLayers.COLD_PIG);
        LAYER_FOR.put(HeadType.COPPER_GOLEM, HeadLayers.COPPER_GOLEM);
        LAYER_FOR.put(HeadType.EXPOSED_COPPER_GOLEM, HeadLayers.COPPER_GOLEM);
        LAYER_FOR.put(HeadType.WEATHERED_COPPER_GOLEM, HeadLayers.COPPER_GOLEM);
        LAYER_FOR.put(HeadType.OXIDIZED_COPPER_GOLEM, HeadLayers.COPPER_GOLEM);
        LAYER_FOR.put(HeadType.WAXED_COPPER_GOLEM, HeadLayers.COPPER_GOLEM);
        LAYER_FOR.put(HeadType.WAXED_EXPOSED_COPPER_GOLEM, HeadLayers.COPPER_GOLEM);
        LAYER_FOR.put(HeadType.WAXED_WEATHERED_COPPER_GOLEM, HeadLayers.COPPER_GOLEM);
        LAYER_FOR.put(HeadType.WAXED_OXIDIZED_COPPER_GOLEM, HeadLayers.COPPER_GOLEM);

        SCALE_FOR.put(HeadLayers.MOA_LIKE, 2.0F);
        SCALE_FOR.put(HeadLayers.AERWHALE, 4.0F / 7.0F);
        SCALE_FOR.put(HeadLayers.MIMIC, 0.5F);
        SCALE_FOR.put(HeadLayers.AECHOR_PLANT, 0.8F);
        SCALE_FOR.put(HeadLayers.VENOMITE, 0.5F);
        SCALE_FOR.put(HeadLayers.BABY_ZEPHYR, 0.5F);
        SCALE_FOR.put(HeadLayers.EOTS_CONTROLLER, 0.5F);
        SCALE_FOR.put(HeadLayers.HAPPY_GHAST, 0.5F);
        SCALE_FOR.put(HeadLayers.COPPER_GOLEM, 0.5F);
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HeadLayers.PHYG, HeadMeshes::phyg);
        event.registerLayerDefinition(HeadLayers.FLYING_COW, HeadMeshes::flyingCow);
        event.registerLayerDefinition(HeadLayers.SHEEPUFF, HeadMeshes::sheepuff);
        event.registerLayerDefinition(HeadLayers.AERBUNNY, HeadMeshes::aerbunny);
        event.registerLayerDefinition(HeadLayers.MOA_LIKE, HeadMeshes::moaLike);
        event.registerLayerDefinition(HeadLayers.SWET, HeadMeshes::swet);
        event.registerLayerDefinition(HeadLayers.AECHOR_PLANT, HeadMeshes::aechorPlant);
        event.registerLayerDefinition(HeadLayers.ZEPHYR, HeadMeshes::zephyr);
        event.registerLayerDefinition(HeadLayers.MIMIC, HeadMeshes::mimic);
        event.registerLayerDefinition(HeadLayers.SENTRY, HeadMeshes::sentry);
        event.registerLayerDefinition(HeadLayers.HUMANOID_SMALL, HeadMeshes::humanoidSmall);
        event.registerLayerDefinition(HeadLayers.SUN_SPIRIT, HeadMeshes::sunSpirit);
        event.registerLayerDefinition(HeadLayers.AERWHALE, HeadMeshes::aerwhale);
        event.registerLayerDefinition(HeadLayers.QUAIL, HeadMeshes::quail);
        event.registerLayerDefinition(HeadLayers.AERGLOW_FISH, HeadMeshes::aerglowFish);
        event.registerLayerDefinition(HeadLayers.VENOMITE, HeadMeshes::venomite);
        event.registerLayerDefinition(HeadLayers.BABY_ZEPHYR, HeadMeshes::babyZephyr);
        event.registerLayerDefinition(HeadLayers.EOTS_CONTROLLER, HeadMeshes::eotsController);
        event.registerLayerDefinition(HeadLayers.HAPPY_GHAST, HeadMeshes::happyGhast);
        event.registerLayerDefinition(HeadLayers.WARM_CHICKEN, HeadMeshes::warmChicken);
        event.registerLayerDefinition(HeadLayers.COLD_CHICKEN, HeadMeshes::coldChicken);
        event.registerLayerDefinition(HeadLayers.WARM_COW, HeadMeshes::warmCow);
        event.registerLayerDefinition(HeadLayers.COLD_COW, HeadMeshes::coldCow);
        event.registerLayerDefinition(HeadLayers.WARM_PIG, HeadMeshes::warmPig);
        event.registerLayerDefinition(HeadLayers.COLD_PIG, HeadMeshes::coldPig);
        event.registerLayerDefinition(HeadLayers.COPPER_GOLEM, HeadMeshes::copperGolem);
    }

    @SubscribeEvent
    public static void onCreateSkullModels(EntityRenderersEvent.CreateSkullModels event) {
        EntityModelSet modelSet = event.getEntityModelSet();
        Map<ModelLayerLocation, SkullModelBase> cache = new HashMap<>();
        SpectrumCompat.HEAD_ITEMS.keySet().forEach(type -> {
            ModelLayerLocation location = LAYER_FOR.get(type);
            SkullModelBase model = cache.computeIfAbsent(location, loc -> {
                var baked = modelSet.bakeLayer(loc);
                Float scale = SCALE_FOR.get(loc);
                return scale != null
                        ? new ScaledHeadModel(baked, scale, scale, scale)
                        : new ConfigurableHeadModel(baked);
            });
            event.registerSkullModel(type, model);
        });
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> SpectrumCompat.HEAD_ITEMS.keySet()
                .forEach(type -> SkullBlockRenderer.SKIN_BY_TYPE.put(type, type.textureLocation())));
    }
}
