package engineers.workshop.common.network.data;

import engineers.workshop.client.page.setting.*;
import engineers.workshop.common.table.TileTable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class DataSide extends DataBase {

	private static final int SETTINGS = 5;
	private static final int SIDES = 6;
	private static final int MODES = 2;
	public static final int LENGTH = SETTINGS * SIDES * MODES;

	public static int getId(Setting setting, Side side, Transfer transfer) {
		return setting.getId() + SETTINGS * side.getDirection().ordinal() + (transfer.isInput() ? 0 : SETTINGS * SIDES);
	}

	protected Transfer getTransfer(TileTable table, int id) {
		int settingId = id % SETTINGS;
		id /= SETTINGS;
		int sideId = id % SIDES;
		id /= SIDES;
		int modeId = id;
		Side side = table.getTransferPage().getSettings().get(settingId).getSides().get(sideId);
		if (modeId == 0) {
			return side.getInput();
		} else {
			return side.getOutput();
		}
	}
	
	@Override
	public boolean shouldBounce(TileTable table) {
		return true;
	}
	
	@Override
	public boolean shouldBounceToAll(TileTable table) {
		return true;
	}

	public static class Enabled extends DataSide {
		@Override
		public void save(TileTable table, NBTTagCompound dw, int id) {
			dw.setBoolean("enabled", getTransfer(table, id).isEnabled());
		}

		@Override
		public void load(TileTable table, NBTTagCompound dr, int id) {
			getTransfer(table, id).setEnabled(dr.getBoolean("enabled"));
		}
	}

	public static class Auto extends DataSide {
		@Override
		public void save(TileTable table, NBTTagCompound dw, int id) {
			dw.setBoolean("auto", getTransfer(table, id).isAuto());
		}

		@Override
		public void load(TileTable table, NBTTagCompound dr, int id) {
			getTransfer(table, id).setAuto(dr.getBoolean("auto"));
		}
	}

	public static class WhiteList extends DataSide {
		@Override
		public void save(TileTable table, NBTTagCompound dw, int id) {
			dw.setBoolean("whitelist", getTransfer(table, id).hasWhiteList());
		}

		@Override
		public void load(TileTable table, NBTTagCompound dr, int id) {
			getTransfer(table, id).setUseWhiteList(dr.getBoolean("whitelist"));
		}
	}

	public static abstract class FilterBase extends DataSide {
		public static final int LENGTH = DataSide.LENGTH * ItemSetting.ITEM_COUNT;

		public static int getId(Setting setting, Side side, Transfer transfer, ItemSetting itemSetting) {
			return getId(setting, side, transfer) * ItemSetting.ITEM_COUNT + itemSetting.getId();
		}

		protected ItemSetting getSetting(TileTable table, int id) {
			return getTransfer(table, id / ItemSetting.ITEM_COUNT).getItem(id % ItemSetting.ITEM_COUNT);
		}
	}

	public static class Filter extends FilterBase {
		@Override
		public void save(TileTable table, NBTTagCompound dw, int id) {
			ItemSetting setting = getSetting(table, id);
			ItemStack itemStack = setting.getItem();

			dw.setBoolean("hasItem", !itemStack.isEmpty());
			if (!itemStack.isEmpty()) {
				dw.setInteger("id", Item.getIdFromItem(itemStack.getItem()));
				dw.setInteger("damage", itemStack.getItemDamage());
				if (itemStack.hasTagCompound()) {
					dw.setTag("nbt", itemStack.getTagCompound());
				}

			}
		}

		@Override
		public void load(TileTable table, NBTTagCompound dr, int id) {
			ItemSetting setting = getSetting(table, id);

			if (dr.getBoolean("hasItem")) {
				int itemId = dr.getInteger("id");
				int itemDmg = dr.getInteger("damage");

				ItemStack item = new ItemStack(Item.getItemById(itemId), 1, itemDmg);
				if (dr.hasKey("nbt")) {
					item.setTagCompound(dr.getCompoundTag("nbt"));
				}

				setting.setItem(item);
			} else {
				setting.setItem(ItemStack.EMPTY);
			}
		}
	}

	public static class FilterMode extends FilterBase {
		@Override
		public void save(TileTable table, NBTTagCompound dw, int id) {
			dw.setInteger("mode", getSetting(table, id).getMode().ordinal());
		}

		@Override
		public void load(TileTable table, NBTTagCompound dr, int id) {
			getSetting(table, id).setMode(TransferMode.values()[dr.getInteger("mode")]);
		}
	}
}
