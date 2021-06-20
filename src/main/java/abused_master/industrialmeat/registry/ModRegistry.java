package abused_master.industrialmeat.registry;

import abused_master.industrialmeat.IndustrialMeat;
import abused_master.industrialmeat.gui.container.ContainerMeatPacker;
import abused_master.industrialmeat.items.BaseItemFood;
import abused_master.industrialmeat.items.BaseMeatBlock;
import abused_master.industrialmeat.items.MeatPacker;
import abused_master.industrialmeat.tileentity.TileEntityMeatPacker;
import com.buuz135.industrial.api.recipe.ProteinReactorEntry;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRegistry {

    public static final DeferredRegister<Block> blocksRegistry = DeferredRegister.create(ForgeRegistries.BLOCKS, IndustrialMeat.MODID);
    public static final DeferredRegister<Item> itemsRegistry = DeferredRegister.create(ForgeRegistries.ITEMS, IndustrialMeat.MODID);
    public static final DeferredRegister<ContainerType<?>> containersRegistry = DeferredRegister.create(ForgeRegistries.CONTAINERS, IndustrialMeat.MODID);
    public static final DeferredRegister<TileEntityType<?>> tilesRegistry = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, IndustrialMeat.MODID);

    public static final Food rawMeatFood = new Food.Builder().hunger(4).saturation(0.2f).build();
    public static final Food cookedMeatFood = new Food.Builder().hunger(10).saturation(0.8f).build();
    public static final Food rawMeatNuggetFood = new Food.Builder().hunger(1).saturation(0.1f).build();

    public static RegistryObject<Item> rawMeatIngotType = itemsRegistry.register("raw_meat_ingot", () -> new BaseItemFood(rawMeatFood));
    public static RegistryObject<Item> meatIngotType = itemsRegistry.register("cooked_meat_ingot", () -> new BaseItemFood(cookedMeatFood));
    public static RegistryObject<Item> rawMeatNuggetType = itemsRegistry.register("raw_meat_nugget", () -> new BaseItemFood(rawMeatNuggetFood));

    public static RegistryObject<Block> meatBlockType = blocksRegistry.register("cooked_meat_block", () -> new BaseMeatBlock());
    public static RegistryObject<Item> meatBlockItemType = itemsRegistry.register("cooked_meat_block", () -> new BlockItem(meatBlockType.get(), new Item.Properties().group(IndustrialMeat.modItemGroup)));

    public static RegistryObject<Block> rawMeatBlockType = blocksRegistry.register("raw_meat_block", () -> new BaseMeatBlock());
    public static RegistryObject<Item> rawMeatBlockItemType = itemsRegistry.register("raw_meat_block", () -> new BlockItem(rawMeatBlockType.get(), new Item.Properties().group(IndustrialMeat.modItemGroup)));

    public static RegistryObject<Block> meatPackerType = blocksRegistry.register("meat_packer", () -> new MeatPacker());
    public static RegistryObject<Item> meatPackerItemType = itemsRegistry.register("meat_packer", () -> new BlockItem(meatPackerType.get(), new Item.Properties().group(IndustrialMeat.modItemGroup)));

    public static RegistryObject<TileEntityType<TileEntityMeatPacker>> meatPackerTileType = tilesRegistry.register("meat_packer_tile", () -> TileEntityType.Builder.create(TileEntityMeatPacker::new, meatPackerType.get()).build(null));
    public static RegistryObject<ContainerType<ContainerMeatPacker>> meatPackerContainerType = containersRegistry.register("meat_packer_container", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntityMeatPacker tileEntityMeatPacker = (TileEntityMeatPacker) inv.player.world.getTileEntity(pos);
        return new ContainerMeatPacker(windowId, inv, tileEntityMeatPacker);
    }));

    public static void register() {
        //ManualAdditions.addToManual();
        ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES.add(new ProteinReactorEntry(new ItemStack(meatIngotType.get())));
        ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES.add(new ProteinReactorEntry(new ItemStack(rawMeatIngotType.get())));
        ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES.add(new ProteinReactorEntry(new ItemStack(meatBlockType.get())));
        ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES.add(new ProteinReactorEntry(new ItemStack(rawMeatBlockType.get())));
        ProteinReactorEntry.PROTEIN_REACTOR_ENTRIES.add(new ProteinReactorEntry(new ItemStack(rawMeatNuggetType.get())));
    }
}
