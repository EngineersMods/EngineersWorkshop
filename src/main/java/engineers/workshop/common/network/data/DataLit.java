package engineers.workshop.common.network.data;

import engineers.workshop.common.network.DataReader;
import engineers.workshop.common.network.DataWriter;
import engineers.workshop.common.table.TileTable;

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
