package us.engineersworkshop.gui.container.slot;

import us.engineersworkshop.gui.GuiBase;
import us.engineersworkshop.page.Page;
import us.engineersworkshop.tileentity.TileEntityTable;

public class SlotTable extends us.engineersworkshop.gui.container.slot.SlotBase {
    private us.engineersworkshop.page.Page page;

    public SlotTable(TileEntityTable table, Page page, int id, int x, int y) {
        super(table, table, id, x, y);

        this.page = page;
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && (page == null || page.equals(table.getSelectedPage()));
    }

    @Override
    public int getTextureIndex(GuiBase gui) {
        return shouldSlotHighlightSelf() && shouldHighlight(this, gui.getSelectedSlot()) && gui.getSelectedSlot() instanceof SlotPlayer ? 3 : super.getTextureIndex(gui);
    }
}
