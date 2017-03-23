package engineers.workshop.client.container.slot.crafting;

import engineers.workshop.common.table.TileTable;
import engineers.workshop.client.container.slot.SlotUnit;
import engineers.workshop.client.page.Page;
import engineers.workshop.client.page.unit.UnitCraft;
import net.minecraft.item.ItemStack;

public class SlotUnitCraftingGrid extends SlotUnit {

	public SlotUnitCraftingGrid(TileTable table, Page page, int id, int x, int y, UnitCraft unit) {
		super(table, page, id, x, y, unit);
	}

	@Override
	public boolean canAcceptItem(ItemStack item) {
		if (getHasStack() && item != null) {
			UnitCraft crafting = (UnitCraft) unit;

			int own = getCount(item, getStack());

			if (own != -1) {
				int start = crafting.getGridId();
				for (int i = start; i < start + 9; i++) {
					if (i != getSlotIndex()) {
						int other = getCount(item, table.getStackInSlot(i));
						if (other != -1 && other < own) {
							return false;
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	private int getCount(ItemStack item, ItemStack slotItem) {
		if (slotItem != null && slotItem.isItemEqual(item) && ItemStack.areItemStackTagsEqual(item, slotItem)) {
			return slotItem.stackSize;
		} else {
			return -1;
		}
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
		((UnitCraft) unit).onGridChanged();
	}

	@Override
	public boolean shouldSlotHighlightItems() {
		return false;
	}

	@Override
	public boolean shouldSlotHighlightSelf() {
		return false;
	}
}
