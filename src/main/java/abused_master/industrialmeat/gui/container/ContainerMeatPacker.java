package abused_master.industrialmeat.gui.container;

import abused_master.industrialmeat.registry.ModRegistry;
import abused_master.industrialmeat.tileentity.TileEntityMeatPacker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMeatPacker extends Container {

    public PlayerInventory playerInv;
    public TileEntityMeatPacker meatPacker;

    public ContainerMeatPacker(int windowId, PlayerInventory playerInv, TileEntityMeatPacker meatPacker) {
        super(ModRegistry.meatPackerContainerType.get(), windowId);
        this.playerInv = playerInv;
        this.meatPacker = meatPacker;

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInv, k, 8 + k * 18, 142));
        }
    }

    public TileEntityMeatPacker getMeatPacker() {
        return meatPacker;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        return ItemStack.EMPTY;
    }
}
