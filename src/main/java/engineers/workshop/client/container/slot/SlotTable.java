package engineers.workshop.client.container.slot;

import engineers.workshop.client.gui.GuiBase;
import engineers.workshop.client.page.Page;
import engineers.workshop.common.table.TileTable;

public class SlotTable extends SlotBase {
	private Page page;

	public SlotTable(TileTable table, Page page, int id, int x, int y) {
		super(table, table, id, x, y);

		this.page = page;
	}

	@Override
	public boolean isVisible() {
		return super.isVisible() && (page == null || page.equals(table.getSelectedPage()));
	}

	@Override
	public int getTextureIndex(GuiBase gui) {
		return super.getTextureIndex(gui);
	}
}
