package engineers.workshop.common.network.data;

import engineers.workshop.common.network.DataReader;
import engineers.workshop.common.network.DataWriter;
import engineers.workshop.common.table.TileTable;

public abstract class DataBase {

    public abstract void save(TileTable table, DataWriter dw, int id);
    public abstract void load(TileTable table, DataReader dr, int id);
    public boolean shouldBounce(TileTable table) {
        return true;
    }
    public boolean shouldBounceToAll(TileTable table) {
        return false;
    }
}
