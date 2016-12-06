package us.engineersworkshop.gui.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import us.engineersworkshop.page.unit.UnitCrafting;


public class SlotUnitCraftingResult extends us.engineersworkshop.gui.container.slot.SlotUnit {
    public SlotUnitCraftingResult(us.engineersworkshop.tileentity.TileEntityTable table, us.engineersworkshop.page.Page page, int id, int x, int y, us.engineersworkshop.page.unit.UnitCrafting unit) {
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
    public void onPickupFromSlot(EntityPlayer player, ItemStack item) {
        super.onPickupFromSlot(player, item);
        ((UnitCrafting)unit).onCrafting(player, item);
    }

    @Override
    public boolean canAcceptItems() {
        return false;
    }


    @Override
    public int getY() {
        int offset = 0;
        if (table.getUpgradePage().hasUpgrade(unit.getId(), us.engineersworkshop.item.Upgrade.AUTO_CRAFTER)) {
            offset = us.engineersworkshop.page.unit.UnitCrafting.RESULT_AUTO_OFFSET;
        }
        return super.getY() + offset;
    }

    @Override
    public boolean canPickUpOnDoubleClick() {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int count) {
        ItemStack itemstack = getStack();
        if (itemstack != null) {
            putStack(null);
        }
        return itemstack;
    }

    @Override
    public boolean shouldDropOnClosing() {
        return false;
    }
}
