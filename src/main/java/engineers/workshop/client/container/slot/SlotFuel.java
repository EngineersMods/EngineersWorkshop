package engineers.workshop.client.container.slot;

import engineers.workshop.client.page.Page;
import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.table.TileTable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class SlotFuel extends SlotTable {

	public SlotFuel(TileTable table, Page page, int id, int x, int y) {
		super(table, page, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		String[] upgrades = {};
		return super.isItemValid(stack) && TileEntityFurnace.isItemFuel(stack) && !(Upgrade.ParentType.CRAFTING.isValidParent(stack) || Upgrade.ParentType.SMELTING.isValidParent(stack) || Upgrade.ParentType.CRUSHING.isValidParent(stack) || Upgrade.ParentType.ALLOY.isValidParent(stack) || Upgrade.ParentType.STORAGE.isValidParent(stack));
	}
}
