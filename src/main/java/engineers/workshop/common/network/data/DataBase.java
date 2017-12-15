package engineers.workshop.common.network.data;

import engineers.workshop.common.table.TileTable;
import net.minecraft.nbt.NBTTagCompound;

public abstract class DataBase {

	public abstract void save(TileTable table, NBTTagCompound dw, int id);

	public abstract void load(TileTable table, NBTTagCompound dr, int id);

	public boolean shouldBounce(TileTable table) {
		return true;
	}

	public boolean shouldBounceToAll(TileTable table) {
		return false;
	}
}
