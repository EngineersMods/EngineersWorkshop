package engineers.workshop.gui.page.setting;


import java.util.List;

import engineers.workshop.gui.container.slot.SlotBase;
import engineers.workshop.gui.page.unit.Unit;
import engineers.workshop.table.TileTable;
import net.minecraft.item.ItemStack;

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
