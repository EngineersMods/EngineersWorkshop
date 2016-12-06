package engineers.workshop.gui.container.slot;


import engineers.workshop.gui.page.Page;
import engineers.workshop.gui.page.unit.Unit;
import engineers.workshop.items.Upgrade;
import engineers.workshop.table.TileTable;
import net.minecraft.item.ItemStack;

public class SlotUnitFurnaceQueue extends SlotUnitFurnaceInput {
    private int queueId;
    public SlotUnitFurnaceQueue(TileTable table, Page page, int id, int x, int y, Unit unit, int queueId) {
        super(table, page, id, x, y, unit);
        this.queueId = queueId;
    }

    @Override
    public boolean isVisible() {
        return isUsed() && super.isVisible();
    }

    @Override
    public boolean isEnabled() {
        return isUsed() && super.isEnabled();
    }

    private boolean isUsed() {
        return queueId < table.getUpgradePage().getUpgradeCount(unit.getId(), Upgrade.QUEUE);
    }

    @Override
    public boolean canShiftClickInto(ItemStack item) {
        return true;
    }
}
