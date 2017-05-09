package engineers.workshop.common.table;

import cofh.api.energy.IEnergyReceiver;
import engineers.workshop.client.container.slot.SlotBase;
import engineers.workshop.client.container.slot.SlotFuel;
import engineers.workshop.client.menu.GuiMenu;
import engineers.workshop.client.menu.GuiMenuItem;
import engineers.workshop.client.page.Page;
import engineers.workshop.client.page.PageMain;
import engineers.workshop.client.page.PageTransfer;
import engineers.workshop.client.page.PageUpgrades;
import engineers.workshop.client.page.setting.Setting;
import engineers.workshop.client.page.setting.Side;
import engineers.workshop.client.page.setting.Transfer;
import engineers.workshop.client.page.unit.Unit;
import engineers.workshop.client.page.unit.UnitCraft;
import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.loaders.BlockLoader;
import engineers.workshop.common.loaders.ConfigLoader;
import engineers.workshop.common.network.*;
import engineers.workshop.common.network.data.DataType;
import engineers.workshop.common.util.Logger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class TileTable extends TileEntity implements IInventory, ISidedInventory, ITickable, /* RF */ IEnergyReceiver {

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		if (oldState.getBlock() != newSate.getBlock())
			return true;
		return false;
	}

	private List<Page> pages;
	private Page selectedPage;
	private List<SlotBase> slots;
	private ItemStack[] items;

	private GuiMenu menu;

	private int power;
	public int maxPower = ConfigLoader.TWEAKS.MIN_POWER;
	private SlotFuel fuelSlot;

	public int getPower() {
		return power;
	}

	public void setCapacity(int newCap) {
		this.maxPower = newCap;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public TileTable() {

		pages = new ArrayList<>();
		pages.add(new PageMain(this, "main"));
		pages.add(new PageTransfer(this, "transfer"));
		pages.add(new PageUpgrades(this, "upgrade"));

		slots = new ArrayList<>();
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

	private List<EntityPlayer> players = new ArrayList<>();

	public void addPlayer(EntityPlayer player) {
		Logger.debug("Trying to add player %s", player.getName());
		if (!players.contains(player)) {
			players.add(player);
			sendAllDataToPlayer(player);
		} else {
			Logger.error("Trying to add a listening player: " + player.getName());
		}
	}

	public void removePlayer(EntityPlayer player) {
		Logger.debug("Trying to remove player %s", player.getName());
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
		players.stream().filter(player -> !player.equals(ignored))
				.forEach(player -> PacketHandler.sendToPlayer(dw, player));
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
	private int moveTick = 0;
	private static final int MOVE_DELAY = 20;
	private boolean lit;
	private boolean lastLit;
	private int slotTick = 0;
	private static final int SLOT_DELAY = 10;
	private int fuelDelay;
	private boolean firstUpdate = true;

	@Override
	public void update() {
		pages.forEach(Page::onUpdate);

		if (firstUpdate) {
			onUpgradeChangeDistribute();
			onSideChange();
			onUpgradeChange();
			firstUpdate = false;
		}

		if (!worldObj.isRemote && ++fuelTick >= fuelDelay) {
			lit = worldObj.canSeeSky(pos.up());
			fuelTick = 0;
			updateFuel();
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
			// Logger.info(slots.stream().filter(SlotBase::getHasStack).filter(slot
			// -> slot instanceof SlotUpgrade)
			// .toArray(SlotUpgrade[]::new).length);
			slots.stream().filter(SlotBase::isEnabled).forEach(SlotBase::updateServer);
		}
		
	}

	private void transfer(Setting setting, Side side, Transfer transfer, int transferSize) {
		if (transfer.isEnabled() && transfer.isAuto()) {
			EnumFacing direction = side.getDirection();
			BlockPos nPos = pos.add(direction.getFrontOffsetX(), direction.getFrontOffsetY(),
					direction.getFrontOffsetZ());
			TileEntity te = worldObj.getTileEntity(nPos);
			if (te instanceof IInventory) {
				IInventory inventory = (IInventory) te;
				/*
				 * if (te instanceof TileEntityChest) { // inventory =
				 * Blocks.CHEST.func_149951_m(te.getWorld(), //
				 * te.getPos().getX(), te.getPos().getY(), //
				 * te.getPos().getX()); if (inventory == null) { return; } }
				 * else { inventory = (IInventory) te; }
				 */

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
					slots2 = ((ISidedInventory) inventory).getSlotsForFace(directionReversed);
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
					transfer(inventory, this, slots2, slots1, directionReversed, direction, transferSize);
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

	// TODO: updateFuel bookmark
	private int lastPower;

	private void updateFuel() {
		if (lastLit != lit) {
			lastLit = lit;
			sendDataToAllPlayer(DataType.LIT);
		}

		int weatherModifier;

		if (canSeeTheSky()) {
			weatherModifier = worldObj.isRaining() ? ConfigLoader.UPGRADES.SOLAR_GENERATION : 1;
			if (worldObj.isDaytime())
				power += (ConfigLoader.UPGRADES.SOLAR_GENERATION
						* getUpgradePage().getGlobalUpgradeCount(Upgrade.SOLAR)) / weatherModifier;
		}

		ItemStack fuel = fuelSlot.getStack();
		if (fuel != null && fuelSlot.isItemValid(fuel)) {
			int fuelLevel = TileEntityFurnace.getItemBurnTime(fuel);
			fuelLevel *= 1F + getUpgradePage().getGlobalUpgradeCount(Upgrade.EFFICIENCY)
					/ ConfigLoader.UPGRADES.FUEL_EFFICIENCY_CHANGE;
			if (fuelLevel > 0 && fuelLevel + power <= maxPower) {
				power += fuelLevel;
				if (fuel.getItem().hasContainerItem(fuel)) {
					fuelSlot.putStack(fuel.getItem().getContainerItem(fuel).copy());
				} else {
					decrStackSize(fuelSlot.getSlotIndex(), 1);
				}
			}
		}

		if (power > maxPower)
			power = maxPower;
		if (power != lastPower)
			lastPower = power;

		sendDataToAllPlayer(DataType.POWER);
	}

	public boolean canSeeTheSky() {
		return worldObj.canSeeSky(pos.up());
	}

	public void onUpgradeChangeDistribute() {
		if (!worldObj.isRemote) {
			onUpgradeChange();
			worldObj.notifyNeighborsOfStateChange(pos, BlockLoader.blockTable);
			sendToAllPlayers(PacketHandler.getWriter(this, PacketId.UPGRADE_CHANGE));
		} else {
			getUpgradePage().onUpgradeChange();
		}
	}

	public void onUpgradeChange() {
		reloadTransferSides();
		getUpgradePage().onUpgradeChange();
		getMainPage().getCraftingList().forEach(UnitCraft::onUpgradeChange);
		maxPower = (ConfigLoader.TWEAKS.MIN_POWER
				+ (ConfigLoader.UPGRADES.MAX_POWER_CHANGE * getUpgradePage().getGlobalUpgradeCount(Upgrade.MAX_POWER)));
		fuelDelay = (ConfigLoader.TWEAKS.FUEL_DELAY - (ConfigLoader.UPGRADES.FUEL_DELAY_CHANGE
				* getUpgradePage().getGlobalUpgradeCount(Upgrade.FUEL_DELAY)));
		sendDataToAllPlayer(DataType.POWER);
	}

	public void onSideChange() {
		reloadTransferSides();
	}

	private void reloadTransferSides() {
		for (int i = 0; i < sideSlots.length; i++) {
			for (SlotBase slot : slots) {
				slot.resetValidity(i);
			}

			List<SlotBase> slotsForSide = new ArrayList<>();

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
				&& slots.get(slot).isInputValid(side.ordinal(), item);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, EnumFacing side) {
		return slots.get(slot).isOutputValid(side.ordinal(), item);
	}

	public GuiMenu getMenu() {
		return menu;
	}

	public void setMenu(GuiMenuItem menu) {
		this.menu = menu;
	}

	public boolean isLit() {
		return lit;
	}

	public void setLit(boolean lit) {
		this.lit = lit;
	}

	private static final String NBT_ITEMS = "item";
	private static final String NBT_UNITS = "units";
	private static final String NBT_SETTINGS = "settings";
	private static final String NBT_SIDES = "sides";
	private static final String NBT_INPUT = "input";
	private static final String NBT_OUTPUT = "output";
	private static final String NBT_SLOT = "slot";
	private static final String NBT_POWER = "power";
	private static final String NBT_MAX_POWER = "max_power";
	private static final int COMPOUND_ID = 10;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger(NBT_POWER, power);
		compound.setInteger(NBT_MAX_POWER, maxPower);

		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				NBTTagCompound slotTag = items[i].writeToNBT(new NBTTagCompound());
				slotTag.setInteger(NBT_SLOT, i);
				itemList.appendTag(slotTag);
			}
		}
		compound.setTag(NBT_ITEMS, itemList);

		NBTTagList unitList = new NBTTagList();
		for (Unit unit : getMainPage().getUnits()) {
			unitList.appendTag(unit.writeToNBT(new NBTTagCompound()));
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

		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		power = compound.getInteger(NBT_POWER);
		maxPower = compound.getInteger(NBT_MAX_POWER);

		items = new ItemStack[getSizeInventory()];

		NBTTagList itemList = compound.getTagList(NBT_ITEMS, COMPOUND_ID);
		for (int i = 0; i < itemList.tagCount(); i++) {
			NBTTagCompound slotCompound = itemList.getCompoundTagAt(i);
			int id = slotCompound.getInteger(NBT_SLOT);
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

		UnitCraft crafting = getMainPage().getCraftingList().get(id);
		if (crafting.isEnabled()) {
			int[] from = new int[9];
			for (int i = 0; i < from.length; i++) {
				from[i] = crafting.getGridId() + i;
			}
			int[] to = new int[player.inventory.mainInventory.length];
			for (int i = 0; i < to.length; i++) {
				to[i] = i;
			}
			

			for (int i = 0; i < 9; i++) {
				ItemStack fromCrafting = crafting.getSlots().get(i).getStack();
				if(fromCrafting != null){
					player.inventory.addItemStackToInventory(fromCrafting);
				}
			}

			// transfer(this, player.inventory, from, to, EnumFacing.UP,
			// EnumFacing.UP, Integer.MAX_VALUE);
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
		return sideSlots[side.ordinal()];
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

	public int getStoredPower() {
		return power;
	}

	public int getCapacity() {
		return maxPower;
	}

	public int getEnergyStored(EnumFacing from) {
		return 0;
	}

	public int getMaxEnergyStored(EnumFacing from) {
		return 8000;
	}

	public boolean canConnectEnergy(EnumFacing from) {
		return getUpgradePage().hasGlobalUpgrade(Upgrade.RF);
	}

	public int receiveEnergy(EnumFacing from, int energy, boolean simulate) {
		int energyToPower = Math.min(getCapacity() - getStoredPower(), (energy / ConfigLoader.TWEAKS.POWER_CONVERSION));
		if (!simulate) power += energyToPower;

		return energyToPower * ConfigLoader.TWEAKS.POWER_CONVERSION;
	}
}
