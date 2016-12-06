package engineers.workshop.items;

import java.util.List;

import engineers.workshop.EngineersWorkshop;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemUpgrade extends Item {

	public ItemUpgrade() {
		setCreativeTab(EngineersWorkshop.tabWorkshop);
		setHasSubtypes(true);
		
		//Register
		setRegistryName("engineersworkshop:upgrade");
		GameRegistry.register(this);
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {
		Upgrade upgrade = getUpgrade(item);
		return "engineersworkshop:upgrade." + (upgrade != null ? upgrade.getName() : "unknown");
	}

	public static Upgrade getUpgrade(int dmg) {
		if (dmg >= 0 && dmg < Upgrade.values().length) {
			return Upgrade.values()[dmg];
		} else {
			return null;
		}
	}

	public static Upgrade getUpgrade(ItemStack item) {
		if (item != null && EngineersWorkshop.itemUpgrade.equals(item.getItem())) {
			return getUpgrade(item.getItemDamage());
		} else {
			return null;
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> lst) {
		for (int i = 0; i < Upgrade.values().length; ++i) {
			Upgrade[] upgrades = Upgrade.values().clone();
			lst.add(upgrades[i].getItemStack());
		}
	}

	@Override
	public void addInformation(ItemStack item, EntityPlayer player, List<String> lst, boolean useExtraInfo) {
		Upgrade upgrade = getUpgrade(item);
		if (upgrade != null) {
			upgrade.addInfo(lst);
		} else {
			lst.add(TextFormatting.RED + "This is not a valid item");
		}
	}
}
