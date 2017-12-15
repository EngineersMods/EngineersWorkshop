package engineers.workshop.common.network.data;

import engineers.workshop.common.network.count.IBitCount;
import engineers.workshop.common.network.count.LengthCount;
import engineers.workshop.common.table.TileTable;
import net.minecraft.nbt.NBTTagCompound;

public enum DataType {
	PAGE(DataPage.class),
	PROGRESS(DataUnit.Progress.class, DataUnit.LENGTH),
	SIDE_ENABLED(DataSide.Enabled.class, DataSide.LENGTH),
	SIDE_AUTO(DataSide.Auto.class, DataSide.LENGTH),
	SIDE_FILTER(DataSide.Filter.class, DataSide.FilterBase.LENGTH),
	SIDE_WHITE_LIST(DataSide.WhiteList.class, DataSide.LENGTH),
	SIDE_FILTER_MODE(DataSide.FilterMode.class, DataSide.FilterBase.LENGTH),
	LIT(DataLit.class),
	CHARGED(DataUnit.Charged.class, DataUnit.LENGTH);

	private IBitCount lengthBits;
	private int length;
	private DataBase data;

	DataType(Class<? extends DataBase> clazz, int length) {
		this(clazz);
		this.length = length;
		lengthBits = new LengthCount(length);
	}

	DataType(Class<? extends DataBase> clazz) {
		try {
			data = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.length = 1;
	}

	public void save(TileTable table, NBTTagCompound dw, int id) {
		if (data != null) {
			if (id == -1) {
				for (int i = 0; i < length; i++) {
					data.save(table, dw, i);
				}
			} else {
				if (lengthBits != null) {
					dw.setInteger("id", id);
				}
				data.save(table, dw, id);
			}
		}
	}

	public int load(TileTable table, NBTTagCompound dr, boolean all) {
		if (data != null) {
			if (all) {
				for (int i = 0; i < length; i++) {
					data.load(table, dr, i);
				}
			} else {
				int id = 0;
				if (lengthBits != null) {
					id = dr.getInteger("id");
				}
				data.load(table, dr, id);
				return id;
			}
		}

		return -1;
	}

	public boolean shouldBounce(TileTable table) {
		return data != null && data.shouldBounce(table);
	}

	public boolean shouldBounceToAll(TileTable table) {
		return data != null && data.shouldBounceToAll(table);
	}
}
