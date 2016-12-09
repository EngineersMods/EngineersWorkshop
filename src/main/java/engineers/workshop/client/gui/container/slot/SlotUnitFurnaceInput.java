package engineers.workshop.client.gui.container.slot;

import engineers.workshop.client.gui.page.Page;
import engineers.workshop.client.gui.page.unit.Unit;
import engineers.workshop.common.table.TileTable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class SlotUnitFurnaceInput extends SlotUnit {

    public SlotUnitFurnaceInput(TileTable table, Page page, int id, int x, int y, Unit unit) {
        super(table, page, id, x, y, unit);
    }

    @Override
    public boolean isItemValid(ItemStack itemstack) {
        return super.isItemValid(itemstack) && FurnaceRecipes.instance().getSmeltingResult(itemstack) != null;
    }

    @Override
    public boolean canShiftClickInto(ItemStack item) {
        return true;
    }
}
