package abused_master.industrialmeat;

import abused_master.industrialmeat.proxy.CommonProxy;
import com.buuz135.industrial.book.BookCategory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = IndustrialMeat.MODID, name = IndustrialMeat.MODNAME, version = IndustrialMeat.VERSION,
acceptedMinecraftVersions = IndustrialMeat.ACCEPTED_VERSIONS, dependencies = IndustrialMeat.DEPS)
public class IndustrialMeat {

    public static final String MODID = "industrialmeat";
    public static final String MODNAME = "Industrial Meat";
    public static final String VERSION = "1.12-1.0.2";
    public static final String ACCEPTED_VERSIONS = "[1.12,1.12.2]";
    public static final String DEPS = "required-after:industrialforegoing";

    @Mod.Instance
    public static IndustrialMeat instance;

    @SidedProxy(clientSide = "abused_master.industrialmeat.proxy.ClientProxy", serverSide = "abused_master.industrialmeat.proxy.CommonProxy")
    static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit(e);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }
}
