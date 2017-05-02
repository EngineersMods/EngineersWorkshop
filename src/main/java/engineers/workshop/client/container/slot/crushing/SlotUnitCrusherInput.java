package engineers.workshop.client.container.slot.crushing;

import engineers.workshop.client.container.slot.SlotUnit;
import engineers.workshop.client.page.Page;
import engineers.workshop.client.page.unit.Unit;
import engineers.workshop.common.table.TileTable;
import net.minecraft.item.ItemStack;

public class SlotUnitCrusherInput extends SlotUnit {

	public SlotUnitCrusherInput(TileTable table, Page page, int id, int x, int y, Unit unit) {
		super(table, page, id, x, y, unit);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return super.isItemValid(itemstack) ;//&& (SagMillRecipeManager.getInstance().getRecipeForInput(itemstack) != null) ;
	}

	@Override
	public boolean canShiftClickInto(ItemStack item) {
		return true;
	}
}
