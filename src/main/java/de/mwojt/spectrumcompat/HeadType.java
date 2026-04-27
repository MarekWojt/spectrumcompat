package de.mwojt.spectrumcompat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * One enum value per mob (or mob variant) whose head we contribute. Each value
 * doubles as the SkullBlock.Type (consumed by vanilla SkullBlock + SkullBlockRenderer)
 * and as the source-of-truth for the matching entity ResourceLocation used by our
 * loot modifier, recipes and Spectrum interop mixin.
 *
 * <p>Fields:
 * <ul>
 *   <li>{@code requiredModId} — mod that must be loaded for this type to register.
 *       For namespaced entities (Aether, Deep Aether) it equals the entity namespace
 *       and the 3-arg constructor defaults it. VB / CAB register entities under
 *       {@code minecraft:}, so they use an explicit form.</li>
 *   <li>{@code entityId} — the mob's entity type ResourceLocation. Multiple variants
 *       (e.g. blue/white/black Moa) share the same entity type.</li>
 *   <li>{@code textureLocation} — per-variant mob texture.</li>
 *   <li>{@code blockName} — the head block's registry path. Defaults to the entity
 *       path for non-variant mobs; for variants the 5-arg constructor sets it
 *       explicitly so e.g. {@code aether:moa}-derived heads register as
 *       {@code spectrumcompat:blue_moa_head} / {@code spectrumcompat:white_moa_head}.</li>
 * </ul>
 */
public enum HeadType implements SkullBlock.Type {
    // aether — single-variant mobs use the 3-arg constructor.
    PHYG("aether", "phyg", "textures/entity/mobs/phyg/phyg.png"),
    FLYING_COW("aether", "flying_cow", "textures/entity/mobs/flying_cow/flying_cow.png"),
    SHEEPUFF("aether", "sheepuff", "textures/entity/mobs/sheepuff/sheepuff.png"),
    AERBUNNY("aether", "aerbunny", "textures/entity/mobs/aerbunny/aerbunny.png"),
    AERWHALE("aether", "aerwhale", "textures/entity/mobs/aerwhale/aerwhale.png"),
    BLUE_SWET("aether", "blue_swet", "textures/entity/mobs/swet/swet_blue.png"),
    GOLDEN_SWET("aether", "golden_swet", "textures/entity/mobs/swet/swet_golden.png"),
    AECHOR_PLANT("aether", "aechor_plant", "textures/entity/mobs/aechor_plant/aechor_plant.png"),
    COCKATRICE("aether", "cockatrice", "textures/entity/mobs/cockatrice/cockatrice.png"),
    ZEPHYR("aether", "zephyr", "textures/entity/mobs/zephyr/zephyr.png"),
    MIMIC("aether", "mimic", "textures/entity/mobs/mimic/normal.png"),
    SENTRY("aether", "sentry", "textures/entity/mobs/sentry/sentry.png"),
    VALKYRIE("aether", "valkyrie", "textures/entity/mobs/valkyrie/valkyrie.png"),
    VALKYRIE_QUEEN("aether", "valkyrie_queen", "textures/entity/mobs/valkyrie_queen/valkyrie_queen.png"),
    // Fire Minion shares its texture with Sun Spirit in Aether's FireMinionRenderer.
    FIRE_MINION("aether", "fire_minion", "textures/entity/mobs/sun_spirit/sun_spirit.png"),
    SUN_SPIRIT("aether", "sun_spirit", "textures/entity/mobs/sun_spirit/sun_spirit.png"),
    // Moa variants — MoaType is a data-driven registry (aether:moa_types). We cover
    // the three base types shipped by Aether 1.21.1 under data/aether/aether/moa_type/.
    // Each variant shares entity aether:moa but gets its own block name and texture;
    // loot_modifier entries filter on the entity's {@code "MoaType"} NBT key
    // (ResourceLocation string). "Black" uses raptor.png — Aether's AetherMoaTypes
    // registry points its black entry at raptor.png (the "raptor moa" is the black
    // Moa variant despite the historical filename).
    BLUE_MOA("aether", "aether", "moa", "blue_moa", "textures/entity/mobs/moa/blue_moa.png"),
    WHITE_MOA("aether", "aether", "moa", "white_moa", "textures/entity/mobs/moa/white_moa.png"),
    BLACK_MOA("aether", "aether", "moa", "black_moa", "textures/entity/mobs/moa/raptor.png"),

    // deep aether
    AERGLOW_FISH("deep_aether", "aerglow_fish", "textures/entity/aerglow_fish.png"),
    VENOMITE("deep_aether", "venomite", "textures/entity/venomite/venomite.png"),
    BABY_ZEPHYR("deep_aether", "baby_zephyr", "textures/entity/baby_zephyr.png"),
    // EOTSController's own EOTSModel is an invisible 16³ cube; the visible head is
    // the "controlling segment" rendered with eots_segment_controlling.png.
    EOTS_CONTROLLER("deep_aether", "eots_controller", "textures/entity/eots/eots_segment_controlling.png"),
    // Quail variants — QuailVariants enum (7 colors, 0..6). Loot-modifier entries
    // filter on the entity's {@code "Variant"} NBT key (int ordinal).
    OLD_GREEN_QUAIL("deep_aether", "deep_aether", "quail", "old_green_quail", "textures/entity/quail/quail_old_green.png"),
    PINK_QUAIL("deep_aether", "deep_aether", "quail", "pink_quail", "textures/entity/quail/quail_pink.png"),
    PURPLE_QUAIL("deep_aether", "deep_aether", "quail", "purple_quail", "textures/entity/quail/quail_purple.png"),
    TROPICAL_BLUE_QUAIL("deep_aether", "deep_aether", "quail", "tropical_blue_quail", "textures/entity/quail/quail_tropical_blue.png"),
    FADED_YELLOW_QUAIL("deep_aether", "deep_aether", "quail", "faded_yellow_quail", "textures/entity/quail/quail_faded_yellow.png"),
    LIGHT_BLUE_QUAIL("deep_aether", "deep_aether", "quail", "light_blue_quail", "textures/entity/quail/quail_light_blue.png"),
    COPPER_QUAIL("deep_aether", "deep_aether", "quail", "copper_quail", "textures/entity/quail/quail_copper.png"),

    // vanillabackport — entity is registered under minecraft: namespace, so the
    // required-mod override is mandatory; otherwise activeTypes() would always
    // pass for these entries since "minecraft" is always loaded.
    HAPPY_GHAST("vanillabackport", "minecraft", "happy_ghast", "textures/entity/ghast/happy_ghast.png"),
    // Chicken / Cow / Pig biome variants (1.21.2 backport). Only warm + cold
    // registered here; the "temperate" default is Spectrum's own chicken_head /
    // cow_head / pig_head (which we don't duplicate). A Java loot modifier
    // (VariantHeadDedupeLootModifier) strips Spectrum's generic head from the
    // loot when the killed entity's "variant" NBT is anything other than
    // minecraft:temperate — so warm/cold kills drop only our variant head, and
    // temperate kills drop only Spectrum's generic head.
    WARM_CHICKEN("vanillabackport", "minecraft", "chicken", "warm_chicken", "textures/entity/chicken/warm_chicken.png"),
    COLD_CHICKEN("vanillabackport", "minecraft", "chicken", "cold_chicken", "textures/entity/chicken/cold_chicken.png"),
    WARM_COW("vanillabackport", "minecraft", "cow", "warm_cow", "textures/entity/cow/warm_cow.png"),
    COLD_COW("vanillabackport", "minecraft", "cow", "cold_cow", "textures/entity/cow/cold_cow.png"),
    WARM_PIG("vanillabackport", "minecraft", "pig", "warm_pig", "textures/entity/pig/warm_pig.png"),
    COLD_PIG("vanillabackport", "minecraft", "pig", "cold_pig", "textures/entity/pig/cold_pig.png"),

    // copperagebackport — all 4 oxidation states + 4 waxed equivalents. The unwaxed
    // four drop from killed entities (filtered by {@code "weather_state"} NBT int
    // ordinal 0..3). The waxed four never drop — CAB's wax mechanic transforms the
    // golem into a permanent statue block, so no killable waxed entity exists. They
    // are instead obtainable via crafting-table recipes (unwaxed + honeycomb →
    // waxed) mirroring vanilla's copper-waxing pattern. All eight share the same
    // four upstream textures; waxed vs unwaxed is a block-item-only distinction.
    COPPER_GOLEM("copperagebackport", "minecraft", "copper_golem", "copper_golem", "textures/entity/copper_golem/copper_golem.png"),
    EXPOSED_COPPER_GOLEM("copperagebackport", "minecraft", "copper_golem", "exposed_copper_golem", "textures/entity/copper_golem/exposed_copper_golem.png"),
    WEATHERED_COPPER_GOLEM("copperagebackport", "minecraft", "copper_golem", "weathered_copper_golem", "textures/entity/copper_golem/weathered_copper_golem.png"),
    OXIDIZED_COPPER_GOLEM("copperagebackport", "minecraft", "copper_golem", "oxidized_copper_golem", "textures/entity/copper_golem/oxidized_copper_golem.png"),
    WAXED_COPPER_GOLEM("copperagebackport", "minecraft", "copper_golem", "waxed_copper_golem", "textures/entity/copper_golem/copper_golem.png"),
    WAXED_EXPOSED_COPPER_GOLEM("copperagebackport", "minecraft", "copper_golem", "waxed_exposed_copper_golem", "textures/entity/copper_golem/exposed_copper_golem.png"),
    WAXED_WEATHERED_COPPER_GOLEM("copperagebackport", "minecraft", "copper_golem", "waxed_weathered_copper_golem", "textures/entity/copper_golem/weathered_copper_golem.png"),
    WAXED_OXIDIZED_COPPER_GOLEM("copperagebackport", "minecraft", "copper_golem", "waxed_oxidized_copper_golem", "textures/entity/copper_golem/oxidized_copper_golem.png");

    private final String requiredModId;
    private final ResourceLocation entityId;
    private final ResourceLocation textureLocation;
    private final String headBlockName;

    // Primary constructor: all four fields explicit. Used for variant entries
    // where the block name must differ from the entity path (e.g. BLUE_MOA's
    // entity is aether:moa but its block is spectrumcompat:blue_moa_head).
    HeadType(String requiredModId, String entityNamespace, String entityPath,
             String blockName, String texturePath) {
        this.requiredModId = requiredModId;
        this.entityId = ResourceLocation.fromNamespaceAndPath(entityNamespace, entityPath);
        this.textureLocation = ResourceLocation.fromNamespaceAndPath(entityNamespace, texturePath);
        this.headBlockName = blockName + "_head";
    }

    // Cross-namespace (minecraft:) single-variant entries — entity is under
    // minecraft: but block name equals entity path (VB Happy Ghast pattern).
    HeadType(String requiredModId, String entityNamespace, String entityPath, String texturePath) {
        this(requiredModId, entityNamespace, entityPath, entityPath, texturePath);
    }

    // Aether/DA single-variant entries — namespace serves as both the entity
    // namespace and the required mod id.
    HeadType(String namespace, String path, String texturePath) {
        this(namespace, namespace, path, path, texturePath);
    }

    public @NotNull ResourceLocation textureLocation() {
        return textureLocation;
    }

    public @NotNull ResourceLocation entityId() {
        return entityId;
    }

    public @NotNull String headBlockName() {
        return headBlockName;
    }

    public @NotNull String wallHeadBlockName() {
        return headBlockName + "_wall";
    }

    @Override
    public @NotNull String getSerializedName() {
        return SpectrumCompat.MODID + "_" + headBlockName;
    }

    /**
     * Gate used by the {@code SpectrumCompat} registration loop to decide which types
     * actually get registered as blocks/items. A type is active iff its
     * {@link #requiredModId} is loaded. Post-registration code paths iterate the
     * registered-entry maps on {@code SpectrumCompat} ({@code HEAD_ITEMS},
     * {@code FLOOR_BLOCKS}, {@code WALL_BLOCKS}) instead, which are guaranteed by
     * construction to contain exactly the active types. Not safe to call during
     * class init of {@code SpectrumCompat}; {@code ModList} isn't reliably available
     * then.
     */
    public static Stream<HeadType> activeTypes() {
        ModList modList = ModList.get();
        return Arrays.stream(values())
                .filter(t -> modList.isLoaded(t.requiredModId));
    }
}
