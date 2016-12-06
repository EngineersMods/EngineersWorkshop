package engineers.workshop.network.data;

import engineers.workshop.network.DataReader;
import engineers.workshop.network.DataWriter;
import engineers.workshop.table.TileTable;

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
