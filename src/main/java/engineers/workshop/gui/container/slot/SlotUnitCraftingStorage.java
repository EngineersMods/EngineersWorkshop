package engineers.workshop.gui.container.slot;

import engineers.workshop.gui.page.Page;
import engineers.workshop.gui.page.unit.Unit;
import engineers.workshop.gui.page.unit.UnitCrafting;
import engineers.workshop.items.Upgrade;
import engineers.workshop.table.TileTable;

public class SlotUnitCraftingStorage extends SlotUnit {
    public SlotUnitCraftingStorage(TileTable table, Page page, int id, int x, int y, Unit unit) {
        super(table, page, id, x, y, unit);
    }

    @Override
    public boolean isVisible() {
        return isAvailable() && super.isVisible();
    }

    @Override
    public boolean isEnabled() {
        return isAvailable() && super.isEnabled();
    }

    private boolean isAvailable() {
        return table.getUpgradePage().hasUpgrade(unit.getId(), Upgrade.STORAGE);
    }

    @Override
    public boolean canAcceptItems() {
        return true;
    }

    @Override
    public boolean shouldSlotHighlightItems() {
        return false;
    }

    @Override
    public boolean shouldSlotHighlightSelf() {
        return false;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        ((UnitCrafting)unit).onGridChanged();
    }
}
