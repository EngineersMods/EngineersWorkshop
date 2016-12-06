package us.engineersworkshop.gui.container.slot;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;


public class SlotUnitFurnaceInput extends us.engineersworkshop.gui.container.slot.SlotUnit {
    public SlotUnitFurnaceInput(us.engineersworkshop.tileentity.TileEntityTable table, us.engineersworkshop.page.Page page, int id, int x, int y, us.engineersworkshop.page.unit.Unit unit) {
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
