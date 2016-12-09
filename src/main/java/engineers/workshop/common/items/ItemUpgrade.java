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

public class ItemUpgrade extends Item {

	public ItemUpgrade() {
		setCreativeTab(CreativeTabLoader.tabWorkshop);
		setHasSubtypes(true);
		setRegistryName(MODID + ":" + "upgrade");
		GameRegistry.register(this);
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {
		Upgrade upgrade = getUpgrade(item);
		return MODID + ":" + "upgrade" +  "." + (upgrade != null ? upgrade.getName() : "unknown");
	}

	public static Upgrade getUpgrade(int dmg) {
		return dmg >= 0 && dmg < Upgrade.values().length ? Upgrade.values()[dmg] : null;
	}

	public static Upgrade getUpgrade(ItemStack item) {
		return item != null && ItemLoader.itemUpgrade.equals(item.getItem()) ? getUpgrade(item.getItemDamage()) : null;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (int i = 0; i < Upgrade.values().length; ++i) {
			Upgrade[] upgrades = Upgrade.values().clone();
			list.add(upgrades[i].getItemStack());
		}
	}

	@Override
	public void addInformation(ItemStack item, EntityPlayer player, List<String> list, boolean useExtraInfo) {
		Upgrade upgrade = getUpgrade(item);
		if (upgrade != null) upgrade.addInfo(list);
		else list.add(TextFormatting.RED + "This is not a valid item");
	}
}
