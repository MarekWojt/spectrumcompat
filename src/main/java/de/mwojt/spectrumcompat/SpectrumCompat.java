package de.mwojt.spectrumcompat;

import com.mojang.logging.LogUtils;
import de.mwojt.spectrumcompat.mixin.BlockEntityTypeAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mod(SpectrumCompat.MODID)
public class SpectrumCompat {
    public static final String MODID = "spectrumcompat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // These maps are populated by register(...) in the constructor. Because the
    // constructor routes registration through HeadType.activeTypes(), the maps
    // contain exactly the active types — and because they're EnumMaps, iteration
    // order matches enum declaration order. Post-registration code paths (creative
    // tab, BlockEntityType.SKULL.validBlocks swap, mixin lookup, client model +
    // texture wiring) therefore iterate these maps directly rather than re-filtering
    // through activeTypes().
    public static final Map<HeadType, DeferredBlock<HeadBlock>> FLOOR_BLOCKS = new EnumMap<>(HeadType.class);
    public static final Map<HeadType, DeferredBlock<WallHeadBlock>> WALL_BLOCKS = new EnumMap<>(HeadType.class);
    public static final Map<HeadType, DeferredItem<Item>> HEAD_ITEMS = new EnumMap<>(HeadType.class);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HEADS_TAB = TABS.register("heads",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.spectrumcompat.heads"))
                    .icon(() -> HEAD_ITEMS.values().stream().findFirst()
                            .map(i -> i.get().getDefaultInstance())
                            .orElse(ItemStack.EMPTY))
                    .displayItems((parameters, output) ->
                            HEAD_ITEMS.values().forEach(i -> output.accept(i.get())))
                    .build());

    public SpectrumCompat(IEventBus modEventBus, ModContainer modContainer) {
        // ModList is not reliably available during class init, so registration must run
        // here — not in a static block — so activeTypes() can filter on mod presence.
        HeadType.activeTypes().forEach(SpectrumCompat::register);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        TABS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
    }

    private static BlockBehaviour.Properties baseProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOL)
                .strength(1.0F)
                .sound(SoundType.WOOL);
    }

    private static void register(HeadType type) {
        DeferredBlock<HeadBlock> floor = BLOCKS.registerBlock(
                type.headBlockName(),
                props -> new HeadBlock(type, props),
                baseProps());
        // We cannot call floor.get() here (the block is not bound until the registry
        // event fires). Drop parity between wall and floor is instead handled by the
        // wall-variant loot tables under data/spectrumcompat/loot_table/blocks/.
        DeferredBlock<WallHeadBlock> wall = BLOCKS.registerBlock(
                type.wallHeadBlockName(),
                props -> new WallHeadBlock(type, props),
                baseProps());

        // Single item for both floor and wall variants, like every vanilla mob head.
        DeferredItem<Item> headItem = ITEMS.register(type.headBlockName(),
                () -> new StandingAndWallBlockItem(floor.get(), wall.get(), new Item.Properties(), Direction.DOWN));

        FLOOR_BLOCKS.put(type, floor);
        WALL_BLOCKS.put(type, wall);
        HEAD_ITEMS.put(type, headItem);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(SpectrumCompat::registerHeadsAsSkullBlockEntities);
    }

    /**
     * Vanilla {@link BlockEntityType#SKULL} only accepts its hardcoded block list, so
     * {@code level.getBlockEntity(pos)} would return null for ours. We swap
     * {@code validBlocks} for a mutable copy that also contains every registered head.
     */
    private static void registerHeadsAsSkullBlockEntities() {
        BlockEntityTypeAccessor accessor =
                (BlockEntityTypeAccessor) (Object) BlockEntityType.SKULL;
        Set<Block> validBlocks = new HashSet<>(accessor.spectrumcompat$getValidBlocks());
        FLOOR_BLOCKS.forEach((type, floor) -> {
            validBlocks.add(floor.get());
            validBlocks.add(WALL_BLOCKS.get(type).get());
        });
        accessor.spectrumcompat$setValidBlocks(Set.copyOf(validBlocks));
    }
}
