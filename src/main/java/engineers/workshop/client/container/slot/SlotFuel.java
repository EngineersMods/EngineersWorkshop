package engineers.workshop.client.container.slot;

import engineers.workshop.common.loaders.ConfigLoader;
import engineers.workshop.common.table.TileTable;

import org.apache.commons.lang3.ArrayUtils;

import engineers.workshop.client.page.Page;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class SlotFuel extends SlotTable {

	public SlotFuel(TileTable table, Page page, int id, int x, int y) {
		super(table, page, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		String[] upgrades = {};
		upgrades = ArrayUtils.addAll(upgrades, ConfigLoader.MACHINES.CRAFTER_BLOCKS);
		upgrades = ArrayUtils.addAll(upgrades, ConfigLoader.MACHINES.FURNACE_BLOCKS);
		upgrades = ArrayUtils.addAll(upgrades, ConfigLoader.MACHINES.CRUSHER_BLOCKS);
		return super.isItemValid(itemstack) && TileEntityFurnace.isItemFuel(itemstack) && !(ArrayUtils.contains(upgrades, itemstack.getItem().getRegistryName().toString()));
	}
}
