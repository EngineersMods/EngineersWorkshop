package engineers.workshop.container.page.setting;

import engineers.workshop.common.table.TileTable;
import engineers.workshop.container.container.slot.SlotBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SettingCoal extends Setting {
	private ItemStack itemStack;

	public SettingCoal(TileTable table, int id, int x, int y) {
		super(table, id, x, y);
		itemStack = new ItemStack(Items.COAL);
	}

	@Override
	public ItemStack getItem() {
		return itemStack;
	}

	@Override
	public List<SlotBase> getSlots() {
		List<SlotBase> slots = new ArrayList<>();
		slots.add(table.getSlots().get(0));
		return slots;
	}

	@Override
	public String getName() {
		return "Fuel";
	}
}
