package engineers.workshop.client.gui.container.slot;

import engineers.workshop.client.gui.page.Page;
import engineers.workshop.client.gui.page.unit.Unit;
import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.table.TileTable;
import net.minecraft.item.ItemStack;

public class SlotUnitCraftingOutput extends SlotUnit {

    public SlotUnitCraftingOutput(TileTable table, Page page, int id, int x, int y, Unit unit) {
        super(table, page, id, x, y, unit);
    }

    @Override
    public boolean isVisible() {
        return isAutoCrafting() && super.isVisible();
    }

    @Override
    public boolean isEnabled() {
        return isAutoCrafting() && super.isEnabled();
    }

    private boolean isAutoCrafting() {
        return table.getUpgradePage().hasUpgrade(unit.getId(), Upgrade.AUTO_CRAFTER);
    }

    @Override
    public boolean canSupplyItems() {
        return true;
    }

    @Override
    public boolean canAcceptItems() {
        return false;
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        return false;
    }


}
