package engineers.workshop.client.container.slot;

import engineers.workshop.common.table.TileTable;
import engineers.workshop.client.page.Page;
import engineers.workshop.client.page.unit.Unit;
import net.minecraft.item.ItemStack;

public class SlotUnit extends SlotTable {

	protected Unit unit;

	public SlotUnit(TileTable table, Page page, int id, int x, int y, Unit unit) {
		super(table, page, id, x, y);

		this.unit = unit;
	}

	@Override
	public boolean isVisible() {
		return super.isVisible() && isEnabled();
	}

	@Override
	public boolean isEnabled() {
		return unit.isEnabled();
	}

	@Override
	public boolean canSupplyItems() {
		return false;
	}

	@Override
	public boolean canShiftClickInto(ItemStack item) {
		return false;
	}
}
