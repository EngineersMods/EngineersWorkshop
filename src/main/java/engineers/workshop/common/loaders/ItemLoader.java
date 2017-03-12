package engineers.workshop.common.loaders;

import engineers.workshop.common.items.ItemUpgrade;
import net.minecraft.item.Item;

public class ItemLoader {

    public static Item itemUpgrade;

    public static void registerItems() {
        itemUpgrade = new ItemUpgrade();
    }
}
