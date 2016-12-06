package engineers.workshop.network.data;

import engineers.workshop.network.BasicCount;
import engineers.workshop.network.DataReader;
import engineers.workshop.network.DataWriter;
import engineers.workshop.network.IBitCount;
import engineers.workshop.table.TileTable;

public class DataPage extends DataBase {
    private static final IBitCount BITS = new BasicCount(2);

    @Override
    public void save(TileTable table, DataWriter dw, int id) {
        dw.writeData(table.getSelectedPage().getId(), BITS);
    }

    @Override
    public void load(TileTable table, DataReader dr, int id) {
        table.setSelectedPage(table.getPages().get(dr.readData(BITS)));
    }
}
