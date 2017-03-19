package engineers.workshop.client.page.unit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.alloy.AlloyRecipeManager;
import crazypants.enderio.machine.recipe.IRecipe;
import engineers.workshop.client.GuiBase;
import engineers.workshop.client.container.slot.alloying.SlotUnitAlloyInput;
import engineers.workshop.client.container.slot.alloying.SlotUnitAlloyQueue;
import engineers.workshop.client.container.slot.alloying.SlotUnitAlloyResult;
import engineers.workshop.client.page.Page;
import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.loaders.ConfigLoader;
import engineers.workshop.common.table.TileTable;
import engineers.workshop.common.util.Logger;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UnitAlloy extends Unit {

	public UnitAlloy(TileTable table, Page page, int id, int x, int y) {
		super(table, page, id, x, y);
	}

	private int inputId;
	private int inputSize = 3;
	private int outputId;
	private int queueId;

	private static final int QUEUE_MAX_COUNT = 3;
	private static final int QUEUE_X = 5;
	private static final int QUEUE_Y = 5;
	private static final int START_X = 25;
	private static final int START_Y = 23;
	private static final int RESULT_X = 56;
	private static final int SLOT_SIZE = 18;

	@Override
	public int createSlots(int id) {
		inputId = id;
		addSlot(new SlotUnitAlloyInput(table, page, id++, this.x + START_X, this.y + START_Y, this));
		addSlot(new SlotUnitAlloyInput(table, page, id++, this.x + START_X - (SLOT_SIZE/2), this.y + START_Y + SLOT_SIZE, this));
		addSlot(new SlotUnitAlloyInput(table, page, id++, this.x + START_X + (SLOT_SIZE/2), this.y + START_Y + SLOT_SIZE, this));
		outputId = id;
		addSlot(new SlotUnitAlloyResult(table, page, id++, this.x + START_X + RESULT_X, this.y + START_Y, this));
		queueId = id;

		for (int i = 0; i < QUEUE_MAX_COUNT; i++) {
			addSlot(new SlotUnitAlloyQueue(table, page, id++, this.x + QUEUE_X, this.y + QUEUE_Y + i * SLOT_SIZE, this,
					QUEUE_ORDER[i]));
		}

		return id;
	}

	private static final int[] QUEUE_ORDER = { 2, 0, 1 };
	private static final int[] QUEUE_ORDER_START = { 1, 1, 0 };

	@Override
	public void onUpdate() {
		super.onUpdate();

		int queueLength = table.getUpgradePage().getUpgradeCount(id, Upgrade.QUEUE);
		if (queueLength > 0) {
			int start = QUEUE_ORDER_START[queueLength - 1];
			for (int i = start + queueLength - 1; i >= start; i--) {
				int targetId;
				if (i == start + queueLength - 1) {
					targetId = inputId;
				} else {
					targetId = queueId + i + 1;
				}
				int sourceId = queueId + i;

				ItemStack target = table.getStackInSlot(targetId);
				ItemStack source = table.getStackInSlot(sourceId);
				if (source != null) {
					ItemStack move = source.copy();
					move.stackSize = 1;
					if (canMove(move, target)) {
						if (target == null) {
							table.setInventorySlotContents(targetId, move);
						} else {
							target.stackSize++;
						}
						source.stackSize--;
						if (source.stackSize == 0) {
							table.setInventorySlotContents(sourceId, null);
						}
					}
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void draw(GuiBase gui, int mX, int mY) {
		super.draw(gui, mX, mY);

		// gui.drawRect(100, 100, 100, 100, 100, 100);
		//
		// boolean isEmpty = true;
		// for (int i = gridId; i < gridId + GRID_SIZE; i++) {
		// if (table.getStackInSlot(i) != null) {
		// isEmpty = false;
		// break;
		// }
		// }
		//
		// int x = this.x + START_X + GRID_WIDTH * SLOT_SIZE + CLEAR_OFFSET_X;
		// int y = this.y + START_Y + CLEAR_OFFSET_Y;
		//
		// int index;
		// if (isEmpty) {
		// index = 0;
		// } else if (gui.inBounds(x, y, CLEAR_SIZE, CLEAR_SIZE, mX, mY)) {
		// index = 2;
		// gui.drawMouseOver("Clear grid");
		// } else {
		// index = 1;
		// }
		//
		// gui.drawRect(x, y, CLEAR_SRC_X + index * CLEAR_SIZE, CLEAR_SRC_Y,
		// CLEAR_SIZE, CLEAR_SIZE);
	}

	@Override
	protected ItemStack getProductionResult() {
		List<MachineRecipeInput> inputs = new ArrayList<MachineRecipeInput>();
		for (int i = 0; i < inputSize; i++) {
			ItemStack stack = table.getStackInSlot(i + inputId);
			if (isItemStackValid(stack))
				inputs.add(new MachineRecipeInput(i, stack));
		}
		IRecipe ir = AlloyRecipeManager.getInstance().getRecipeForInputs(inputs.toArray(new MachineRecipeInput[] {}));
		return inputs == null ? null : ir == null ? null : ir.getOutputs()[0].getOutput();
	}

	private boolean isItemStackValid(ItemStack in) {
		return in != null && in.getItem() != null;
	}

	@Override
	protected void onProduction(ItemStack result) {
		List<MachineRecipeInput> inputs = new ArrayList<MachineRecipeInput>();
		for (int i = 0; i < inputSize; i++) {
			ItemStack stack = table.getStackInSlot(i + inputId);
			if (isItemStackValid(stack))
				inputs.add(new MachineRecipeInput(i, stack));
		}
		IRecipe ir = AlloyRecipeManager.getInstance().getRecipeForInputs(inputs.toArray(new MachineRecipeInput[] {}));

		List<ItemStack> stacks = ir.getInputStacks();
		for (ItemStack stack : stacks) {
			for (int i = 0; i < 3; i++) {
				ItemStack tableStack = table.getStackInSlot(inputId + i);
				if (tableStack.getItem() == stack.getItem()) {
					tableStack.stackSize -= stack.stackSize;
					break;
				}
			}
		}
	}

	@Override
	public int getOutputId() {
		return outputId;
	}

	@Override
	public boolean isEnabled() {
		ItemStack item = table.getUpgradePage().getUpgradeMainItem(id);
		return item != null
				&& ArrayUtils.contains(ConfigLoader.MACHINES.ALLOY_BLOCKS, item.getItem().getRegistryName().toString());
	}

	private static final int ARROW_X = 25;
	private static final int ARROW_Y = 1;

	@Override
	public int getArrowX() {
		return START_X + ARROW_X;
	}

	@Override
	public int getArrowY() {
		return START_Y + ARROW_Y;
	}
}
