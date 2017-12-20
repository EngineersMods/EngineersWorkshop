package engineers.workshop.client.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
    This is a client and therefore extends Container, however, to clean it all up all the Container code is included
    in this class and it's therefore not using any code in the Container class. However, to make this compatible as a
    client for other classes (interfaces and the current open client a player has for instance) it must still
    extend Container.
 */

@SuppressWarnings("unused")
public abstract class ContainerBase extends RebornContainer {

	private static final int MOUSE_LEFT_CLICK = 0;
	private static final int MOUSE_RIGHT_CLICK = 1;
	private static final int FAKE_SLOT_ID = -999;
	private static final int CLICK_MODE_NORMAL = 0;
	private static final int CLICK_MODE_SHIFT = 1;
	private static final int CLICK_MODE_KEY = 2;
	private static final int CLICK_MODE_PICK_ITEM = 3;
	private static final int CLICK_MODE_OUTSIDE = 4;
	private static final int CLICK_DRAG_RELEASE = 5;
	private static final int CLICK_MODE_DOUBLE_CLICK = 6;
	private static final int CLICK_DRAG_MODE_PRE = 0;
	private static final int CLICK_DRAG_MODE_SLOT = 1;
	private static final int CLICK_DRAG_MODE_POST = 2;
	private final Set<Slot> draggedSlots = new HashSet<>();
	@SideOnly(Side.CLIENT)
	private short transactionID;
	private int dragMouseButton = -1;
	private int dragMode;
	private Set<EntityPlayer> invalidPlayers = new HashSet<>();

	private List<ItemStack> getItems() {
		return inventoryItemStacks;
	}

	private List<Slot> getSlots() {
		return inventorySlots;
	}

	@Override
	protected Slot addSlotToContainer(Slot slot) {
		slot.slotNumber = this.inventorySlots.size();
		getSlots().add(slot);
		getItems().add(ItemStack.EMPTY);
		return slot;
	}

	@Override
	public NonNullList<ItemStack> getInventory() {

		NonNullList<ItemStack> result = NonNullList.create();
		getSlots().forEach(slot -> result.add(slot.getStack()));
		return result;
	}

	@Override
	public boolean enchantItem(EntityPlayer player, int slotId) {
		return false;
	}

	@Override
	public Slot getSlot(int slotId) {
		return getSlots().get(slotId);
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventory) {
		detectAndSendChanges();
	}

	@Override
	public void putStackInSlot(int slotId, ItemStack item) {
		getSlot(slotId).putStack(item);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {}

	@SideOnly(Side.CLIENT)
	public short getNextTransactionID(InventoryPlayer inventory) {
		transactionID++;
		return transactionID;
	}

	protected boolean isPlayerValid(EntityPlayer player) {
		return !invalidPlayers.contains(player);
	}

	protected void setValidState(EntityPlayer player, boolean valid) {
		if (valid) {
			invalidPlayers.remove(player);
		} else {
			invalidPlayers.add(player);
		}
	}

	protected void resetDragging() {
		dragMode = 0;
		draggedSlots.clear();
	}

	@Override
	public boolean canDragIntoSlot(Slot slot) {
		return true;
	}

	protected int getSlotStackLimit(Slot slot, ItemStack itemStack) {
		return slot.getSlotStackLimit();
	}

}