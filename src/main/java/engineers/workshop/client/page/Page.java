package engineers.workshop.client.page;

import engineers.workshop.client.gui.GuiBase;
import engineers.workshop.client.gui.GuiTable;
import engineers.workshop.client.container.slot.SlotBase;
import engineers.workshop.common.table.TileTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

public abstract class Page {

	protected TileTable table;
	private String name;
	private int id;

	public Page(TileTable table, String name) {
		this.id = table.getPages().size();
		this.table = table;
		this.name = name;
	}

	public String getName() {
		return name.toUpperCase();
	}

	public int createSlots(int id) {
		return id;
	}

	public String getDesc() {
		return "Add a description!";
	}

	protected void addSlot(SlotBase slot) {
		table.addSlot(slot);
	}

	@SideOnly(Side.CLIENT)
	public void draw(GuiBase gui, int mX, int mY) {
		gui.drawString(StringUtils.capitalize(name), 8, 6, 0x1E1E1E);
	}

	@SideOnly(Side.CLIENT)
	public void onClick(GuiBase gui, int mX, int mY, int button) {
	}

	public int getId() {
		return id;
	}

	public void onUpdate() {
	}

	@SideOnly(Side.CLIENT)
	public void onRelease(GuiTable gui, int mX, int mY, int button) {
	}

	public boolean isEnabled() {
		return true;
	}
}
