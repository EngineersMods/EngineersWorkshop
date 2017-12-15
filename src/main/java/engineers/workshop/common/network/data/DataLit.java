package engineers.workshop.common.network.data;

import engineers.workshop.common.table.TileTable;
import net.minecraft.nbt.NBTTagCompound;

public class DataLit extends DataBase {
	@Override
	public void save(TileTable table, NBTTagCompound dw, int id) {
		dw.setBoolean("lit", table.isLit());
	}

	@Override
	public void load(TileTable table, NBTTagCompound dr, int id) {
		table.setLit(dr.getBoolean("lit"));
	}
}
