package engineers.workshop.common.loaders;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import static engineers.workshop.common.util.Reference.Info.MODID;

public class CreativeTabLoader {

   public static CreativeTabs tabWorkshop = new CreativeTabs (MODID) {
        public ItemStack getIconItemStack() {
            return new ItemStack(BlockLoader.blockTable);
        }
        @Override
        public Item getTabIconItem() {return null;}
    };
}
