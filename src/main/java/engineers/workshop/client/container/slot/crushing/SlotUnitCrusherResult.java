package engineers.workshop.client.container.slot.crushing;

import engineers.workshop.common.table.TileTable;
import engineers.workshop.client.container.slot.SlotUnit;
import engineers.workshop.client.page.Page;
import engineers.workshop.client.page.unit.Unit;
import net.minecraft.item.ItemStack;

public class SlotUnitCrusherResult extends SlotUnit {

    public SlotUnitCrusherResult(TileTable table, Page page, int id, int x, int y, Unit unit) {
        super(table, page, id, x, y, unit);
    }

    @Override
    public boolean isBig() {
        return true;
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean canSupplyItems() {
        return true;
    }

    @Override
    public boolean canAcceptItems() {
        return false;
    }

}
