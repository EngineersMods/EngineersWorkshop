package engineers.workshop.common.network.data;

import engineers.workshop.common.table.TileTable;
import net.minecraft.nbt.NBTTagCompound;

public class DataPage extends DataBase {

	@Override
	public void save(TileTable table, NBTTagCompound dw, int id) {
		dw.setInteger("page", table.getSelectedPage().getId());
	}

	@Override
	public void load(TileTable table, NBTTagCompound dr, int id) {
		if (id >= table.getPages().size()) {
			return;
		}
		table.setSelectedPage(table.getPages().get(dr.getInteger("page")));
	}
}
