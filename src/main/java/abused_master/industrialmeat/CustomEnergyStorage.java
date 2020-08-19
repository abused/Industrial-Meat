package abused_master.industrialmeat;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage {

    public CustomEnergyStorage(int capacity) {
        super(capacity);
    }

    public CustomEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public void setEnergyStored(int amount) {
        this.energy = amount;

        if(this.energy > capacity) {
            this.energy = capacity;
        }else if(energy < 0) {
            energy = 0;
        }
    }

    public CustomEnergyStorage readFromNBT(CompoundNBT nbt) {

        this.energy = nbt.getInt("Energy");

        if (energy > capacity) {
            energy = capacity;
        }
        return this;
    }

    public CompoundNBT writeToNBT(CompoundNBT nbt) {

        if (energy < 0) {
            energy = 0;
        }
        nbt.putInt("Energy", energy);
        return nbt;
    }
}
