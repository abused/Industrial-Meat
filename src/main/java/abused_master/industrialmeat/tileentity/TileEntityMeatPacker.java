package abused_master.industrialmeat.tileentity;

import abused_master.industrialmeat.CustomEnergyStorage;
import abused_master.industrialmeat.proxy.CommonProxy;
import com.buuz135.industrial.proxy.FluidsRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class TileEntityMeatPacker extends TileEntity implements ITickable {

    public CustomEnergyStorage storage = new CustomEnergyStorage(50000);
    public FluidTank tank = new FluidTank(10000);
    public boolean nuggetForm = false;
    public int workTime;
    public int totalWorkTime;

    public TileEntityMeatPacker() {
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.workTime = nbt.getInteger("WorkTime");
        this.totalWorkTime = nbt.getInteger("TotalWorkTime");
        this.storage.readFromNBT(nbt);

        if (nbt.hasKey("FluidData")) {
            this.tank.setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("FluidData")));
        }

        if(tank != null && tank.getFluid() != null) {
            tank.readFromNBT(nbt);
        }

        if (this.tank != null) {
            this.tank.setTileEntity(this);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.tank != null && this.tank.getFluid() != null) {
            final NBTTagCompound tankTag = new NBTTagCompound();
            this.tank.getFluid().writeToNBT(tankTag);
            nbt.setTag("FluidData", tankTag);
            tank.writeToNBT(nbt);
        }
        nbt.setInteger("WorkTime", (short) this.workTime);
        nbt.setInteger("TotalWorkTime", this.totalWorkTime);
        storage.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
        this.totalWorkTime = getWorkTime();

        if (!world.isRemote) {
            if (canCreateMeat()) {
                workTime++;
                if (workTime == totalWorkTime) {
                    workTime = 0;

                    if(nuggetForm) {
                        storage.extractEnergy(250, false);
                        tank.drain(50, true);
                    }else {
                        storage.extractEnergy(600, false);
                        tank.drain(200, true);
                    }

                    ItemStack stack = new ItemStack(CommonProxy.RawMeatIngot);
                    ItemStack rawStack = new ItemStack(CommonProxy.RawMeatNugget);
                    for (EnumFacing side : EnumFacing.VALUES) {
                        BlockPos cip = pos.offset(side);
                        TileEntity ite = world.getTileEntity(cip);
                        if (ite instanceof IInventory) {
                            if (nuggetForm) {
                                stack = TileEntityHopper.putStackInInventoryAllSlots(null, (IInventory) ite, rawStack, side.getOpposite());
                            }else {
                                stack = TileEntityHopper.putStackInInventoryAllSlots(null, (IInventory) ite, stack, side.getOpposite());
                            }
                        }
                        if (stack.isEmpty() || rawStack.isEmpty()) {
                            break;
                        }
                    }
                    if (!stack.isEmpty()) {
                        if(nuggetForm) {
                            world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY() + 1, pos.getZ(), rawStack));
                        }else {
                            world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY() + 1, pos.getZ(), stack));
                        }
                    }

                }
            }else {
                if(workTime > 0) {
                    workTime = 0;
                }
            }
        }
    }

    public boolean canCreateMeat() {
        if (tank.getFluidAmount() >= 200) {
            if (storage.getEnergyStored() >= 500) {
                if(tank.getFluid().getFluid() == FluidsRegistry.MEAT) {
                    nuggetForm = false;
                    return true;
                }
            }
        } else if (tank.getFluidAmount() < 200 && tank.getFluidAmount() >= 50) {
            if (storage.getEnergyStored() >= 300) {
                if(tank.getFluid().getFluid() == FluidsRegistry.MEAT) {
                    nuggetForm = true;
                    return true;
                }
            }
        }
        return false;
    }

    public int getWorkTime() {
        return nuggetForm ? 50 : 90;
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        this.readFromNBT(tag);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return true;
        }
        if(capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) this.tank;
        }
        if(capability == CapabilityEnergy.ENERGY) {
            return (T) this.storage;
        }
        return super.getCapability(capability, facing);
    }
}
