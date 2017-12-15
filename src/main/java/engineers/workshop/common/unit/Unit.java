package engineers.workshop.common.unit;

import engineers.workshop.client.gui.GuiBase;
import engineers.workshop.client.container.slot.SlotBase;
import engineers.workshop.client.page.Page;
import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.network.data.DataType;
import engineers.workshop.common.network.data.DataUnit;
import engineers.workshop.common.table.TileTable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public abstract class Unit {

	public static final int PRODUCTION_TIME = 400;
	public static final int CHARGES_PER_LEVEL = 4;
	private static final int ARROW_SRC_X = 0;
	private static final int ARROW_SRC_Y = 34;
	private static final int ARROW_WIDTH = 22;
	private static final int ARROW_HEIGHT = 15;
	private static final int PROGRESS_OFFSET = -1;
	private static final String NBT_CHARGED = "Charged";
	private static final String NBT_PROGRESS = "Progress";
	private static final int WORKING_COOLDOWN = 20;
	protected TileTable table;
	protected Page page;
	protected int id;
	protected int x;
	protected int y;
	private int productionProgress;
	private int chargeCount;
	private List<SlotBase> slots = new ArrayList<>();
	private int workingTicks;
	public Unit(TileTable table, Page page, int id, int x, int y) {
		this.table = table;
		this.page = page;
		this.id = id;
		this.x = x;
		this.y = y;
	}

	@SideOnly(Side.CLIENT)
	public void draw(GuiBase gui, int mX, int mY) {
		gui.prepare();
		int x = getArrowX();
		int y = getArrowY();
		gui.drawRect(this.x + x, this.y + y, ARROW_SRC_X, ARROW_SRC_Y, ARROW_WIDTH, ARROW_HEIGHT);
		int max = getMaxCharges();
		boolean charging = false;
		if (max > 0 && chargeCount > 0) {
			charging = true;
			GlStateManager.color(0.11F, 0.35F, 0.17F, 1);
			int count = Math.min(chargeCount, max);
			gui.drawRect(this.x + x, this.y + y, ARROW_SRC_X, ARROW_SRC_Y + ARROW_HEIGHT, count * ARROW_WIDTH / max,
				ARROW_HEIGHT);
		}

		if (isCharging()) {
			charging = true;
			GlStateManager.color(0.25F, 0.8F, 0.38F, 0.5F);
			GlStateManager.enableBlend();
		} else {
			GlStateManager.color(1, 1, 1, 1);
		}
		int progress = Math.min(productionProgress, PRODUCTION_TIME);
		gui.drawRect(this.x + x, this.y + y + PROGRESS_OFFSET, ARROW_SRC_X, ARROW_SRC_Y + ARROW_HEIGHT,
			progress * ARROW_WIDTH / PRODUCTION_TIME, ARROW_HEIGHT);
		GlStateManager.disableBlend();
	}

	@SideOnly(Side.CLIENT)
	public void onClick(GuiBase gui, int mX, int mY) {

	}

	protected int getArrowX() {
		return 2147;
	}

	protected int getArrowY() {
		return 2147;
	}

	protected void addSlot(SlotBase slot) {
		table.addSlot(slot);
		slots.add(slot);
	}

	public int getId() {
		return id;
	}

	public abstract int createSlots(int id);

	protected boolean canCharge() {
		return chargeCount < getMaxCharges();
	}

	private int getMaxCharges() {
		return table.getUpgradePage().getUpgradeCount(id, Upgrade.CHARGED) * CHARGES_PER_LEVEL;
	}

	private boolean isCharging() {
		if (canCharge()) {
			ItemStack result = getProductionResult();
			if (result.isEmpty()) {
				return true;
			} else {
				ItemStack output = table.getStackInSlot(getOutputId());
				return !canMove(result, output);
			}
		} else {
			return false;
		}
	}

	private int getProductionSpeed(boolean charging) {
		int base = 1 + table.getUpgradePage().getUpgradeCount(id, Upgrade.SPEED);

		return charging ? base : base * 4;
	}

	private int getPowerConsumption(boolean charging) {
		int base = 1 + table.getUpgradePage().getUpgradeCount(id, Upgrade.SPEED) * 2;

		return charging ? base * 2 : base;
	}

	private void produce(ItemStack result, ItemStack output) {
		if (output.isEmpty()) {
			table.setInventorySlotContents(getOutputId(), result.copy());
		} else {
			table.getStackInSlot(getOutputId()).grow(result.getCount());
		}

		onProduction(result);
	}

	public void onUpdate() {
		if (!table.getWorld().isRemote) {
			boolean canCharge = false;
			boolean updatedProgress = false;
			boolean canReset = false;
			ItemStack result = getProductionResult();
			if (!result.isEmpty()) {
				boolean updatedCharge = false;
				boolean done;
				do {
					done = true;
					ItemStack output = table.getStackInSlot(getOutputId());
					if (canMove(result, output)) {
						if (chargeCount > 0 && getMaxCharges() > 0) {
							chargeCount--;
							done = false;
							updatedCharge = true;
							produce(result, output);
							result = getProductionResult();
						} else {
							int powerConsumption = getPowerConsumption(false);

							if (table.getFuel() >= powerConsumption) {
								table.setFuel(table.getFuel() - powerConsumption);
								productionProgress += getProductionSpeed(false);
								while (productionProgress >= PRODUCTION_TIME) {
									productionProgress -= PRODUCTION_TIME;
									produce(result, output);
									result = getProductionResult();
									output = table.getStackInSlot(getOutputId());
									if (!canMove(result, output)) {
										break;
									}
								}
								updatedProgress = true;
							}
						}
					} else {
						canCharge = true;
					}
				} while (!done);
				if (updatedCharge) {
					table.sendDataToAllPlayers(DataType.CHARGED, DataUnit.getId(this), table.getOpenPlayers());
				}
			} else {
				canCharge = true;
				canReset = true;
			}

			if (canCharge && canCharge()) {
				boolean done = false;
				while (canCharge() && !done) {
					done = true;
					int powerConsumption = getPowerConsumption(true);
					if (table.getFuel() >= powerConsumption) {
						table.setFuel(table.getFuel() - powerConsumption);
						productionProgress += getProductionSpeed(true);
						if (productionProgress >= PRODUCTION_TIME) {
							productionProgress -= PRODUCTION_TIME;
							chargeCount++;
							table.sendDataToAllPlayers(DataType.CHARGED, DataUnit.getId(this), table.getOpenPlayers());
							done = false;
						}
						updatedProgress = true;
					}
				}
			} else if (canReset && productionProgress != 0) {
				productionProgress = 0;
				updatedProgress = true;
			}
			if (updatedProgress) {
				workingTicks = WORKING_COOLDOWN;
				table.sendDataToAllPlayers(DataType.PROGRESS, DataUnit.getId(this), table.getOpenPlayers());
			} else if (workingTicks > 0) {
				workingTicks--;
			}
		} else if (workingTicks > 0) {
			workingTicks--;
		}
	}

	public int getChargeCount() {
		return chargeCount;
	}

	public void setChargeCount(int chargeCount) {
		this.chargeCount = chargeCount;
	}

	protected abstract ItemStack getProductionResult();

	protected abstract int getOutputId();

	protected abstract void onProduction(ItemStack result);

	protected boolean canMove(ItemStack source, ItemStack target) {
		if (!source.isEmpty()) {
			if (target.isEmpty()) {
				return true;
			} else if (target.isItemEqual(source) && ItemStack.areItemStackTagsEqual(target, source)) {
				int resultSize = target.getCount() + source.getCount();
				if (resultSize <= table.getInventoryStackLimit() && resultSize <= target.getMaxStackSize()) {
					return true;
				}
			}
		}
		return false;
	}

	public int getProductionProgress() {
		return productionProgress;
	}

	public void setProductionProgress(int productionProgress) {
		this.productionProgress = productionProgress;
		workingTicks = WORKING_COOLDOWN;
	}

	public abstract boolean isEnabled();

	public List<SlotBase> getSlots() {
		return slots;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setByte(NBT_CHARGED, (byte) chargeCount);
		compound.setShort(NBT_PROGRESS, (short) productionProgress);
		return compound;
	}

	public void readFromNBT(NBTTagCompound compound) {
		chargeCount = compound.getByte(NBT_CHARGED);
		productionProgress = compound.getShort(NBT_PROGRESS);
	}

	public boolean isWorking() {
		return workingTicks > 0;
	}

}
