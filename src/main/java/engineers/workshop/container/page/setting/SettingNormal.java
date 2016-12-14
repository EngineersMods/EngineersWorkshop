package engineers.workshop.container.page.setting;

import engineers.workshop.common.table.TileTable;
import engineers.workshop.container.container.slot.SlotBase;
import engineers.workshop.container.page.unit.Unit;
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
        Unit unit = table.getMainPage().getCraftingList().get(id);
        if (!unit.isEnabled()) {
            unit = table.getMainPage().getSmeltingList().get(id);
            if (!unit.isEnabled()) {
                return null;
            }
        }
        return unit.getSlots();
    }

    @Override
    public String getName() {
        return null;
    }
}
