package engineers.workshop.common.items;

import engineers.workshop.common.loaders.CreativeTabLoader;
import engineers.workshop.common.loaders.ItemLoader;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;

import static engineers.workshop.common.util.Reference.Info.MODID;

public class ItemMachine extends Item {

	public ItemMachine() {
		setCreativeTab(CreativeTabLoader.tabWorkshop);
		setHasSubtypes(true);
		setRegistryName(MODID + ":" + "machine");
		GameRegistry.register(this);
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {
		Machine machine = getMachine(item);
		return MODID + ":" + "machine" + "." + (machine != null ? machine.getName() : "unknown");
	}

	public static Machine getMachine(int dmg) {
		return dmg >= 0 && dmg < Upgrade.values().length ? Machine.values()[dmg] : null;
	}

	public static Machine getMachine(ItemStack item) {
        return item != null && ItemLoader.itemUpgrade.equals(item.getItem()) ? getMachine(item.getItemDamage()) : null;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> lst) {
		for (int i = 0; i < Machine.values().length; ++i) {
			Machine[] machine = Machine.values().clone();
			lst.add(machine[i].getItemStack());
		}
	}

	@Override
	public void addInformation(ItemStack item, EntityPlayer player, List<String> list, boolean useExtraInfo) {
		Machine machine = getMachine(item);
		if (machine != null) {
			list.add(machine.getDescription());
		} else {
			list.add(TextFormatting.RED + "This is not a valid item");
		}
	}
}
