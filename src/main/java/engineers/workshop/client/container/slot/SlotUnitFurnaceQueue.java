package engineers.workshop.client.container.slot;

import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.table.TileTable;
import engineers.workshop.client.page.Page;
import engineers.workshop.client.page.unit.Unit;
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
