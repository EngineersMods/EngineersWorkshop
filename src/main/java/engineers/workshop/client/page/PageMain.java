package engineers.workshop.client.page;

import engineers.workshop.client.gui.GuiBase;
import engineers.workshop.common.table.TileTable;
import engineers.workshop.common.unit.Unit;
import engineers.workshop.common.unit.UnitCraft;
import engineers.workshop.common.unit.UnitSmelt;
import engineers.workshop.common.unit.UnitStorage;

import java.util.ArrayList;
import java.util.List;

public class PageMain extends Page {

	public static final int WIDTH = 256;
	public static final int HEIGHT = 174;
	private static final int TEXTURE_SHEET_SIZE = 256;
	private static final int BAR_THICKNESS = 4;
	private static final int BAR_WIDTH = 240;
	private static final int BAR_HEIGHT = 162;
	private static final int BAR_HORIZONTAL_X = (WIDTH - BAR_WIDTH) / 2;
	private static final int BAR_HORIZONTAL_Y = (HEIGHT - BAR_THICKNESS) / 2;
	private static final int BAR_VERTICAL_X = (WIDTH - BAR_THICKNESS) / 2;
	private static final int BAR_VERTICAL_Y = (HEIGHT - BAR_HEIGHT) / 2;
	private List<Unit> units;
	private List<UnitCraft> craftingList;
	private List<UnitSmelt> smeltingList;
	private List<UnitStorage> storageList;
	public PageMain(TileTable table, String name) {
		super(table, name);

		units = new ArrayList<>();
		craftingList = new ArrayList<>();
		smeltingList = new ArrayList<>();
		storageList = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			addUnit(i);
		}
	}

	private void addUnit(int id) {
		int x = (id % 2) * WIDTH / 2;
		int y = (id / 2) * HEIGHT / 2;

		UnitCraft crafting = new UnitCraft(table, this, id, x, y);
		craftingList.add(crafting);
		units.add(crafting);

		UnitSmelt smelting = new UnitSmelt(table, this, id, x, y);
		smeltingList.add(smelting);
		units.add(smelting);

		UnitStorage storage = new UnitStorage(table, this, id, x, y);
		storageList.add(storage);
		units.add(storage);
	}

	public List<UnitSmelt> getSmeltingList() {
		return smeltingList;
	}

	public List<UnitCraft> getCraftingList() {
		return craftingList;
	}

	public List<UnitStorage> getStorageList() {
		return storageList;
	}

	@Override
	public void onUpdate() {
		for (Unit unit : units) {
			if (unit.isEnabled()) {
				unit.onUpdate();
			}
		}
	}

	@Override
	public int createSlots(int id) {
		for (Unit unit : units) {
			id = unit.createSlots(id);
		}
		return id;
	}

	@Override
	public void draw(GuiBase gui, int mX, int mY) {
		gui.prepare();
		int enabledUnits = 0;

		for (Unit unit : units) {
			if (unit.isEnabled()) {
				enabledUnits++;
			}
		}

		if (drawHorizontal())
			gui.drawRect(BAR_HORIZONTAL_X, BAR_HORIZONTAL_Y, 0, TEXTURE_SHEET_SIZE - BAR_THICKNESS, BAR_WIDTH, BAR_THICKNESS);

		if (drawVertical())
			gui.drawRect(BAR_VERTICAL_X, BAR_VERTICAL_Y, TEXTURE_SHEET_SIZE - BAR_THICKNESS, 0, BAR_THICKNESS, BAR_HEIGHT);

		if (enabledUnits == 0) {
			gui.drawString("ADD A CRAFTING TABLE, CHEST OR FURNACE", 45, 30, 0x1E1E1E);
			gui.drawString("IN THE UPGRADE PAGE TO GET STARTED", 40, 45, 0x1E1E1E);
		}

		units.stream().filter(Unit::isEnabled).forEachOrdered(unit -> unit.draw(gui, mX, mY));
	}

	@Override
	public void onClick(GuiBase gui, int mX, int mY, int button) {
		units.stream().filter(Unit::isEnabled).forEachOrdered(unit -> unit.onClick(gui, mX, mY));
	}

	public List<Unit> getUnits() {
		return units;
	}

	@Override
	public String getDesc() {
		return "Workshop Area";
	}

	private boolean[] makeUnitMap() {
		boolean[] out = new boolean[4];
		for (int i = 0; i < out.length; i++) {
			out[i] = isUnitLoaded(i);
		}
		return out;
	}

	private boolean isUnitLoaded(int id) {

		if (craftingList.size() <= id || smeltingList.size() <= id || storageList.size() <= id)
			return false;

		return (
			craftingList.get(id) != null && craftingList.get(id).isEnabled())
			|| (smeltingList.get(id) != null && smeltingList.get(id).isEnabled()
			|| (storageList.get(id) != null && storageList.get(id).isEnabled())
		);
	}

	public boolean drawVertical() {
		boolean[] map = makeUnitMap();
		return (map[0] && map[1]) || (map[0] && map[3]) || (map[2] && map[1]) || (map[2] && map[3]);
	}

	public boolean drawHorizontal() {
		boolean[] map = makeUnitMap();
		return (map[0] && map[2]) || (map[0] && map[3]) || (map[1] && map[2]) || (map[1] && map[3]);
	}
}
