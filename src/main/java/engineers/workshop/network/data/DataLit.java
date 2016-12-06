package engineers.workshop.network.data;

import engineers.workshop.network.DataReader;
import engineers.workshop.network.DataWriter;
import engineers.workshop.table.TileTable;

public class DataLit extends DataBase {
    @Override
    public void save(TileTable table, DataWriter dw, int id) {
        dw.writeBoolean(table.isLit());
    }

    @Override
    public void load(TileTable table, DataReader dr, int id) {
        table.setLit(dr.readBoolean());
    }
}
