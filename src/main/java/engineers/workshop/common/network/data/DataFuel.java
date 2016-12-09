package engineers.workshop.common.network.data;

import engineers.workshop.common.network.DataReader;
import engineers.workshop.common.network.DataWriter;
import engineers.workshop.common.network.IBitCount;
import engineers.workshop.common.network.MaxCount;
import engineers.workshop.common.table.TileTable;

public class DataFuel extends DataBase {
	//TODO: not a todo, max fuel (packet) is 32000
    private static IBitCount FUEL_BIT_COUNT = new MaxCount(16000);

    @Override
    public void save(TileTable table, DataWriter dw, int id) {
    	dw.writeData(table.getPower(), FUEL_BIT_COUNT);
//    	dw.writeData(table.getMaxPower(), FUEL_BIT_COUNT);
    }

    @Override
    public void load(TileTable table, DataReader dr, int id) {
    	table.setPower(dr.readData(FUEL_BIT_COUNT));
//    	table.setMaxPower(dr.readData(FUEL_BIT_COUNT));
    }
}
