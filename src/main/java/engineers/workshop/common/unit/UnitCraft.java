package engineers.workshop.common.unit;

import engineers.workshop.client.gui.GuiBase;
import engineers.workshop.client.container.slot.crafting.SlotUnitCraftingGrid;
import engineers.workshop.client.container.slot.crafting.SlotUnitCraftingOutput;
import engineers.workshop.client.container.slot.crafting.SlotUnitCraftingResult;
import engineers.workshop.client.container.slot.crafting.SlotUnitCraftingStorage;
import engineers.workshop.client.page.Page;
import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.table.TileTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class UnitCraft extends Unit {

	public static final int RESULT_AUTO_OFFSET = -5;
	private static final int START_X = 5;
	private static final int START_Y = 5;
	private static final int SLOT_SIZE = 18;
	private static final int GRID_WIDTH = 3;
	private static final int GRID_HEIGHT = 3;
	public static final int GRID_SIZE = GRID_WIDTH * GRID_HEIGHT;
	private static final int RESULT_OFFSET_X = 94;
	private static final int RESULT_OFFSET_Y = 18;
	private static final int STORAGE_COUNT = 6;
	private static final int STORAGE_Y = 65;
	private static final int ARROW_X = 61;
	private static final int ARROW_Y = 19;
	private static final int CLEAR_SRC_X = 48;
	private static final int CLEAR_SRC_Y = 112;
	private static final int CLEAR_SIZE = 9;
	private static final int CLEAR_OFFSET_X = 3;
	private static final int CLEAR_OFFSET_Y = 0;
	private static final int CAN_CRAFT_DELAY = 10;
	private int gridId;
	private int resultId;
	private int outputId;
	private CraftingBase inventoryCrafting = new CraftingWrapper();
	private boolean canAutoCraft;
	private boolean lockedRecipeGeneration;
	private int canCraftTick = 0;
	private CraftingBase oldGrid;

	public UnitCraft(TileTable table, Page page, int id, int x, int y) {
		super(table, page, id, x, y);
	}

	@Override
	public int createSlots(int id) {
		gridId = id;

		for (int y = 0; y < GRID_HEIGHT; y++) {
			for (int x = 0; x < GRID_WIDTH; x++) {
				addSlot(new SlotUnitCraftingGrid(table, page, id++, this.x + START_X + x * SLOT_SIZE,
					this.y + START_Y + y * SLOT_SIZE, this));
			}
		}

		for (int i = 0; i < STORAGE_COUNT; i++) {
			addSlot(new SlotUnitCraftingStorage(table, page, id++, this.x + START_X + i * SLOT_SIZE, this.y + STORAGE_Y,
				this));
		}

		resultId = id;
		addSlot(new SlotUnitCraftingResult(table, page, id++, this.x + START_X + RESULT_OFFSET_X,
			this.y + START_Y + RESULT_OFFSET_Y, this));

		outputId = id;
		addSlot(new SlotUnitCraftingOutput(table, page, id++, this.x + START_X + RESULT_OFFSET_X,
			this.y + START_Y + 2 * SLOT_SIZE, this));

		return id;
	}

	@Override
	public boolean isEnabled() {
		ItemStack item = table.getUpgradePage().getUpgradeMainItem(id);
		return !item.isEmpty() && Upgrade.ParentType.CRAFTING.isValidParent(item);
	}

	@Override
	protected boolean canCharge() {
		return super.canCharge() && table.getUpgradePage().hasUpgrade(id, Upgrade.AUTO_CRAFTER);
	}

	public void onCrafting(EntityPlayer player, ItemStack item) {
		onCrafted(player, item);
		lockedRecipeGeneration = true;
		try {
			onCrafting(inventoryCrafting, player == null, false);
		} finally {
			lockedRecipeGeneration = false;
		}
		onGridChanged();
	}

	private void onCrafted(EntityPlayer player, ItemStack itemStack) {

		if (itemStack.isEmpty()) {
			return;
		}

		Item item = itemStack.getItem();

		try {
			item.onCreated(itemStack, table.getWorld(), player);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void draw(GuiBase gui, int mX, int mY) {
		super.draw(gui, mX, mY);

		boolean isEmpty = true;
		for (int i = gridId; i < gridId + GRID_SIZE; i++) {
			if (!table.getStackInSlot(i).isEmpty()) {
				isEmpty = false;
				break;
			}
		}

		int x = this.x + START_X + GRID_WIDTH * SLOT_SIZE + CLEAR_OFFSET_X;
		int y = this.y + START_Y + CLEAR_OFFSET_Y;

		int index;
		if (isEmpty) {
			index = 0;
		} else if (gui.inBounds(x, y, CLEAR_SIZE, CLEAR_SIZE, mX, mY)) {
			index = 2;
			gui.drawMouseOver("Clear grid");
		} else {
			index = 1;
		}

		gui.drawRect(x, y, CLEAR_SRC_X + index * CLEAR_SIZE, CLEAR_SRC_Y, CLEAR_SIZE, CLEAR_SIZE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onClick(GuiBase gui, int mX, int mY) {
		super.onClick(gui, mX, mY);
		if (gui.inBounds(this.x + START_X + GRID_WIDTH * SLOT_SIZE + CLEAR_OFFSET_X, this.y + START_Y + CLEAR_OFFSET_Y,
			CLEAR_SIZE, CLEAR_SIZE, mX, mY)) {
			table.clearGridSend(id);
		}
	}

	private void onCrafting(CraftingBase crafting, boolean auto, boolean fake) {
		for (int i = 0; i < GRID_SIZE; i++) {
			ItemStack itemStack = crafting.getStackInSlot(i);
			if (!itemStack.isEmpty()) {
				int id = i;
				for (int j = auto ? 0 : GRID_SIZE; j < crafting.getFullSize(); j++) {
					if (i == j)
						continue;

					ItemStack other = crafting.getStackInSlot(j);
					// TODO support ore dictionary and fuzzy etc?. Problem is
					// that it needs to figure out if the recipe supports it
					if (!other.isEmpty() && (j >= GRID_SIZE || other.getCount() > itemStack.getCount())
						&& itemStack.isItemEqual(other) && ItemStack.areItemStackTagsEqual(itemStack, other)) {
						id = j;
						itemStack = other;
						break;
					}
				}

				crafting.decrStackSize(id, 1);
				if (itemStack.getItem().hasContainerItem(itemStack)) {
					ItemStack containerItem = itemStack.getItem().getContainerItem(itemStack);
					if (!containerItem.isItemStackDamageable()
						|| containerItem.getItemDamage() <= containerItem.getMaxDamage()) {
						crafting.setInventorySlotContents(id, containerItem);
					}
				}
			}
		}
	}

	public int getGridId() {
		return gridId;
	}

	public void onGridChanged() {
		if (!lockedRecipeGeneration) {
			IRecipe recipe = inventoryCrafting.getRecipe();
			ItemStack result = inventoryCrafting.getResult(recipe);
			if (!result.isEmpty()) {
				result = result.copy();
			}
			table.setInventorySlotContents(resultId, result);

			if (table.getUpgradePage().hasUpgrade(id, Upgrade.AUTO_CRAFTER)) {
				if (recipe == null) {
					canAutoCraft = false;
				} else {
					CraftingBase dummy = new CraftingDummy(inventoryCrafting);
					onCrafting(dummy, true, true);
					canAutoCraft = dummy.isMatch(recipe);
				}
			}
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (++canCraftTick == CAN_CRAFT_DELAY) {
			canCraftTick = 0;
			if (oldGrid == null || !oldGrid.equals(inventoryCrafting)) {
				oldGrid = new CraftingDummy(inventoryCrafting);
				onGridChanged();
			}
		}
	}

	public void onUpgradeChange() {
		if (table.getUpgradePage().getUpgradeCount(id, Upgrade.AXE) > 0 && table.getUpgradePage().getUpgradeCount(id, Upgrade.AUTO_CRAFTER) > 0) {
			for (int i = 0; i < 9; i++) {
				table.getSlots().get(i + gridId).setEnabled(false);
			}
			table.getSlots().get(gridId + 4).setEnabled(true);
		} else {
			for (int i = 0; i < 9; i++) {
				table.getSlots().get(i + gridId).setEnabled(true);
			}
		}
		onGridChanged();
	}

	@Override
	protected int getArrowX() {
		return START_X + ARROW_X;
	}

	@Override
	protected int getArrowY() {
		int offset = 0;
		if (table.getUpgradePage().hasUpgrade(id, Upgrade.AUTO_CRAFTER)) {
			offset = RESULT_AUTO_OFFSET;
		}
		return START_Y + ARROW_Y + offset;
	}

	@Override
	protected ItemStack getProductionResult() {
		if (table.getUpgradePage().hasUpgrade(id, Upgrade.AUTO_CRAFTER)) {
			ItemStack result = table.getStackInSlot(resultId);
			if (!result.isEmpty() && canAutoCraft) {
				return result;
			}
		}
		return ItemStack.EMPTY;
	}

	//	private static final IRecipe REPAIR_RECIPE = new RepairRecipe();
	//
	//	private static class RepairRecipe implements IRecipe {
	//
	//		@Override
	//		public boolean matches(InventoryCrafting crafting, World world) {
	//			return getCraftingResult(crafting) != null;
	//		}
	//
	//		@SuppressWarnings("deprecation")
	//		@Override
	//		public ItemStack getCraftingResult(InventoryCrafting crafting) {
	//			Item repairItem = null;
	//			int count = 0, units = 0;
	//
	//			for (int i = 0; i < crafting.getSizeInventory(); i++) {
	//				ItemStack item = crafting.getStackInSlot(i);
	//				if (!item.isEmpty()) {
	//					if (repairItem == null) {
	//						repairItem = item.getItem();
	//						if (!repairItem.isRepairable()) {
	//							return null;
	//						}
	//						units = repairItem.getMaxDamage() * 5 / 100;
	//					} else if (repairItem != item.getItem() || item.getCount() != 1 || count == 2) {
	//						return null;
	//					}
	//
	//					units += item.getMaxDamage() - item.getItemDamage();
	//					count++;
	//				}
	//			}
	//
	//			if (repairItem != null && count == 2) {
	//				int damage = repairItem.getMaxDamage() - units;
	//				if (damage < 0) {
	//					damage = 0;
	//				}
	//				return new ItemStack(repairItem, 1, damage);
	//			} else {
	//				return null;
	//			}
	//		}
	//
	//		@Override
	//		public int getRecipeSize() {
	//			return 9;
	//		}
	//
	//		@Override
	//		public ItemStack getRecipeOutput() {
	//			return null;
	//		}
	//
	//		@Override
	//		public ItemStack[] getRemainingItems(InventoryCrafting inv) {
	//			return null;
	//		}
	//	}

	@Override
	protected int getOutputId() {
		return outputId;
	}

	@Override
	protected void onProduction(ItemStack result) {
		onCrafting(null, result);
	}

	private class CraftingWrapper extends CraftingBase {
		@Override
		public ItemStack getStackInSlot(int id) {
			return table.getStackInSlot(gridId + id);
		}

		@Override
		public void setInventorySlotContents(int id, ItemStack item) {
			table.setInventorySlotContents(gridId + id, item);
		}
	}

	private class CraftingDummy extends CraftingBase {
		private ItemStack[] items;

		private CraftingDummy(CraftingBase base) {
			items = new ItemStack[base.getFullSize()];
			for (int i = 0; i < items.length; i++) {
				ItemStack itemStack = base.getStackInSlot(i);
				if (!itemStack.isEmpty()) {
					items[i] = itemStack.copy();
				}
			}
		}

		@Override
		public int getFullSize() {
			return items.length;
		}

		@Override
		public ItemStack getStackInSlot(int id) {
			return items[id];
		}

		@Override
		public void setInventorySlotContents(int id, ItemStack item) {
			items[id] = item;
		}
	}

	private class CraftingBase extends InventoryCrafting {

		private static final int INVENTORY_WIDTH = 3;
		private static final int INVENTORY_HEIGHT = 3;

		public CraftingBase() {
			super(null, INVENTORY_WIDTH, INVENTORY_HEIGHT);
		}

		@Override
		public final int getSizeInventory() {
			return INVENTORY_WIDTH * INVENTORY_HEIGHT;
		}

		protected int getFullSize() {
			return INVENTORY_WIDTH * INVENTORY_HEIGHT
				+ (table.getUpgradePage().hasUpgrade(id, Upgrade.STORAGE) ? STORAGE_COUNT : 0);
		}

		@Override
		public ItemStack decrStackSize(int id, int count) {
			ItemStack item = getStackInSlot(id);
			if (!item.isEmpty()) {
				if (item.getCount() <= count) {
					setInventorySlotContents(id, ItemStack.EMPTY);
					return item;
				}
				ItemStack result = item.splitStack(count);
				if (item.getCount() == 0) {
					setInventorySlotContents(id, ItemStack.EMPTY);
				}
				return result;
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Override
		public ItemStack getStackInRowAndColumn(int x, int y) {
			if (x >= 0 && x < INVENTORY_WIDTH) {
				int id = x + y * INVENTORY_WIDTH;
				return this.getStackInSlot(id);
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Nonnull
		public ItemStack getResult(IRecipe recipe) {
			return recipe == null ? ItemStack.EMPTY : recipe.getCraftingResult(this);
		}

		public boolean isMatch(IRecipe recipe) {
			return recipe.matches(this, table.getWorld());
		}

		public IRecipe getRecipe() {
			//			if (isMatch(REPAIR_RECIPE)) {
			//				return REPAIR_RECIPE;
			//			}

			for (IRecipe recipe : CraftingManager.REGISTRY) {
				if (isMatch(recipe)) {
					return recipe;
				}
			}

			return null;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof CraftingBase))
				return false;
			CraftingBase crafting = (CraftingBase) obj;
			if (getFullSize() != crafting.getFullSize())
				return false;

			for (int i = 0; i < getFullSize(); i++) {
				if (getStackInSlot(i) == null) { //Not sure whats cuasing it, but this is a dirty work around
					return false;
				}
				if (!ItemStack.areItemStacksEqual(getStackInSlot(i), crafting.getStackInSlot(i))) {
					return false;
				}
			}
			return true;
		}
	}
}