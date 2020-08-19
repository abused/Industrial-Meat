package abused_master.industrialmeat.items;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BaseMeatBlock extends Block {

    public BaseMeatBlock() {
        super(Properties.create(Material.ROCK).hardnessAndResistance(0.5f));
    }
}
