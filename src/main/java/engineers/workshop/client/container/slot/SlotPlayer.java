package engineers.workshop.client.container.slot;

import engineers.workshop.common.table.TileTable;
import engineers.workshop.client.GuiBase;
import net.minecraft.inventory.IInventory;

public class SlotPlayer extends SlotBase {
	public SlotPlayer(IInventory inventory, TileTable table, int id, int x, int y) {
		super(inventory, table, id, x, y);
	}

	@Override
	public int getTextureIndex(GuiBase gui) {
		return shouldHighlight(gui.getSelectedSlot(), this) && gui.getSelectedSlot().shouldSlotHighlightItems() ? 3
				: super.getTextureIndex(gui);
	}

	@Override
	public boolean shouldSlotHighlightItems() {
		return false;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
}
