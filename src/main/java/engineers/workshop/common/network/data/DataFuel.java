package engineers.workshop.common.network.data;

import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.loaders.ConfigLoader;
import engineers.workshop.common.network.DataReader;
import engineers.workshop.common.network.DataWriter;
import engineers.workshop.common.network.IBitCount;
import engineers.workshop.common.network.MaxCount;
import engineers.workshop.common.table.TileTable;

public class DataFuel extends DataBase {
    private static IBitCount FUEL_BIT_COUNT = new MaxCount(ConfigLoader.MIN_POWER + (ConfigLoader.MAX_POWER_CHANGE * Upgrade.MAX_POWER.getMaxCount()));

    @Override
    public void save(TileTable table, DataWriter dw, int id) {
    	dw.writeData(table.getPower(), FUEL_BIT_COUNT);
    }

    @Override
    public void load(TileTable table, DataReader dr, int id) {
    	table.setPower(dr.readData(FUEL_BIT_COUNT));
    }
}
