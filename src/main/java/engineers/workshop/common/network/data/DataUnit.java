package engineers.workshop.common.network.data;

import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.loaders.ConfigLoader;
import engineers.workshop.common.network.DataReader;
import engineers.workshop.common.network.DataWriter;
import engineers.workshop.common.network.IBitCount;
import engineers.workshop.common.network.MaxCount;
import engineers.workshop.common.table.TileTable;
import engineers.workshop.client.page.unit.Unit;

public abstract class DataUnit extends DataBase {

    public static final int LENGTH = 8;

    protected Unit getUnit(TileTable table, int id) {
    	id /= 2;
    	
    	Unit smelting = table.getMainPage().getSmeltingList().get(id);
    	Unit crushing = ConfigLoader.MACHINES.CRUSHER_ENABLED ? crushing = table.getMainPage().getCrushingList().get(id) : null;
    	Unit crafting = table.getMainPage().getCraftingList().get(id);
    	
        if (smelting.isEnabled())
        	return smelting;
        else if(ConfigLoader.MACHINES.CRUSHER_ENABLED && crushing.isEnabled())
        	return crushing;
        else
        	return crafting;
        
        	
    }

    public static int getId(Unit unit) {
        return unit.getId() *2;
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
