package engineers.workshop.table;

import java.util.ArrayList;
import java.util.List;

import engineers.workshop.gui.container.slot.SlotBase;
import engineers.workshop.gui.container.slot.SlotFuel;
import engineers.workshop.gui.menu.GuiMenu;
import engineers.workshop.gui.menu.GuiMenuItem;
import engineers.workshop.gui.page.Page;
import engineers.workshop.gui.page.PageMain;
import engineers.workshop.gui.page.PageTransfer;
import engineers.workshop.gui.page.PageUpgrades;
import engineers.workshop.gui.page.setting.Setting;
import engineers.workshop.gui.page.setting.Side;
import engineers.workshop.gui.page.setting.Transfer;
import engineers.workshop.gui.page.unit.Unit;
import engineers.workshop.gui.page.unit.UnitCrafting;
import engineers.workshop.items.Upgrade;
import engineers.workshop.network.DataReader;
import engineers.workshop.network.DataWriter;
import engineers.workshop.network.IBitCount;
import engineers.workshop.network.LengthCount;
import engineers.workshop.network.PacketHandler;
import engineers.workshop.network.PacketId;
import engineers.workshop.network.data.DataType;
import engineers.workshop.util.Logger;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({ @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "tesla"),
		@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla"), })
public class TileTable extends TileEntity
		implements IInventory, ISidedInventory, IFluidHandler, ITickable, /* TESLA */ ITeslaHolder, ITeslaConsumer {

	private List<Page> pages;
	private Page selectedPage;
	private List<SlotBase> slots;
	private ItemStack[] items;

	private GuiMenu menu;

	private int power;
	public int max_power = 2000;
	public int max_lava = 1000;
	private SlotFuel fuelSlot;

	public int getPower() {
		return power;
	}
	
	public int getMaxPower(){
		return max_power;
	}
	
	public int getMaxLava(){
		return max_lava;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public TileTable() {
	pages = new ArrayList<Page>();
	pages.add(new PageMain(this, "main"));
	pages.add(new PageTransfer(this, "transfer"));
	pages.add(new PageUpgrades(this, "upgrade"));

	slots = new ArrayList<SlotBase>();
	int id = 0;
	addSlot(fuelSlot = new SlotFuel(this, null, id++, 226, 226));
	for (Page page : pages) {
		id = page.createSlots(id);
	}
	items = new ItemStack[slots.size()];

	setSelectedPage(pages.get(0));
	onUpgradeChange();
}

	public List<SlotBase> getSlots() {
		return slots;
	}

	public List<Page> getPages() {
		return pages;
	}

	public Page getSelectedPage() {
		return selectedPage;
	}

	public void setSelectedPage(Page selectedPage) {
		this.selectedPage = selectedPage;
	}

	public ItemStack[] getItems() {
		return items;
	}

	@Override
	public int getSizeInventory() {
		return items.length;
	}

	public PageMain getMainPage() {
		return (PageMain) pages.get(0);
	}

	public PageTransfer getTransferPage() {
		return (PageTransfer) pages.get(1);
	}

	public PageUpgrades getUpgradePage() {
		return (PageUpgrades) pages.get(2);
	}

	@Override
	public ItemStack getStackInSlot(int id) {
		return items[id];
	}

	@Override
	public ItemStack decrStackSize(int id, int count) {
		ItemStack item = getStackInSlot(id);
		if (item != null) {
			if (item.stackSize <= count) {
				setInventorySlotContents(id, null);
				return item;
			}

			ItemStack result = item.splitStack(count);

			if (item.stackSize == 0) {
				setInventorySlotContents(id, null);
			}
			return result;
		} else {
			return null;
		}
	}

	public ItemStack getStackInSlotOnClosing(int id) {
		if (slots.get(id).shouldDropOnClosing()) {
			ItemStack item = getStackInSlot(id);
			setInventorySlotContents(id, null);
			return item;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int id, ItemStack item) {
		items[id] = item;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64;
	}

	public void addSlot(SlotBase slot) {
		slots.add(slot);
	}

	private List<EntityPlayer> players = new ArrayList<EntityPlayer>();

	public void addPlayer(EntityPlayer player) {
		Logger.infof("Trying to add player %s", player.getName());
		if (!players.contains(player)) {
			players.add(player);
			sendAllDataToPlayer(player);
		} else {
			Logger.error("Trying to add a listening player: " + player.getName());
		}
	}
	
	public void removePlayer(EntityPlayer player) {
		Logger.infof("Trying to remove player %s", player.getName());
		if (!players.remove(player)) {
			Logger.error("Trying to remove non-listening player: " + player.getName());
		}
	}

	private void sendAllDataToPlayer(EntityPlayer player) {
		DataWriter dw = PacketHandler.getWriter(this, PacketId.ALL);
		for (DataType dataType : DataType.values()) {
			dataType.save(this, dw, -1);
		}
		PacketHandler.sendToPlayer(dw, player);
	}
	
	public void sendDataToAllPlayer(DataType dataType) {
		sendDataToAllPlayer(dataType, 0);
	}

	public void sendDataToAllPlayer(DataType dataType, int id) {
		sendToAllPlayers(getWriterForType(dataType, id));
	}

	private void sendDataToAllPlayersExcept(DataType dataType, int id, EntityPlayer ignored) {
		sendToAllPlayersExcept(getWriterForType(dataType, id), ignored);
	}

	private void sendToAllPlayers(DataWriter dw) {
		sendToAllPlayersExcept(dw, null);
	}

	private void sendToAllPlayersExcept(DataWriter dw, EntityPlayer ignored) {
		for (EntityPlayer player : players) {
			if (!player.equals(ignored)) {
				PacketHandler.sendToPlayer(dw, player);
			}
		}
	}

	public void updateServer(DataType dataType) {
		updateServer(dataType, 0);
	}

	public void updateServer(DataType dataType, int id) {
		PacketHandler.sendToServer(getWriterForType(dataType, id));
	}

	private DataWriter getWriterForType(DataType dataType, int id) {
		DataWriter dw = PacketHandler.getWriter(this, PacketId.TYPE);
		dw.writeEnum(dataType);
		dataType.save(this, dw, id);

		return dw;
	}

	public void receiveServerPacket(DataReader dr, PacketId id, EntityPlayer player) {
		switch (id) {
		case TYPE:
			DataType dataType = dr.readEnum(DataType.class);
			int index = dataType.load(this, dr, false);
			if (index != -1 && dataType.shouldBounce(this)) {
				sendDataToAllPlayersExcept(dataType, index, dataType.shouldBounceToAll(this) ? null : player);
			}
			if (dataType == DataType.SIDE_ENABLED) {
				onSideChange();
			}
			markDirty();
			break;
		case CLOSE:
			removePlayer(player);
			break;
		case RE_OPEN:
			addPlayer(player);
			break;
		case CLEAR:
			clearGrid(player, dr.readData(GRID_ID_BITS));
			break;
		case ALL:
			break;
		case UPGRADE_CHANGE:
			onUpgradeChange();
			break;
		default:
			break;
		}
	}

	public void receiveClientPacket(DataReader dr, PacketId id) {
		switch (id) {
		case ALL:
			for (DataType dataType : DataType.values()) {
				dataType.load(this, dr, true);
			}
			onUpgradeChange();
			break;
		case TYPE:
			DataType dataType = dr.readEnum(DataType.class);
			dataType.load(this, dr, false);
			if (dataType == DataType.SIDE_ENABLED) {
				onSideChange();
			}
			break;
		case UPGRADE_CHANGE:
			onUpgradeChange();
			break;
		default:
			break;
		}
	}

	private int fuelTick = 0;
	private static final int FUEL_DELAY = 15;
	private int moveTick = 0;
	private static final int MOVE_DELAY = 20;
	private boolean lit;
	private boolean lastLit;
	private int slotTick = 0;
	private static final int SLOT_DELAY = 10;

	@Override
	public void update() {
		for (Page page : pages) {
			page.onUpdate();
		}

		if (!worldObj.isRemote && ++fuelTick >= FUEL_DELAY) {
			lit = worldObj.getBlockLightOpacity(pos) == 15;
			if (lastLit != lit) {
				lastLit = lit;
				sendDataToAllPlayer(DataType.LIT);
			}
			fuelTick = 0;
			reloadFuel();
		}

		if (!worldObj.isRemote && ++moveTick >= MOVE_DELAY) {
			moveTick = 0;
			if (getUpgradePage().hasGlobalUpgrade(Upgrade.AUTO_TRANSFER)) {
				int transferSize = (int) Math.pow(2, getUpgradePage().getGlobalUpgradeCount(Upgrade.TRANSFER));
				for (Setting setting : getTransferPage().getSettings()) {
					for (Side side : setting.getSides()) {
						transfer(setting, side, side.getInput(), transferSize);
						transfer(setting, side, side.getOutput(), transferSize);
					}
				}
			}
		}

		if (!worldObj.isRemote && ++slotTick >= SLOT_DELAY) {
			slotTick = 0;
			for (SlotBase slot : slots) {
				slot.updateServer();
			}
		}
	}

	private void transfer(Setting setting, Side side, Transfer transfer, int transferSize) {
		if (transfer.isEnabled() && transfer.isAuto()) {
			EnumFacing direction = EnumFacing.values()[BlockTable
					.getSideFromSideAndMetaReversed(side.getDirection().ordinal(), getBlockMetadata())];
			TileEntity te = worldObj.getTileEntity(new BlockPos(pos.getX() + direction.getFrontOffsetX(),
					pos.getY() + direction.getFrontOffsetY(), pos.getX() + direction.getFrontOffsetZ()));
			if (te instanceof IInventory) {
				IInventory inventory = null;
				if (te instanceof TileEntityChest) {
					// inventory = Blocks.CHEST.func_149951_m(te.getWorld(),
					// te.getPos().getX(), te.getPos().getY(),
					// te.getPos().getX());
					if (inventory == null) {
						return;
					}
				} else {
					inventory = (IInventory) te;
				}
				List<SlotBase> transferSlots = setting.getSlots();
				if (transferSlots == null) {
					return;
				}
				int[] slots1 = new int[transferSlots.size()];
				for (int i = 0; i < transferSlots.size(); i++) {
					slots1[i] = transferSlots.get(i).getSlotIndex();
				}
				int[] slots2;
				EnumFacing directionReversed = direction.getOpposite();
				if (inventory instanceof ISidedInventory) {
					slots2 = ((TileTable) inventory).getSlotsForFace(directionReversed);
				} else {
					slots2 = new int[inventory.getSizeInventory()];
					for (int i = 0; i < slots2.length; i++) {
						slots2[i] = i;
					}
				}
				if (slots2 == null || slots2.length == 0) {
					return;
				}
				if (transfer.isInput()) {
					transfer(inventory, (IInventory) this, slots2, slots1, directionReversed, direction, transferSize);
				} else {
					transfer(this, inventory, slots1, slots2, direction, directionReversed, transferSize);
				}
			}
		}
	}

	private void transfer(IInventory from, IInventory to, int[] fromSlots, int[] toSlots, EnumFacing fromSide,
			EnumFacing toSide, int maxTransfer) {
		int oldTransfer = maxTransfer;

		try {
			ISidedInventory fromSided = fromSide.ordinal() != -1 && from instanceof ISidedInventory
					? (ISidedInventory) from : null;
			ISidedInventory toSided = toSide.ordinal() != -1 && to instanceof ISidedInventory ? (ISidedInventory) to
					: null;

			for (int fromSlot : fromSlots) {
				ItemStack fromItem = from.getStackInSlot(fromSlot);
				if (fromItem != null && fromItem.stackSize > 0) {
					if (fromSided == null || fromSided.canExtractItem(fromSlot, fromItem, fromSide)) {
						if (fromItem.isStackable()) {
							for (int toSlot : toSlots) {
								ItemStack toItem = to.getStackInSlot(toSlot);
								if (toItem != null && toItem.stackSize > 0) {
									if (toSided == null || toSided.canInsertItem(toSlot, fromItem, toSide)) {
										if (fromItem.isItemEqual(toItem)
												&& ItemStack.areItemStackTagsEqual(toItem, fromItem)) {
											int maxSize = Math.min(toItem.getMaxStackSize(),
													to.getInventoryStackLimit());
											int maxMove = Math.min(maxSize - toItem.stackSize,
													Math.min(maxTransfer, fromItem.stackSize));

											toItem.stackSize += maxMove;
											maxTransfer -= maxMove;
											fromItem.stackSize -= maxMove;
											if (fromItem.stackSize == 0) {
												from.setInventorySlotContents(fromSlot, null);
											}

											if (maxTransfer == 0) {
												return;
											} else if (fromItem.stackSize == 0) {
												break;
											}
										}
									}
								}
							}
						}
						if (fromItem.stackSize > 0) {
							for (int toSlot : toSlots) {
								ItemStack toItem = to.getStackInSlot(toSlot);
								if (toItem == null && to.isItemValidForSlot(toSlot, fromItem)) {
									if (toSided == null || toSided.canInsertItem(toSlot, fromItem, toSide)) {
										toItem = fromItem.copy();
										toItem.stackSize = Math.min(maxTransfer, fromItem.stackSize);
										to.setInventorySlotContents(toSlot, toItem);
										maxTransfer -= toItem.stackSize;
										fromItem.stackSize -= toItem.stackSize;

										if (fromItem.stackSize == 0) {
											from.setInventorySlotContents(fromSlot, null);
										}

										if (maxTransfer == 0) {
											return;
										} else if (fromItem.stackSize == 0) {
											break;
										}
									}
								}
							}
						}
					}
				}

			}
		} finally {
			if (oldTransfer != maxTransfer) {
				to.markDirty();
				from.markDirty();
			}
		}
	}

	private int lava;
	//TODO Upgrades?
	public static final int MAX_LAVA = 1000;
	private static final int MAX_LAVA_DRAIN = 50;
	private static final int LAVA_EFFICIENCY = 12;
	private static final int SOLAR_GENERATION = 4;

	private int lastPower;
	private int lastLava;

	private void reloadFuel() {

		if (isLitAndCanSeeTheSky()) {
			power += SOLAR_GENERATION * getUpgradePage().getGlobalUpgradeCount(Upgrade.SOLAR);
		}

		if (getUpgradePage().hasGlobalUpgrade(Upgrade.LAVA)) {
			int space = (max_power - power) / LAVA_EFFICIENCY;
			if (space > 0) {
				int move = Math.max(0, Math.min(MAX_LAVA_DRAIN, Math.min(space, lava)));
				power += move * LAVA_EFFICIENCY;
				lava -= move;
			}
		}

		ItemStack fuel = fuelSlot.getStack();
		if (fuel != null && fuelSlot.isItemValid(fuel)) {
			int fuelLevel = TileEntityFurnace.getItemBurnTime(fuel);
			fuelLevel *= 1 + getUpgradePage().getGlobalUpgradeCount(Upgrade.EFFICIENCY) / 4F;

			if (fuelLevel > 0 && fuelLevel + power <= max_power) {
				power += fuelLevel;
				if (fuel.getItem().hasContainerItem(fuel)) {
					fuelSlot.putStack(fuel.getItem().getContainerItem(fuel).copy());
				} else {
					decrStackSize(fuelSlot.getSlotIndex(), 1);
				}
			}
		}
		if (power > max_power) {
			power = max_power;
		}

		if (power != lastPower) {
			lastPower = power;
		}
		sendDataToAllPlayer(DataType.POWER);
		if (lava != lastLava) {
			lastLava = lava;
		}
		sendDataToAllPlayer(DataType.LAVA);
	}

	public boolean isLitAndCanSeeTheSky() {
		return lit && worldObj.canBlockSeeSky(pos);
	}

	public void onUpgradeChangeDistribute() {
		if (!worldObj.isRemote) {
			onUpgradeChange();
			sendToAllPlayers(PacketHandler.getWriter(this, PacketId.UPGRADE_CHANGE));
		} else {
			getUpgradePage().onUpgradeChange();
		}
	}

	public void onUpgradeChange() {
		reloadTransferSides();
		getUpgradePage().onUpgradeChange();
		for (UnitCrafting crafting : getMainPage().getCraftingList()) {
			crafting.onUpgradeChange();
		}
	}

	public void onSideChange() {
		reloadTransferSides();
	}

	private void reloadTransferSides() {
		for (int i = 0; i < sideSlots.length; i++) {
			for (SlotBase slot : slots) {
				slot.resetValidity(i);
			}

			List<SlotBase> slotsForSide = new ArrayList<SlotBase>();

			for (Setting setting : getTransferPage().getSettings()) {
				Transfer input = setting.getSides().get(i).getInput();
				Transfer output = setting.getSides().get(i).getOutput();

				if (input.isEnabled() || output.isEnabled()) {
					List<SlotBase> unitSlots = setting.getSlots();
					if (unitSlots != null) {
						slotsForSide.addAll(unitSlots);
						for (SlotBase unitSlot : unitSlots) {
							boolean isSlotInput = input.isEnabled() && unitSlot.canAcceptItems();
							boolean isSlotOutput = output.isEnabled() && unitSlot.canSupplyItems();

							unitSlot.setValidity(i, isSlotInput ? input : null, isSlotOutput ? output : null);
						}
					}
				}
			}
			sideSlots[i] = getSlotIndexArray(slotsForSide);
		}
	}

	private int[] getSlotIndexArray(List<SlotBase> slots) {
		int[] result = new int[slots.size()];
		for (int j = 0; j < slots.size(); j++) {
			result[j] = slots.get(j).getSlotIndex();
		}
		return result;
	}

	private int[][] sideSlots = new int[6][];

	@Override
	public boolean isItemValidForSlot(int id, ItemStack item) {
		return slots.get(id).isItemValid(item);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, EnumFacing side) {
		return isItemValidForSlot(slot, item) && slots.get(slot).canAcceptItem(item)
				&& slots.get(slot).isInputValid(getTransferSide(side), item);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, EnumFacing side) {
		return slots.get(slot).isOutputValid(getTransferSide(side), item);
	}

	private int getTransferSide(EnumFacing side) {
		return BlockTable.getSideFromSideAndMeta(side.ordinal(), getBlockMetadata());
	}

	public GuiMenu getMenu() {
		return menu;
	}

	public void setMenu(GuiMenuItem menu) {
		this.menu = menu;
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if (resource != null && resource.getFluid() != null && resource.getFluid().equals(FluidRegistry.LAVA)) {
			int space = MAX_LAVA - lava;
			int fill = Math.min(space, resource.amount);
			if (doFill) {
				lava += fill;
			}
			return fill;
		}
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if (resource != null && resource.getFluid() != null && resource.getFluid().equals(FluidRegistry.LAVA)) {
			return drain(from, resource.amount, doDrain);
		} else {
			return null;
		}
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		int drain = Math.min(maxDrain, lava);
		if (doDrain) {
			lava -= drain;
		}

		return drain == 0 ? null : new FluidStack(FluidRegistry.LAVA, drain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return fluid != null && fluid.equals(FluidRegistry.LAVA);
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return fluid != null && fluid.equals(FluidRegistry.LAVA);
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[] { new FluidTankInfo(new FluidStack(FluidRegistry.LAVA, lava), MAX_LAVA) };
	}

	public int getLava() {
		return lava;
	}

	public void setLava(int lava) {
		this.lava = lava;
	}

	public boolean isLit() {
		return lit;
	}

	public void setLit(boolean lit) {
		this.lit = lit;
	}

	private static final String NBT_ITEMS = "Items";
	private static final String NBT_UNITS = "Units";
	private static final String NBT_SETTINGS = "Settings";
	private static final String NBT_SIDES = "Sides";
	private static final String NBT_INPUT = "Input";
	private static final String NBT_OUTPUT = "Output";
	private static final String NBT_SLOT = "Slot";
	private static final String NBT_POWER = "Power";
	private static final String NBT_LAVA = "LavaLevel";
	private static final String NBT_TESLA = "Tesla";
	private static final int COMPOUND_ID = 10;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				NBTTagCompound slotCompound = new NBTTagCompound();
				slotCompound.setByte(NBT_SLOT, (byte) i);
				items[i].writeToNBT(slotCompound);
				itemList.appendTag(slotCompound);
			}
		}
		compound.setTag(NBT_ITEMS, itemList);

		NBTTagList unitList = new NBTTagList();
		for (Unit unit : getMainPage().getUnits()) {
			NBTTagCompound unitCompound = new NBTTagCompound();
			unit.writeToNBT(unitCompound);
			unitList.appendTag(unitCompound);
		}
		compound.setTag(NBT_UNITS, unitList);

		NBTTagList settingList = new NBTTagList();
		for (Setting setting : getTransferPage().getSettings()) {
			NBTTagCompound settingCompound = new NBTTagCompound();

			NBTTagList sideList = new NBTTagList();
			for (Side side : setting.getSides()) {
				NBTTagCompound sideCompound = new NBTTagCompound();
				NBTTagCompound inputCompound = new NBTTagCompound();
				NBTTagCompound outputCompound = new NBTTagCompound();

				side.getInput().writeToNBT(inputCompound);
				side.getOutput().writeToNBT(outputCompound);

				sideCompound.setTag(NBT_INPUT, inputCompound);
				sideCompound.setTag(NBT_OUTPUT, outputCompound);
				sideList.appendTag(sideCompound);
			}
			settingCompound.setTag(NBT_SIDES, sideList);
			settingList.appendTag(settingCompound);
		}
		compound.setTag(NBT_SETTINGS, settingList);
		compound.setShort(NBT_POWER, (short) power);
		compound.setShort(NBT_LAVA, (byte) lava);

		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		items = new ItemStack[getSizeInventory()];

		NBTTagList itemList = compound.getTagList(NBT_ITEMS, COMPOUND_ID);
		for (int i = 0; i < itemList.tagCount(); i++) {
			NBTTagCompound slotCompound = itemList.getCompoundTagAt(i);
			int id = slotCompound.getByte(NBT_SLOT);
			if (id < 0) {
				id += 256;
			}

			if (id >= 0 && id < items.length) {
				items[id] = ItemStack.loadItemStackFromNBT(slotCompound);
			}
		}

		NBTTagList unitList = compound.getTagList(NBT_UNITS, COMPOUND_ID);
		List<Unit> units = getMainPage().getUnits();
		for (int i = 0; i < units.size(); i++) {
			Unit unit = units.get(i);
			NBTTagCompound unitCompound = unitList.getCompoundTagAt(i);
			unit.readFromNBT(unitCompound);
		}

		NBTTagList settingList = compound.getTagList(NBT_SETTINGS, COMPOUND_ID);
		List<Setting> settings = getTransferPage().getSettings();
		for (int i = 0; i < settings.size(); i++) {
			Setting setting = settings.get(i);
			NBTTagCompound settingCompound = settingList.getCompoundTagAt(i);
			NBTTagList sideList = settingCompound.getTagList(NBT_SIDES, COMPOUND_ID);
			List<Side> sides = setting.getSides();
			for (int j = 0; j < sides.size(); j++) {
				Side side = sides.get(j);
				NBTTagCompound sideCompound = sideList.getCompoundTagAt(j);
				NBTTagCompound inputCompound = sideCompound.getCompoundTag(NBT_INPUT);
				NBTTagCompound outputCompound = sideCompound.getCompoundTag(NBT_OUTPUT);

				side.getInput().readFromNBT(inputCompound);
				side.getOutput().readFromNBT(outputCompound);
			}
		}
		power = compound.getShort(NBT_POWER);
		lava = compound.getShort(NBT_LAVA);

		onUpgradeChangeDistribute();
		onSideChange();
		onUpgradeChange();
	}

	public void spitOutItem(ItemStack item) {
		float offsetX, offsetY, offsetZ;
		offsetX = offsetY = offsetZ = worldObj.rand.nextFloat() * 0.8F + 1.0F;

		EntityItem entityItem = new EntityItem(worldObj, pos.getX() + offsetX, pos.getY() + offsetY,
				pos.getZ() + offsetZ, item.copy());
		entityItem.motionX = worldObj.rand.nextGaussian() * 0.05F;
		entityItem.motionY = worldObj.rand.nextGaussian() * 0.05F + 0.2F;
		entityItem.motionZ = worldObj.rand.nextGaussian() * 0.05F;

		worldObj.spawnEntityInWorld(entityItem);
	}

	private static final IBitCount GRID_ID_BITS = new LengthCount(4);

	public void clearGridSend(int id) {
		DataWriter dw = PacketHandler.getWriter(this, PacketId.CLEAR);
		dw.writeData(id, GRID_ID_BITS);
		PacketHandler.sendToServer(dw);
	}

	private void clearGrid(EntityPlayer player, int id) {
		UnitCrafting crafting = getMainPage().getCraftingList().get(id);
		if (crafting.isEnabled()) {
			int[] from = new int[9];
			for (int i = 0; i < from.length; i++) {
				from[i] = crafting.getGridId() + i;
			}
			int[] to = new int[player.inventory.mainInventory.length];
			for (int i = 0; i < to.length; i++) {
				to[i] = i;
			}
			transfer(this, player.inventory, from, to, EnumFacing.UP, EnumFacing.UP, Integer.MAX_VALUE);
		}
	}

	@Override
	public String getName() {
		return "Production Table";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return sideSlots[getTransferSide(side)];
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return slots.get(index).getStack().copy();
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	// Tesla
	@Override
	public long getStoredPower() {
		return power;
	}

	@Override
	public long getCapacity() {
		return max_power;
	}

	@Override
	public long givePower(long power, boolean simulated) {
		long rValue = Math.min(getCapacity() - getStoredPower(), power);
		setPower((int) rValue + getPower());
		return rValue;
	}

	@Optional.Method(modid = "tesla")
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return ((capability == TeslaCapabilities.CAPABILITY_HOLDER
				|| capability == TeslaCapabilities.CAPABILITY_CONSUMER)
				&& getUpgradePage().getGlobalUpgradeCount(Upgrade.TESLA) > 0) ? true
						: super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Optional.Method(modid = "tesla")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (getUpgradePage().getGlobalUpgradeCount(Upgrade.TESLA) > 0) {
			if (capability == TeslaCapabilities.CAPABILITY_HOLDER)
				return (T) this;
			if (capability == TeslaCapabilities.CAPABILITY_CONSUMER)
				return (T) this;
		}
		return super.getCapability(capability, facing);
	}

}
