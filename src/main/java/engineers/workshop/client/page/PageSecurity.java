package engineers.workshop.client.page;

import engineers.workshop.client.gui.GuiBase;
import engineers.workshop.client.page.security.Security;
import engineers.workshop.common.table.TileTable;

public class PageSecurity extends Page {

	private static final int HEADER_WIDTH = 38;
	private static final int HEADER_HEIGHT = 17;
	private Security security;

	public PageSecurity(TileTable table, String name) {
		super(table, name);
	}

	@Override
	public int createSlots(int id) {
		return id;
	}

	@Override
	public String getDesc() {
		return "Manage Security";
	}

	@Override
	public void draw(GuiBase gui, int mX, int mY) {
		if (!hasSecurity()) {
			gui.prepare();
			gui.drawRect(100, 10, 2, 0, HEADER_WIDTH + 3, HEADER_HEIGHT);

			gui.drawCenteredString("Claim!", 100, 15, HEADER_WIDTH + 3, 1, 0xDECADE);
		}
		super.draw(gui, mX, mY);
	}

	@Override
	public void onClick(GuiBase gui, int mX, int mY, int button) {
		super.onClick(gui, mX, mY, button);
	}

	public Security getSecurity() {
		return security;
	}

	public boolean hasSecurity() {
		return security != null;
	}
	
	@Override
	public boolean isEnabled() {
		return false;
	}

}
