package engineers.workshop.common.loaders;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import static engineers.workshop.common.util.Reference.Info.MODID;

public class CreativeTabLoader {

    public static CreativeTabs tabWorkshop = new CreativeTabs(MODID) {
        @Override
        public Item getTabIconItem() {
            return ItemLoader.itemUpgrade;
        }
    };

    //TODO Implement this - Use GameTicks to increment
   /* public static CreativeTabs tabWorkshop = new CreativeTabs (MODID) {
        double i = 0;
        public ItemStack getIconItemStack() {
            i+=0.01; if (i >= Upgrade.values().length) i=0;
            return ItemUpgrade.getUpgrade(1);
        }
        @Override
        public Item getTabIconItem() {return null;}
    };*/
}
