package engineers.workshop.client.container.slot;

import engineers.workshop.common.table.TileTable;
import engineers.workshop.client.page.Page;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class SlotFuel extends SlotTable {

	public SlotFuel(TileTable table, Page page, int id, int x, int y) {
		super(table, page, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		
		return super.isItemValid(itemstack) && TileEntityFurnace.isItemFuel(itemstack);
	}
}
