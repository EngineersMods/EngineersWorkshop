package engineers.workshop.client.page.setting;

import java.util.ArrayList;
import java.util.List;

import engineers.workshop.client.container.slot.SlotBase;
import engineers.workshop.common.table.TileTable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

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
