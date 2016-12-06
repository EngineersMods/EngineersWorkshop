package engineers.workshop.network.data;

import engineers.workshop.network.DataReader;
import engineers.workshop.network.DataWriter;
import engineers.workshop.network.IBitCount;
import engineers.workshop.network.MaxCount;
import engineers.workshop.table.TileTable;

public class DataLava extends DataBase {
	//TODO: not a todo, max lava (packet) is 5,000;
    private static IBitCount LAVA_BIT_COUNT = new MaxCount(5000);

    @Override
    public void save(TileTable table, DataWriter dw, int id) {
        dw.writeData(table.getLava(), LAVA_BIT_COUNT);
    }

    @Override
    public void load(TileTable table, DataReader dr, int id) {
        table.setLava(dr.readData(LAVA_BIT_COUNT));
    }
}
