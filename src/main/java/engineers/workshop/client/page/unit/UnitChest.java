package engineers.workshop.client.page.unit;

import engineers.workshop.client.container.slot.storage.SlotUnitStorage;
import engineers.workshop.client.page.Page;
import engineers.workshop.common.loaders.ConfigLoader;
import engineers.workshop.common.table.TileTable;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by EwyBoy
 */
public class UnitChest extends Unit {

    private static final int START_X = 12;
    private static final int START_Y = 8;
    private static final int SLOT_SIZE = 18;
    private static final int GRID_WIDTH = 6;
    private static final int GRID_HEIGHT = 4;
    public static final int GRID_SIZE = GRID_WIDTH * GRID_HEIGHT;

    private int gridId;

    public UnitChest(TileTable table, Page page, int id, int x, int y) {
        super(table, page, id, x, y);
    }


    /** TODO Please make this optional so I don't have to hack it */
    @Override
    protected int getArrowX() {
        return 100000;
    }

    /** TODO Please make this optional so I don't have to hack it */
    @Override
    protected int getArrowY() {
        return 100000;
    }

    @Override
    public int createSlots(int id) {
        gridId = id;

        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0;  x < GRID_WIDTH; x++) {
                addSlot(new SlotUnitStorage(table, page, id++, this.x + START_X + x * SLOT_SIZE, this.y + START_Y + y * SLOT_SIZE, this));
            }
        }
        return id;
    }

    @Override
    protected ItemStack getProductionResult() {
        return null;
    }

    @Override
    protected int getOutputId() {
        return 0;
    }

    @Override
    protected void onProduction(ItemStack result) {}

    @Override
    public boolean isEnabled() {
        ItemStack item = table.getUpgradePage().getUpgradeMainItem(id);
        return item != null && ArrayUtils.contains(ConfigLoader.MACHINES.STORAGE_BLOCKS, item.getItem().getRegistryName().toString());
    }
}
