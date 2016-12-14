package engineers.workshop.client.page.setting;

import engineers.workshop.common.table.TileTable;
import engineers.workshop.client.container.slot.SlotBase;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Setting {
	private int x;
	private int y;
	protected int id;
	protected TileTable table;
	private List<Side> sides;

	public Setting(TileTable table, int id, int x, int y) {
		this.table = table;
		this.id = id;
		this.x = x;
		this.y = y;
		sides = new ArrayList<>();
	}

	public boolean isValid() {
		return getItem() != null;
	}

	public abstract ItemStack getItem();

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public List<Side> getSides() {
		return sides;
	}

	public int getId() {
		return id;
	}

	public abstract List<SlotBase> getSlots();

	public abstract String getName();
}