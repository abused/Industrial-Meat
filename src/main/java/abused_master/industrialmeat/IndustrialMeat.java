package abused_master.industrialmeat;

import abused_master.industrialmeat.gui.GuiMeatPacker;
import abused_master.industrialmeat.registry.ModRegistry;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(IndustrialMeat.MODID)
public class IndustrialMeat {

    public static final String MODID = "industrialmeat";

    public static ItemGroup modItemGroup = new ItemGroup(MODID) {
        @Override
        public ItemStack createIcon() {
            return ModRegistry.meatPackerItemType.get().getDefaultInstance();
        }
    };

    public IndustrialMeat() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initClient);
        ModRegistry.blocksRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModRegistry.itemsRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModRegistry.tilesRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModRegistry.containersRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public void init(FMLCommonSetupEvent event) {
        ModRegistry.register();
    }

    public void initClient(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModRegistry.meatPackerContainerType.get(), GuiMeatPacker::new);
    }
}
