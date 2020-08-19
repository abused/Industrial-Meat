package abused_master.industrialmeat.items;

import abused_master.industrialmeat.tileentity.TileEntityMeatPacker;
import com.buuz135.industrial.module.ModuleCore;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

public class MeatPacker extends ContainerBlock {

    public MeatPacker() {
        super(Properties.create(Material.ROCK).hardnessAndResistance(1.2f));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getHeldItem(hand);
        final TileEntityMeatPacker tank = (TileEntityMeatPacker) world.getTileEntity(pos);

        if(heldItem != FluidUtil.getFilledBucket(new FluidStack(ModuleCore.MEAT.getSourceFluid(), 1000))) {
            ActionResultType actionResultType = tank.getCapability(FLUID_HANDLER_CAPABILITY, hit.getFace()).map(fluidHandler -> {
                FluidActionResult res = this.interactWithFluidHandler(heldItem, fluidHandler, player);
                if (res.isSuccess()) {
                    player.setHeldItem(hand, res.getResult());
                    return ActionResultType.SUCCESS;
                }

                return ActionResultType.FAIL;
            }).orElse(ActionResultType.FAIL);

            if(actionResultType.isSuccess()) {
                return actionResultType;
            }
        }

        if(!world.isRemote) {
            if (heldItem != FluidUtil.getFilledBucket(new FluidStack(ModuleCore.MEAT.getSourceFluid(), 1000))) {
                NetworkHooks.openGui((ServerPlayerEntity) player, tank, pos);
                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public static FluidActionResult interactWithFluidHandler(@Nonnull ItemStack stack, IFluidHandler fluidHandler, PlayerEntity player) {
        if (stack.isEmpty() || fluidHandler == null || player == null) {
            return FluidActionResult.FAILURE;
        }

        IItemHandler playerInventory = new InvWrapper(player.inventory);

        FluidActionResult fillResult = FluidUtil.tryFillContainerAndStow(stack, fluidHandler, playerInventory, Integer.MAX_VALUE, player, true);
        if (fillResult.isSuccess()) {
            return fillResult;
        } else {
            return FluidUtil.tryEmptyContainerAndStow(stack, fluidHandler, playerInventory, Integer.MAX_VALUE, player, true);
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityMeatPacker();
    }
}
