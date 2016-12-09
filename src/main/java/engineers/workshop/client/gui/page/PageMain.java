package engineers.workshop.client.gui.page;

import engineers.workshop.client.gui.GuiBase;
import engineers.workshop.client.gui.page.unit.Unit;
import engineers.workshop.client.gui.page.unit.UnitCrafting;
import engineers.workshop.client.gui.page.unit.UnitSmelting;
import engineers.workshop.common.table.TileTable;

import java.util.ArrayList;
import java.util.List;

public class PageMain extends Page {

    private List<Unit> units;
    private List<UnitCrafting> craftingList;
    private List<UnitSmelting> smeltingList;

    public PageMain(TileTable table, String name) {
        super(table, name);

        units = new ArrayList<>();
        craftingList = new ArrayList<>();
        smeltingList = new ArrayList<>();

        for (int i = 0; i < 4; i++){
            addUnit(i);
        }
    }

    private void addUnit(int id) {
        int x = (id % 2) * WIDTH / 2;
        int y = (id / 2) * HEIGHT / 2;
        UnitCrafting crafting = new UnitCrafting(table, this, id, x, y );
        UnitSmelting smelting = new UnitSmelting(table, this, id, x, y );
        craftingList.add(crafting);
        smeltingList.add(smelting);
        units.add(crafting);
        units.add(smelting);
    }

    public List<UnitSmelting> getSmeltingList() {
        return smeltingList;
    }

    public List<UnitCrafting> getCraftingList() {
        return craftingList;
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
        gui.drawRect(BAR_HORIZONTAL_X, BAR_HORIZONTAL_Y, 0, TEXTURE_SHEET_SIZE - BAR_THICKNESS, BAR_WIDTH, BAR_THICKNESS);
        gui.drawRect(BAR_VERTICAL_X, BAR_VERTICAL_Y, TEXTURE_SHEET_SIZE - BAR_THICKNESS, 0, BAR_THICKNESS, BAR_HEIGHT);

        for (Unit unit : units) {
            if (unit.isEnabled()) {
                unit.draw(gui, mX, mY);
            }
        }
    }

    @Override
    public void onClick(GuiBase gui, int mX, int mY, int button) {
        for (Unit unit : units) {
            if (unit.isEnabled()) {
                unit.onClick(gui, mX, mY);
            }
        }
    }

    public List<Unit> getUnits() {
        return units;
    }

	@Override
	public String getDesc() {
		return "Viewing station";
	}
}
