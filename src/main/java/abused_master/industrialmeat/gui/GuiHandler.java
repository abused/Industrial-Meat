package abused_master.industrialmeat.gui;

import abused_master.industrialmeat.gui.container.ContainerMeatPacker;
import abused_master.industrialmeat.tileentity.TileEntityMeatPacker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    public static int GUI_MEATPACKER = 0;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);

        if(ID == 0) {
            if(te instanceof TileEntityMeatPacker) {
                return new ContainerMeatPacker(player.inventory);
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);

        if(ID == 0) {
            if(te instanceof TileEntityMeatPacker) {
                TileEntityMeatPacker tileEntityMeatPacker = (TileEntityMeatPacker) te;
                return new GuiMeatPacker(new ContainerMeatPacker(player.inventory), tileEntityMeatPacker);
            }
        }
        return null;
    }
}
