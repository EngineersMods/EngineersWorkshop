package engineers.workshop.common.table;

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
import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.network.*;
import engineers.workshop.common.network.data.DataType;
import engineers.workshop.common.register.Register;
import engineers.workshop.common.unit.Unit;
import engineers.workshop.common.unit.UnitCraft;
import engineers.workshop.common.util.Logger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileTable extends TileEntity implements IInventory, ISidedInventory, ITickable {

	private static final int MOVE_DELAY = 20;
	private static final int SLOT_DELAY = 10;
	private static final String NBT_ITEMS = "item";
	private static final String NBT_UNITS = "units";
	private static final String NBT_SETTINGS = "settings";
	private static final String NBT_SIDES = "sides";
	private static final String NBT_INPUT = "input";
	private static final String NBT_OUTPUT = "output";
	private static final String NBT_SLOT = "slot";
	private static final String NBT_POWER = "fuel";
	private static final String NBT_MAX_POWER = "max_power";
	private static final int COMPOUND_ID = 10;
	private static final IBitCount GRID_ID_BITS = new LengthCount(4);
	public int maxFuel = 8000;
	private List<Page> pages;
	private Page selectedPage;
	private List<SlotBase> slots;
	private NonNullList<ItemStack> items;
	private GuiMenu menu;
	private int fuel;
	private SlotFuel fuelSlot;
	private List<EntityPlayer> players = new ArrayList<>();
	private int fuelTick = 0;
	private int moveTick = 0;
	private boolean lit;
	private boolean lastLit;
	private int slotTick = 0;
	private boolean firstUpdate = true;
	private int tickCount = 0;
	private int[][] sideSlots = new int[6][];

	public TileTable() {
		pages = new ArrayList<>();
		pages.add(new PageMain(this, "main"));
		pages.add(new PageTransfer(this, "transfer"));
		pages.add(new PageUpgrades(this, "upgrade"));
		// pages.add(new PageSecurity(this, "security"));

		slots = new ArrayList<>();
		int id = 0;
		addSlot(fuelSlot = new SlotFuel(this, null, id++, 226, 226));
		for (Page page : pages) {
			id = page.createSlots(id);
		}
		items = NonNullList.withSize(slots.size(), ItemStack.EMPTY);
		setSelectedPage(pages.get(0));
		onUpgradeChange();
	}

	public int getFuel() {
		return fuel;
	}

	public void setFuel(int fuel) {
		this.fuel = fuel;
	}

	public void setCapacity(int newCap) {
		this.maxFuel = newCap;
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

	public NonNullList<ItemStack> getItems() {
		return items;
	}

	@Override
	public int getSizeInventory() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : items) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
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
	@Nonnull
	public ItemStack getStackInSlot(int id) {
		return items.get(id);
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int id, int count) {
		ItemStack item = getStackInSlot(id);
		if (!item.isEmpty()) {
			if (item.getCount() <= count) {
				setInventorySlotContents(id, ItemStack.EMPTY);
				return item;
			}
			return item.splitStack(count);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Nonnull
	public ItemStack getStackInSlotOnClosing(int id) {
		if (slots.get(id).shouldDropOnClosing()) {
			ItemStack item = getStackInSlot(id);
			setInventorySlotContents(id, ItemStack.EMPTY);
			return item;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setInventorySlotContents(int id,
	                                     @Nonnull
		                                     ItemStack item) {
		items.set(id, item);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64;
	}

	public void addSlot(SlotBase slot) {
		slots.add(slot);
	}

	public List<EntityPlayer> getOpenPlayers() {
		return players;
	}

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
		DataPacket packet = PacketHandler.getPacket(this, PacketId.ALL);
		for (DataType dataType : DataType.values()) {
			if (dataType != null && this != null && packet != null)
				dataType.save(this, packet.createCompound(), -1);
		}
		PacketHandler.sendToPlayer(packet, player);
	}

	private void sendDataToPlayer(DataType type, EntityPlayer player) {
		DataPacket packet = PacketHandler.getPacket(this, PacketId.RENDER_UPDATE);
		type.save(this, packet.createCompound(), -1);
		PacketHandler.sendToPlayer(packet, player);
	}

	public void sendDataToAllPlayers(DataType dataType, List<EntityPlayer> players) {
		sendDataToAllPlayers(dataType, 0, players);
	}

	public void sendDataToAllPlayers(DataType dataType, int id, List<EntityPlayer> players) {
		sendToAllPlayers(getWriterForType(dataType, id), players);
	}

	private void sendDataToAllPlayersExcept(DataType dataType, int id, EntityPlayer ignored,
	                                        List<EntityPlayer> players) {
		sendToAllPlayersExcept(getWriterForType(dataType, id), ignored, players);
	}

	private void sendToAllPlayers(DataPacket dw, List<EntityPlayer> players) {
		sendToAllPlayersExcept(dw, null, players);
	}

	private void sendToAllPlayersExcept(DataPacket dw, EntityPlayer ignored, List<EntityPlayer> players) {
		players.stream().filter(player -> !player.equals(ignored))
			.forEach(player -> PacketHandler.sendToPlayer(dw, player));
	}

	public void updateServer(DataType dataType) {
		updateServer(dataType, 0);
	}

	public void updateServer(DataType dataType, int id) {
		PacketHandler.sendToServer(getWriterForType(dataType, id));
	}

	private DataPacket getWriterForType(DataType dataType, int id) {
		DataPacket packet = PacketHandler.getPacket(this, PacketId.TYPE);
		packet.dataType = dataType;
		dataType.save(this, packet.createCompound(), id);
		return packet;
	}

	public void receiveServerPacket(DataPacket dr, PacketId id, EntityPlayer player) {
		switch (id) {
			case TYPE:
				DataType dataType = dr.dataType;
				int index = dataType.load(this, dr.compound, false);
				if (index != -1 && dataType.shouldBounce(this)) {
					sendDataToAllPlayersExcept(dataType, index, dataType.shouldBounceToAll(this) ? null : player, players);
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
				clearGrid(player, dr.compound.getInteger("clear"));
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

	public void receiveClientPacket(DataPacket dr, PacketId id) {
		switch (id) {
			case ALL:
				for (DataType dataType : DataType.values()) {
					dataType.load(this, dr.compound, true);
				}
				onUpgradeChange();
				break;
			case TYPE:
				DataType dataType = dr.dataType;
				dataType.load(this, dr.compound, false);
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

	@Override
	public void update() {
		tickCount++;
		pages.forEach(Page::onUpdate);

		if (firstUpdate) {
			onUpgradeChangeDistribute();
			onSideChange();
			onUpgradeChange();
			firstUpdate = false;
		}

		if (!world.isRemote && ++moveTick >= MOVE_DELAY) {
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

		if (!world.isRemote && ++slotTick >= SLOT_DELAY) {
			slotTick = 0;
			slots.stream().filter(SlotBase::isEnabled).forEach(SlotBase::updateServer);
		}

		if (!world.isRemote) {
			if (tickCount % 20 == 0) {
				int x1 = getPos().getX() - 16;
				int x2 = getPos().getX() + 16;
				int z1 = getPos().getY() - 16;
				int z2 = getPos().getY() + 16;
				AxisAlignedBB aabb = new AxisAlignedBB(x1, 0, z1, x2, 255, z2);
				List<EntityPlayer> updatePlayers = world.getEntitiesWithinAABB(EntityPlayerMP.class, aabb);
				updatePlayers.removeAll(players);
			}
			updateFuel();
		}
	}

	private void transfer(Setting setting, Side side, Transfer transfer, int transferSize) {
		if (transfer.isEnabled() && transfer.isAuto()) {
			EnumFacing direction = side.getDirection();
			BlockPos nPos = pos.add(direction.getFrontOffsetX(), direction.getFrontOffsetY(),
				direction.getFrontOffsetZ());
			TileEntity te = world.getTileEntity(nPos);
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
				if (!fromItem.isEmpty() && fromItem.getCount() > 0) {
					if (fromSided == null || fromSided.canExtractItem(fromSlot, fromItem, fromSide)) {
						if (fromItem.isStackable()) {
							for (int toSlot : toSlots) {
								ItemStack toItem = to.getStackInSlot(toSlot);
								if (!toItem.isEmpty() && toItem.getCount() > 0) {
									if (toSided == null || toSided.canInsertItem(toSlot, fromItem, toSide)) {
										if (fromItem.isItemEqual(toItem)
											&& ItemStack.areItemStackTagsEqual(toItem, fromItem)) {
											int maxSize = Math.min(toItem.getMaxStackSize(),
												to.getInventoryStackLimit());
											int maxMove = Math.min(maxSize - toItem.getCount(),
												Math.min(maxTransfer, fromItem.getCount()));
											toItem.grow(maxMove);
											maxTransfer -= maxMove;
											fromItem.shrink(maxMove);

											if (maxTransfer == 0) {
												return;
											} else if (fromItem.isEmpty()) {
												break;
											}
										}
									}
								}
							}
						}
						if (fromItem.getCount() > 0) {
							for (int toSlot : toSlots) {
								ItemStack toItem = to.getStackInSlot(toSlot);
								if (toItem.isEmpty() && to.isItemValidForSlot(toSlot, fromItem)) {
									if (toSided == null || toSided.canInsertItem(toSlot, fromItem, toSide)) {
										toItem = fromItem.copy();
										toItem.setCount(Math.min(maxTransfer, fromItem.getCount()));
										to.setInventorySlotContents(toSlot, toItem);
										maxTransfer -= toItem.getCount();
										fromItem.shrink(toItem.getCount());

										if (maxTransfer == 0) {
											return;
										} else if (fromItem.isEmpty()) {
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

	private void updateFuel() {
		if (lastLit != lit) {
			lastLit = lit;
			sendDataToAllPlayers(DataType.LIT, players);
		}

		ItemStack fuel = fuelSlot.getStack();
		if (!fuel.isEmpty() && fuelSlot.isItemValid(fuel)) {
			int fuelLevel = TileEntityFurnace.getItemBurnTime(fuel);
			if (fuelLevel > 0 && fuelLevel + this.fuel <= maxFuel) {
				this.fuel += fuelLevel;
				if (fuel.getItem().hasContainerItem(fuel)) {
					fuelSlot.putStack(fuel.getItem().getContainerItem(fuel).copy());
				} else {
					decrStackSize(fuelSlot.getSlotIndex(), 1);
				}
			}
		}

		if (this.fuel > maxFuel)
			this.fuel = maxFuel;
	}

	public void onUpgradeChangeDistribute() {
		if (!world.isRemote) {
			onUpgradeChange();
			world.notifyNeighborsOfStateChange(pos, Register.Blocks.table, true);
			sendToAllPlayers(PacketHandler.getPacket(this, PacketId.UPGRADE_CHANGE), players);
		} else {
			getUpgradePage().onUpgradeChange();
		}
	}

	public void onUpgradeChange() {
		reloadTransferSides();
		getUpgradePage().onUpgradeChange();
		getMainPage().getCraftingList().forEach(UnitCraft::onUpgradeChange);
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

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new SPacketUpdateTileEntity(getPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		this.readFromNBT(packet.getNbtCompound());
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger(NBT_POWER, fuel);
		compound.setInteger(NBT_MAX_POWER, maxFuel);

		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < items.size(); i++) {
			if (!items.get(i).isEmpty()) {
				NBTTagCompound slotTag = items.get(i).writeToNBT(new NBTTagCompound());
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
		fuel = compound.getInteger(NBT_POWER);
		maxFuel = compound.getInteger(NBT_MAX_POWER);

		items = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);

		NBTTagList itemList = compound.getTagList(NBT_ITEMS, COMPOUND_ID);
		for (int i = 0; i < itemList.tagCount(); i++) {
			NBTTagCompound slotCompound = itemList.getCompoundTagAt(i);
			int id = slotCompound.getInteger(NBT_SLOT);
			if (id < 0) {
				id += 256;
			}
			if (id >= 0 && id < items.size()) {
				items.set(id, new ItemStack(slotCompound));
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
		offsetX = offsetY = offsetZ = world.rand.nextFloat() * 0.8F + 1.0F;

		EntityItem entityItem = new EntityItem(world, pos.getX() + offsetX, pos.getY() + offsetY,
			pos.getZ() + offsetZ, item.copy());
		entityItem.motionX = world.rand.nextGaussian() * 0.05F;
		entityItem.motionY = world.rand.nextGaussian() * 0.05F + 0.2F;
		entityItem.motionZ = world.rand.nextGaussian() * 0.05F;

		world.spawnEntity(entityItem);
	}

	public void clearGridSend(int id) {
		DataPacket dw = PacketHandler.getPacket(this, PacketId.CLEAR);
		dw.createCompound().setInteger("clear", id);
		PacketHandler.sendToServer(dw);
	}

	private void clearGrid(EntityPlayer player, int id) {

		UnitCraft crafting = getMainPage().getCraftingList().get(id);
		if (crafting.isEnabled()) {
			int[] from = new int[9];
			for (int i = 0; i < from.length; i++) {
				from[i] = crafting.getGridId() + i;
			}
			int[] to = new int[player.inventory.mainInventory.size()];
			for (int i = 0; i < to.length; i++) {
				to[i] = i;
			}

			for (int i = 0; i < 9; i++) {
				ItemStack fromCrafting = crafting.getSlots().get(i).getStack();
				if (!fromCrafting.isEmpty()) {
					player.inventory.addItemStackToInventory(fromCrafting);
				}
			}

			// transfer(this, player.inventory, from, to, EnumFacing.UP,
			// EnumFacing.UP, Integer.MAX_VALUE);
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return false;
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
	@Nonnull
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
}
