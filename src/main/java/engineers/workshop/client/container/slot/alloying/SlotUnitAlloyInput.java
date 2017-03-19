package engineers.workshop.client.container.slot.alloying;

import engineers.workshop.client.container.slot.SlotUnit;
import engineers.workshop.client.page.Page;
import engineers.workshop.client.page.unit.Unit;
import engineers.workshop.common.table.TileTable;
import net.minecraft.item.ItemStack;

public class SlotUnitAlloyInput extends SlotUnit {

	public SlotUnitAlloyInput(TileTable table, Page page, int id, int x, int y, Unit unit) {
		super(table, page, id, x, y, unit);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return super.isItemValid(itemstack);
	}

	@Override
	public boolean canShiftClickInto(ItemStack item) {
		return true;
	}
}
