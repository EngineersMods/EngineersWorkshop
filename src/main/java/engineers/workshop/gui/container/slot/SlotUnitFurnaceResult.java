package engineers.workshop.gui.container.slot;


import engineers.workshop.gui.page.Page;
import engineers.workshop.gui.page.unit.Unit;
import engineers.workshop.table.TileTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.fml.common.FMLCommonHandler;


public class SlotUnitFurnaceResult extends SlotUnit {
    public SlotUnitFurnaceResult(TileTable table, Page page, int id, int x, int y, Unit unit) {
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

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack item) {
        super.onPickupFromSlot(player, item);
        FMLCommonHandler.instance().firePlayerSmeltedEvent(player, item);
        item.onCrafting(player.getEntityWorld(), player, item.stackSize);
        if (item.getItem() == Items.IRON_INGOT){
            player.addStat(AchievementList.ACQUIRE_IRON, 1);
        }else if (item.getItem() == Items.COOKED_FISH) {
            player.addStat(AchievementList.COOK_FISH, 1);
        }
    }
}
