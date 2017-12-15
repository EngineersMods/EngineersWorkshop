package engineers.workshop.client.container;

import engineers.workshop.client.container.slot.SlotBase;
import engineers.workshop.client.container.slot.SlotPlayer;
import engineers.workshop.common.table.TileTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTable extends ContainerBase {

	private static final int SLOT_SIZE = 18;
	private static final int SLOTS_PER_ROW = 9;
	private static final int NORMAL_ROWS = 3;
	private static final int PLAYER_X = 48;
	private static final int PLAYER_Y = 174;
	private static final int PLAYER_HOT_BAR_Y = 232;
	public int power;
	private TileTable table;
	public ContainerTable(TileTable table, EntityPlayer player) {
		this.table = table;

		table.getSlots().forEach(this::addSlotToContainer);
		InventoryPlayer inventory = player.inventory;

		for (int y = 0; y < NORMAL_ROWS; y++) {
			for (int x = 0; x < SLOTS_PER_ROW; x++) {
				addSlotToContainer(new SlotPlayer(inventory, table, x + y * SLOTS_PER_ROW + SLOTS_PER_ROW, PLAYER_X + x * SLOT_SIZE, y * SLOT_SIZE + PLAYER_Y));
			}
		}

		for (int x = 0; x < SLOTS_PER_ROW; x++) {
			addSlotToContainer(new SlotPlayer(inventory, table, x, PLAYER_X + x * SLOT_SIZE, PLAYER_HOT_BAR_Y));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return table.isUsableByPlayer(player);
	}

	@Override
	protected int getSlotStackLimit(Slot slot, ItemStack item) {
		return ((SlotBase) slot).getSlotStackLimit(item);
	}

	@Override
	public boolean canDragIntoSlot(Slot slot) {
		return ((SlotBase) slot).canDragIntoSlot();
	}

	public TileTable getTable() {
		return table;
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		listener.sendAllWindowProperties(this, table);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < this.listeners.size(); ++i) {
			IContainerListener icontainerlistener = this.listeners.get(i);
			if (this.power != table.getFuel()) {
				icontainerlistener.sendWindowProperty(this, 0, table.getFuel());
			}
		}
	}

	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		if (id == 0) {
			this.power = data;
		}
	}
}
