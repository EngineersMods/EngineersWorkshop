package engineers.workshop.client.container.slot;

import engineers.workshop.common.table.TileTable;
import engineers.workshop.client.GuiBase;
import engineers.workshop.client.page.setting.Transfer;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotBase extends Slot {
	private int x;
	private int y;
	private Transfer[] input = new Transfer[6];
	private Transfer[] output = new Transfer[6];
	protected TileTable table;

	public SlotBase(IInventory inventory, TileTable table, int id, int x, int y) {
		super(inventory, id, x, y);

		this.x = x;
		this.y = y;
		this.table = table;
	}

	public void updateClient(boolean visible) {
		if (visible && isEnabled()) {
			xDisplayPosition = getX();
			yDisplayPosition = getY();
		} else {
			xDisplayPosition = -9000;
			yDisplayPosition = -9000;
		}
	}

	public void updateServer() {
		if (!isEnabled() && getHasStack()) {
			table.spitOutItem(getStack());
			putStack(null);
		}
		
		if(getStack().stackSize == 0){
			putStack(null);
		}
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return isEnabled();
	}

	public boolean isVisible() {
		return table.getMenu() == null;
	}

	public boolean isEnabled() {
		return true;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getTextureIndex(GuiBase gui) {
		return isEnabled() ? 0 : 1;
	}

	public boolean isBig() {
		return false;
	}

	public boolean isOutputValid(int id, ItemStack item) {
		return output[id] != null && output[id].isValid(table, item);
	}

	public boolean isInputValid(int id, ItemStack item) {
		return input[id] != null && input[id].isValid(table, item);
	}

	public void resetValidity(int id) {
		this.output[id] = null;
		this.input[id] = null;
	}

	public void setValidity(int id, Transfer input, Transfer output) {
		this.output[id] = output;
		this.input[id] = input;
	}

	public boolean canAcceptItems() {
		return true;
	}

	public boolean canSupplyItems() {
		return true;
	}

	public boolean canAcceptItem(ItemStack item) {
		return true;
	}

	@Override
	public int getSlotStackLimit() {
		return getSlotStackLimit(null);
	}

	public int getSlotStackLimit(ItemStack item) {
		return super.getSlotStackLimit();
	}

	public boolean canPickUpOnDoubleClick() {
		return isVisible() && isEnabled();
	}

	public boolean canDragIntoSlot() {
		return true;
	}

	public boolean canShiftClickInto(ItemStack item) {
		return true;
	}

	public boolean shouldSlotHighlightItems() {
		return true;
	}

	public boolean shouldSlotHighlightSelf() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	protected static boolean shouldHighlight(SlotBase slot, SlotBase other) {
		return Minecraft.getMinecraft().thePlayer.inventory.getItemStack() == null && slot != null
				&& !slot.getHasStack() && other != null && other.getHasStack() && slot.isItemValid(other.getStack())
				&& slot.getSlotStackLimit(other.getStack()) > (slot.getHasStack() ? slot.getStack().stackSize : 0);
	}

	public boolean shouldDropOnClosing() {
		return true;
	}
}
