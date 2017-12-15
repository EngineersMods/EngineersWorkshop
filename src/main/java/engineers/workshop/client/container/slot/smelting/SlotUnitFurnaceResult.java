package engineers.workshop.client.container.slot.smelting;

import engineers.workshop.client.container.slot.SlotUnit;
import engineers.workshop.client.page.Page;
import engineers.workshop.common.table.TileTable;
import engineers.workshop.common.unit.Unit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
	public ItemStack onTake(EntityPlayer player, ItemStack item) {
		item = super.onTake(player, item);
		FMLCommonHandler.instance().firePlayerSmeltedEvent(player, item);
		item.onCrafting(player.getEntityWorld(), player, item.getCount());
		return item;
	}
}
