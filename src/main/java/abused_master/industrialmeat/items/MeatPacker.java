package abused_master.industrialmeat.items;

import abused_master.industrialmeat.IndustrialMeat;
import abused_master.industrialmeat.gui.GuiHandler;
import abused_master.industrialmeat.tileentity.TileEntityMeatPacker;
import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.registry.IFRegistries;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

public class MeatPacker extends BlockContainer {

    public MeatPacker() {
        super(Material.ROCK);
        this.setCreativeTab(IndustrialForegoing.creativeTab);
        this.setRegistryName("meat_packer");
        this.setUnlocalizedName("meat_packer");
        this.setHarvestLevel("pickaxe", 1);
        this.setHardness(1.2f);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        final TileEntityMeatPacker tank = (TileEntityMeatPacker) world.getTileEntity(pos);


        if(heldItem != FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.MEAT, 1000))) {
            IFluidHandler handler = tank.getCapability(FLUID_HANDLER_CAPABILITY, facing);
            FluidActionResult res = this.interactWithFluidHandler(heldItem, handler, player);
            if (res.isSuccess()) {
                player.setHeldItem(hand, res.getResult());
                return true;
            }
        }

        /**
        if (heldItem.equals(FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.MEAT, 1000)))) {
            if(tank.tank != null && tank.tank.getFluidAmount() <=9000) {
                tank.tank.fill(new FluidStack(FluidsRegistry.MEAT, 1000), true);
                player.setHeldItem(hand, new ItemStack(Items.BUCKET));
                System.out.println("held item is meat bucket");
                return true;
            }
        }
         */

        if(!world.isRemote) {
            if (heldItem != FluidUtil.getFilledBucket(new FluidStack(FluidsRegistry.MEAT, 1000))) {
                player.openGui(IndustrialMeat.instance, GuiHandler.GUI_MEATPACKER, world, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityMeatPacker();
    }

    public static FluidActionResult interactWithFluidHandler(@Nonnull ItemStack stack, IFluidHandler fluidHandler, EntityPlayer player)
    {
        if (stack.isEmpty() || fluidHandler == null || player == null)
        {
            return FluidActionResult.FAILURE;
        }

        IItemHandler playerInventory = new InvWrapper(player.inventory);

        FluidActionResult fillResult = FluidUtil.tryFillContainerAndStow(stack, fluidHandler, playerInventory, Integer.MAX_VALUE, player);
        if (fillResult.isSuccess())
        {
            return fillResult;
        }
        else
        {
            return FluidUtil.tryEmptyContainerAndStow(stack, fluidHandler, playerInventory, Integer.MAX_VALUE, player);
        }
    }
}
