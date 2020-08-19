package abused_master.industrialmeat.tileentity;

import abused_master.industrialmeat.CustomEnergyStorage;
import abused_master.industrialmeat.gui.container.ContainerMeatPacker;
import abused_master.industrialmeat.registry.ModRegistry;
import com.buuz135.industrial.module.ModuleCore;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityMeatPacker extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public CustomEnergyStorage storage = new CustomEnergyStorage(50000);
    public FluidTank tank = new FluidTank(10000);
    public boolean nuggetForm = false;
    public int workTime;
    public int totalWorkTime;

    public final LazyOptional<?> capabilityEnergy = LazyOptional.of(() -> storage);
    public final LazyOptional<?> capabilityTank = LazyOptional.of(() -> tank);

    public TileEntityMeatPacker() {
        super(ModRegistry.meatPackerTileType.get());
    }

    //read
    @Override
    public void func_230337_a_(BlockState state, CompoundNBT compound) {
        super.func_230337_a_(state, compound);
        this.workTime = compound.getInt("WorkTime");
        this.totalWorkTime = compound.getInt("TotalWorkTime");
        this.storage.readFromNBT(compound);

        if (compound.contains("FluidData")) {
            this.tank.setFluid(FluidStack.loadFluidStackFromNBT(compound.getCompound("FluidData")));
        }

        if(tank != null && tank.getFluid() != null) {
            tank.readFromNBT(compound);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (this.tank != null && this.tank.getFluid() != null) {
            CompoundNBT tankTag = new CompoundNBT();
            this.tank.getFluid().writeToNBT(tankTag);
            compound.put("FluidData", tankTag);
            tank.writeToNBT(compound);
        }
        compound.putInt("WorkTime", (short) this.workTime);
        compound.putInt("TotalWorkTime", this.totalWorkTime);
        storage.writeToNBT(compound);
        return compound;
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            BlockState state = world.getBlockState(pos);
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
                        tank.drain(50, IFluidHandler.FluidAction.EXECUTE);
                    }else {
                        storage.extractEnergy(600, false);
                        tank.drain(200, IFluidHandler.FluidAction.EXECUTE);
                    }

                    ItemStack stack = new ItemStack(ModRegistry.rawMeatIngotType.get());
                    ItemStack rawStack = new ItemStack(ModRegistry.rawMeatNuggetType.get());
                    for (Direction side : Direction.values()) {
                        BlockPos cip = pos.offset(side);
                        TileEntity ite = world.getTileEntity(cip);
                        if (ite instanceof IInventory) {
                            if (nuggetForm) {
                                stack = HopperTileEntity.putStackInInventoryAllSlots(null, (IInventory) ite, rawStack, side.getOpposite());
                            }else {
                                stack = HopperTileEntity.putStackInInventoryAllSlots(null, (IInventory) ite, stack, side.getOpposite());
                            }
                        }
                        if (stack.isEmpty() || rawStack.isEmpty()) {
                            break;
                        }
                    }
                    if (!stack.isEmpty()) {
                        if(nuggetForm) {
                            world.addEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), rawStack));
                        }else {
                            world.addEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), stack));
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
                if(tank.getFluid().getFluid() == ModuleCore.MEAT.getSourceFluid()) {
                    nuggetForm = false;
                    return true;
                }
            }
        } else if (tank.getFluidAmount() < 200 && tank.getFluidAmount() >= 50) {
            if (storage.getEnergyStored() >= 300) {
                if(tank.getFluid().getFluid() == ModuleCore.MEAT.getSourceFluid()) {
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
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(getBlockState(), pkt.getNbtCompound());
        this.world.notifyBlockUpdate(this.getPos(), world.getBlockState(this.getPos()), world.getBlockState(this.getPos()), 3);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.func_230337_a_(state, tag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (LazyOptional<T>) this.capabilityTank;
        }

        if(capability == CapabilityEnergy.ENERGY) {
            return (LazyOptional<T>) this.capabilityEnergy;
        }

        return null;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Meat Packer");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new ContainerMeatPacker(id, playerInventory, this);
    }
}
