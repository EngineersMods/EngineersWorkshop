package us.engineersworkshop.gui.container.slot;

public class SlotUnitCraftingStorage extends us.engineersworkshop.gui.container.slot.SlotUnit {
    public SlotUnitCraftingStorage(us.engineersworkshop.tileentity.TileEntityTable table, us.engineersworkshop.page.Page page, int id, int x, int y, us.engineersworkshop.page.unit.Unit unit) {
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
        return table.getUpgradePage().hasUpgrade(unit.getId(), us.engineersworkshop.item.Upgrade.STORAGE);
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
        ((us.engineersworkshop.page.unit.UnitCrafting)unit).onGridChanged();
    }
}
