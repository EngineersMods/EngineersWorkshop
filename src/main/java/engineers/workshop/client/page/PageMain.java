package engineers.workshop.client.page;

import java.util.ArrayList;
import java.util.List;

import engineers.workshop.client.GuiBase;
import engineers.workshop.client.page.unit.Unit;
import engineers.workshop.client.page.unit.UnitCrafting;
import engineers.workshop.client.page.unit.UnitCrushing;
import engineers.workshop.client.page.unit.UnitSmelting;
import engineers.workshop.common.table.TileTable;
import net.minecraftforge.fml.common.Loader;

public class PageMain extends Page {

	private List<Unit> units;
	private List<UnitCrafting> craftingList;
	private List<UnitSmelting> smeltingList;
	private List<UnitCrushing> crushingList;

	public PageMain(TileTable table, String name) {
		super(table, name);

		units = new ArrayList<>();
		craftingList = new ArrayList<>();
		smeltingList = new ArrayList<>();
		if(Loader.isModLoaded("EnderIO"))
			crushingList = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			addUnit(i);
		}
	}

	private void addUnit(int id) {
		int x = (id % 2) * WIDTH / 2;
		int y = (id / 2) * HEIGHT / 2;
		UnitCrafting crafting = new UnitCrafting(table, this, id, x, y);
		UnitSmelting smelting = new UnitSmelting(table, this, id, x, y);
		craftingList.add(crafting);
		smeltingList.add(smelting);
		units.add(crafting);
		units.add(smelting);
		if(Loader.isModLoaded("EnderIO")){
			UnitCrushing crushing = new UnitCrushing(table, this, id, x, y);
			crushingList.add(crushing);
			units.add(crushing);
		}
	}

	public List<UnitSmelting> getSmeltingList() {
		return smeltingList;
	}

	public List<UnitCrafting> getCraftingList() {
		return craftingList;
	}
	
	public List<UnitCrafting> getCrushingList() {
		return craftingList;
	}

	@Override
	public void onUpdate() {
		units.stream().filter(Unit::isEnabled).forEachOrdered(Unit::onUpdate);
	}

	@Override
	public int createSlots(int id) {
		for (Unit unit : units) {
			id = unit.createSlots(id);
		}
		return id;
	}

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
			gui.drawString("ADD A CRAFTING TABLE OR FURNACE", 45, 30, 0x1E1E1E);
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

	public boolean[] makeUnitMap() {
		boolean[] out = new boolean[4];
		for (int i = 0; i < out.length; i++) {
			out[i] = craftingList.get(i).isEnabled() || smeltingList.get(i).isEnabled() || crushingList.get(i).isEnabled();
		}
		return out;
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
