package engineers.workshop.client.page.setting;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemSetting {
	public static final int ITEM_COUNT = 10;

	private int id;
	@Nonnull
	private ItemStack item = ItemStack.EMPTY;
	private TransferMode mode = TransferMode.PRECISE;

	public ItemSetting(int id) {
		this.id = id;
	}

	@Nonnull
	public ItemStack getItem() {
		return item;
	}

	public void setItem(
		@Nonnull
			ItemStack item) {
		this.item = item;
	}

	public int getId() {
		return id;
	}

	public TransferMode getMode() {
		return mode;
	}

	public void setMode(TransferMode mode) {
		this.mode = mode;
	}
}
