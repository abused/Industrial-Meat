package abused_master.industrialmeat.proxy;

import abused_master.industrialmeat.IndustrialMeat;
import abused_master.industrialmeat.ManualAdditions;
import abused_master.industrialmeat.gui.GuiHandler;
import abused_master.industrialmeat.items.BaseItemFood;
import abused_master.industrialmeat.items.BaseMeatBlock;
import abused_master.industrialmeat.items.MeatPacker;
import abused_master.industrialmeat.tileentity.TileEntityMeatPacker;
import com.buuz135.industrial.api.IndustrialForegoingHelper;
import com.buuz135.industrial.api.recipe.ProteinReactorEntry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class CommonProxy {

    public static ItemFood RawMeatIngot = new BaseItemFood(4, 0.2f, "raw_meat_ingot");
    public static ItemFood MeatIngot = new BaseItemFood(10, 0.8f, "cooked_meat_ingot");
    public static ItemFood RawMeatNugget = new BaseItemFood(1, 0.1f, "raw_meat_nugget");

    public static Block MeatBlock = new BaseMeatBlock("cooked_meat_block");
    public static Block RawMeatBlock = new BaseMeatBlock("raw_meat_block");
    public static Block MeatPacker = new MeatPacker();

    public void preInit(FMLPreInitializationEvent e) {
        GameRegistry.addSmelting(RawMeatIngot, new ItemStack(MeatIngot), 1.0f);
        GameRegistry.addSmelting(RawMeatBlock, new ItemStack(MeatBlock), 1.0f);
    }

    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(IndustrialMeat.instance, new GuiHandler());
        GameRegistry.registerTileEntity(TileEntityMeatPacker.class, "tile_meatpacker");
        ManualAdditions.addToManual();
        IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(MeatIngot)));
        IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(RawMeatIngot)));
        IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(MeatBlock)));
        IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(RawMeatBlock)));
        IndustrialForegoingHelper.addProteinReactorEntry(new ProteinReactorEntry(new ItemStack(RawMeatNugget)));
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(MeatBlock);
        event.getRegistry().register(RawMeatBlock);
        event.getRegistry().register(MeatPacker);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(RawMeatIngot);
        event.getRegistry().register(MeatIngot);
        event.getRegistry().register(RawMeatNugget);

        event.getRegistry().register(new ItemBlock(MeatBlock).setRegistryName(MeatBlock.getRegistryName()));
        event.getRegistry().register(new ItemBlock(RawMeatBlock).setRegistryName(RawMeatBlock.getRegistryName()));
        event.getRegistry().register(new ItemBlock(MeatPacker).setRegistryName(MeatPacker.getRegistryName()));
    }
}