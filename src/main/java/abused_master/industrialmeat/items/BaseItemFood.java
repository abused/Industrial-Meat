package abused_master.industrialmeat.items;

import abused_master.industrialmeat.IndustrialMeat;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class BaseItemFood extends Item {

    public BaseItemFood(Food food) {
        super(new Item.Properties().food(food).group(IndustrialMeat.modItemGroup));
    }
}
