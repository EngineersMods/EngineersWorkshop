package engineers.workshop.network.data;

import engineers.workshop.gui.page.unit.Unit;
import engineers.workshop.gui.page.unit.UnitCrafting;
import engineers.workshop.items.Upgrade;
import engineers.workshop.network.DataReader;
import engineers.workshop.network.DataWriter;
import engineers.workshop.network.IBitCount;
import engineers.workshop.network.MaxCount;
import engineers.workshop.table.TileTable;

public abstract class DataUnit extends DataBase {
    public static final int LENGTH = 8;

    protected Unit getUnit(TileTable table, int id) {
        boolean isCrafting = id % 2 == 1;
        id /= 2;
        if (isCrafting) {
            return table.getMainPage().getCraftingList().get(id);
        }else{
            return table.getMainPage().getSmeltingList().get(id);
        }
    }

    public static int getId(Unit unit) {
        return unit.getId() * 2 + (unit instanceof UnitCrafting ? 1 : 0);
    }


    public static class Progress extends DataUnit {
        private static final IBitCount BIT_COUNT = new MaxCount(Unit.PRODUCTION_TIME);

        @Override
        public void save(TileTable table, DataWriter dw, int id) {
            dw.writeData(getUnit(table, id).getProductionProgress(), BIT_COUNT);
        }

        @Override
        public void load(TileTable table, DataReader dr, int id) {
            getUnit(table, id).setProductionProgress(dr.readData(BIT_COUNT));
        }
    }

    public static class Charged extends DataUnit {
        private static final IBitCount BIT_COUNT = new MaxCount(Unit.CHARGES_PER_LEVEL * Upgrade.CHARGED.getMaxCount());

        @Override
        public void save(TileTable table, DataWriter dw, int id) {
            dw.writeData(getUnit(table, id).getChargeCount(), BIT_COUNT);
        }

        @Override
        public void load(TileTable table, DataReader dr, int id) {
            getUnit(table, id).setChargeCount(dr.readData(BIT_COUNT));
        }
    }

}
