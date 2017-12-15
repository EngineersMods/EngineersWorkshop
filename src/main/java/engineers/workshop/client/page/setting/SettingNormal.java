package engineers.workshop.client.page.setting;

import engineers.workshop.client.container.slot.SlotBase;
import engineers.workshop.common.table.TileTable;
import engineers.workshop.common.unit.Unit;
import net.minecraft.item.ItemStack;

import java.util.List;

public class SettingNormal extends Setting {

	public SettingNormal(TileTable table, int id, int x, int y) {
		super(table, id, x, y);
	}

	@Override
	public ItemStack getItem() {
		return table.getUpgradePage().getUpgradeMainItem(id);
	}

	@Override
	public List<SlotBase> getSlots() {
		Unit unit = null;
		unit = table.getMainPage().getCraftingList().get(id);
		if (unit != null && !unit.isEnabled()) {
			unit = table.getMainPage().getSmeltingList().get(id);
		}

		return unit != null ? unit.getSlots() : null;
	}

	@Override
	public String getName() {
		return null;
	}

}
