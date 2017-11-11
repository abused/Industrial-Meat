package abused_master.industrialmeat.items;

import com.buuz135.industrial.IndustrialForegoing;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BaseMeatBlock extends Block {

    public BaseMeatBlock(String name) {
        super(Material.ROCK);
        this.setUnlocalizedName(name);
        this.setRegistryName(name);
        this.setCreativeTab(IndustrialForegoing.creativeTab);
        this.setHardness(0.5f);
    }
}
