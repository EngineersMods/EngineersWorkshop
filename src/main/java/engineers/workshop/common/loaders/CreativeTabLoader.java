package engineers.workshop.common.loaders;

import engineers.workshop.common.register.Register;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import static engineers.workshop.common.Reference.Info.MODID;

/**
 * Created by EwyBoy
 */
public class CreativeTabLoader {

    public static CreativeTabs workshop = new CreativeTabs(MODID) {
        @Override
        public ItemStack getTabIconItem() {return new ItemStack(Register.Blocks.table);}
    };

}
