package engineers.workshop.client.page;

import engineers.workshop.client.gui.GuiBase;
import engineers.workshop.client.gui.GuiTable;
import engineers.workshop.client.component.ArrowScroll;
import engineers.workshop.client.component.CheckBox;
import engineers.workshop.client.menu.GuiMenuItem;
import engineers.workshop.client.page.setting.*;
import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.network.data.DataSide;
import engineers.workshop.common.network.data.DataType;
import engineers.workshop.common.table.TileTable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class PageTransfer extends Page {

	private static final int SIDE_X = 75;
	private static final int SIDE_Y = 15;
	private static final int SIDE_OFFSET = 20;
	private static final int SIDE_SIZE = 18;
	private static final int SIDE_SRC_X = 0;
	private static final int SIDE_SRC_Y = 166;
	private static final int SETTING_X = 5;
	private static final int SETTING_Y = 25;
	private static final int SETTING_OFFSET = 20;
	private static final int SETTING_SIZE = 18;
	private static final int SETTING_SRC_X = 0;
	private static final int SETTING_SRC_Y = 112;
	private static final int SETTING_ITEM_OFFSET = 1;
	private static final int ITEM_X = 10;
	private static final int ITEM_Y = 125;
	private static final int ITEM_OFFSET = 20;
	private static final int ITEM_SIZE = 18;
	private static final int SIDE_ITEM_OFFSET = 1;
	protected Side selectedSide;
	private List<Setting> settings;
	private Setting selectedSetting;
	private List<CheckBox> checkBoxes;
	private List<ArrowScroll> arrows;
	private boolean selectMode;
	private Transfer selectedTransfer;
	public PageTransfer(TileTable table, String name) {
		super(table, name);
		settings = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			int x = SETTING_X + (i % 2) * SETTING_OFFSET;
			int y = SETTING_Y + (i / 2) * SETTING_OFFSET;
			settings.add(new SettingNormal(table, i, x, y));
		}

		settings.add(new SettingCoal(table, 4, SETTING_X + 2 * SETTING_OFFSET, SETTING_Y + SETTING_OFFSET / 2));

		for (Setting setting : settings) {
			for (EnumFacing direction : EnumFacing.values()) {
				setting.getSides().add(new Side(setting, direction, SIDE_X + getInterfaceX(direction) * SIDE_OFFSET, SIDE_Y + getInterfaceY(direction) * SIDE_OFFSET));
			}
		}

		checkBoxes = new ArrayList<>();
		checkBoxes.add(new CheckBox("Select mode", 165, 20) {

			@Override
			public void setValue(boolean value) {
				selectMode = value;
			}

			@Override
			public boolean getValue() {
				return selectMode;
			}

			@Override
			public void onUpdate() {
				if (!getValue()) {
					selectedSide = null;
					selectedTransfer = null;
				}
			}

			@Override
			public boolean isVisible() {
				return selectedSetting != null && shouldSelectModeBeVisible();
			}
		});

		checkBoxes.add(new CheckBox("Enabled", 170, 58) {
			@Override
			public void setValue(boolean value) {
				selectedTransfer.setEnabled(value);
				PageTransfer.this.table.updateServer(DataType.SIDE_ENABLED, DataSide.getId(selectedSetting, selectedSide, selectedTransfer));
				PageTransfer.this.table.onSideChange();
			}

			@Override
			public boolean getValue() {
				return selectedTransfer.isEnabled();
			}

			@Override
			public boolean isVisible() {
				return selectedTransfer != null;
			}
		});

		checkBoxes.add(new CheckBox("Auto transfer", 170, 68) {
			@Override
			public boolean getValue() {
				return selectedTransfer.isAuto();
			}

			@Override
			public void setValue(boolean value) {
				selectedTransfer.setAuto(value);
				PageTransfer.this.table.updateServer(DataType.SIDE_AUTO, getSyncId());
			}

			@Override
			public boolean isVisible() {
				return selectedTransfer != null
					&& PageTransfer.this.table.getUpgradePage().hasGlobalUpgrade(Upgrade.AUTO_TRANSFER);
			}
		});

		arrows = new ArrayList<>();
		arrows.add(new ArrowScroll(165, 40, 50) {
			@Override
			public String getText() {
				return selectedTransfer.isInput() ? "Input" : "Output";
			}

			@Override
			public void setId(int id) {
				selectedTransfer = id == 0 ? selectedSide.getInput() : selectedSide.getOutput();
			}

			@Override
			public int getId() {
				return selectedTransfer.isInput() ? 0 : 1;
			}

			@Override
			public boolean isVisible() {
				return selectedTransfer != null;
			}

			@Override
			protected int getLength() {
				return 2;
			}
		});

		arrows.add(new ArrowScroll(10, 112, 80) {
			@Override
			public String getText() {
				return selectedTransfer.hasWhiteList() ? "Use white list" : "Use black list";
			}

			@Override
			public int getId() {
				return selectedTransfer.hasWhiteList() ? 0 : 1;
			}

			@Override
			public void setId(int id) {
				selectedTransfer.setUseWhiteList(id == 0);
			}

			@Override
			public boolean isVisible() {
				return selectedTransfer != null
					&& PageTransfer.this.table.getUpgradePage().hasGlobalUpgrade(Upgrade.FILTER);
			}

			@Override
			public void onUpdate() {
				PageTransfer.this.table.updateServer(DataType.SIDE_WHITE_LIST, getSyncId());
			}

			@Override
			protected int getLength() {
				return 2;
			}

		});
	}

	public int getSyncId() {
		return DataSide.getId(selectedSetting, selectedSide, selectedTransfer);
	}

	public int getSyncId(ItemSetting itemSetting) {
		return DataSide.FilterBase.getId(selectedSetting, selectedSide, selectedTransfer, itemSetting);
	}

	private boolean shouldSelectModeBeVisible() {
		return table.getUpgradePage().hasGlobalUpgrade(Upgrade.AUTO_TRANSFER) || table.getUpgradePage().hasGlobalUpgrade(Upgrade.FILTER);
	}

	@Override
	public int createSlots(int id) {
		return id;
	}

	@Override
	public void draw(GuiBase gui, int mX, int mY) {
		super.draw(gui, mX, mY);

		for (Setting setting : settings) {
			gui.prepare();
			boolean isValid = setting.isValid();
			boolean isSelected = setting.equals(selectedSetting);

			if (isSelected) {
				if (!shouldSelectModeBeVisible()) {
					selectMode = false;
					selectedTransfer = null;
					selectedSide = null;
				}

				if (!isValid) {
					selectedTransfer = null;
					selectedSide = null;
					selectedSetting = null;
				}
			}

			boolean hover = gui.inBounds(setting.getX(), setting.getY(), SETTING_SIZE, SETTING_SIZE, mX, mY);
			int textureIndexX = isValid && hover ? 1 : 0;
			int textureIndexY = isValid ? isSelected ? 1 : 0 : 2;

			ItemStack item = setting.getItem();
			gui.drawRect(setting.getX(), setting.getY(), SETTING_SRC_X + textureIndexX * SETTING_SIZE, SETTING_SRC_Y + textureIndexY * SETTING_SIZE, SETTING_SIZE, SETTING_SIZE);
			gui.drawItem(item, setting.getX() + SETTING_ITEM_OFFSET, setting.getY() + SETTING_ITEM_OFFSET);

			if (hover && isValid) {
				String name = setting.getName();
				if (name == null) {
					gui.drawMouseOver(gui.getItemName(item));
				}
				gui.drawMouseOver(name);
			}
		}

		if (selectedSetting != null) {
			for (Side side : selectedSetting.getSides()) {
				gui.prepare();
				boolean hover = gui.inBounds(side.getX(), side.getY(), SIDE_SIZE, SIDE_SIZE, mX, mY);
				int textureIndexX = side.equals(selectedSide) ? 2 : hover ? 1 : 0;
				boolean output = side.isOutputEnabled();
				boolean input = side.isInputEnabled();
				int textureIndexY = output && input ? 3 : output ? 2 : input ? 1 : 0;

				gui.drawRect(side.getX(), side.getY(), SIDE_SRC_X + textureIndexX * SIDE_SIZE, SIDE_SRC_Y + textureIndexY * SIDE_SIZE, SIDE_SIZE, SIDE_SIZE);
				gui.drawTexturedModalRect(side.getX() + SIDE_ITEM_OFFSET, side.getY() + SIDE_ITEM_OFFSET, 132 + (getTextureOffsetFromSide(side) * 16), 0, 16, 16);
				if (hover) {
					gui.drawMouseOver(side.getDescription(side == selectedSide));
				}
			}

			if (selectedTransfer != null && table.getUpgradePage().hasGlobalUpgrade(Upgrade.FILTER)) {
				gui.drawString("Filter", 8, 100, 0x1E1E1E);

				for (int i = 0; i < ItemSetting.ITEM_COUNT; i++) {
					gui.prepare();
					ItemSetting setting = selectedTransfer.getItem(i);
					ItemStack item = setting != null ? setting.getItem() : null;
					int x = ITEM_X + i * ITEM_OFFSET;

					gui.drawItemWithBackground(item, x, ITEM_Y, mX, mY);
				}
			}
		}

		for (CheckBox checkBox : checkBoxes) {
			checkBox.draw(gui, mX, mY);
		}

		for (ArrowScroll arrow : arrows) {
			arrow.draw(gui, mX, mY);
		}
	}

	@Override
	public void onClick(GuiBase gui, int mX, int mY, int button) {
		for (Setting setting : settings) {
			if (gui.inBounds(setting.getX(), setting.getY(), SETTING_SIZE, SETTING_SIZE, mX, mY)) {
				if (setting.isValid()) {
					if (setting.equals(selectedSetting)) {
						selectedSetting = null;
						selectedSide = null;
						selectedTransfer = null;
					} else {
						if (selectedSide != null) {
							Side side = setting.getSides().get(selectedSide.getDirection().ordinal());
							selectedTransfer = selectedTransfer.isInput() ? side.getInput() : side.getOutput();
							selectedSide = side;
						}
						selectedSetting = setting;
					}
				}
				break;
			}
		}

		if (selectedSetting != null) {
			for (Side side : selectedSetting.getSides()) {
				if (gui.inBounds(side.getX(), side.getY(), SIDE_SIZE, SIDE_SIZE, mX, mY)) {
					if (selectMode) {
						if (side.equals(selectedSide)) {
							selectedSide = null;
							selectedTransfer = null;
						} else {
							if (selectedTransfer == null) {
								selectedTransfer = side.getInput();
							} else {
								selectedTransfer = selectedTransfer.isInput() ? side.getInput() : side.getOutput();
							}
							selectedSide = side;
						}

					} else {
						boolean input = side.isInputEnabled();
						boolean output = side.isOutputEnabled();

						int id = (output ? 2 : 0) + (input ? 1 : 0);
						id += button == 0 ? 1 : -1;
						if (id < 0) {
							id += 4;
						} else {
							id %= 4;
						}

						boolean newInput = (id & 1) != 0;
						boolean newOutput = (id & 2) != 0;
						if (newInput != input) {
							side.setInputEnabled(newInput);
							table.updateServer(DataType.SIDE_ENABLED,
								DataSide.getId(selectedSetting, side, side.getInput()));
						}
						if (newOutput != output) {
							side.setOutputEnabled(newOutput);
							table.updateServer(DataType.SIDE_ENABLED,
								DataSide.getId(selectedSetting, side, side.getOutput()));
						}

						table.onSideChange();
					}
					break;
				}
			}

			if (selectedTransfer != null && table.getUpgradePage().hasGlobalUpgrade(Upgrade.FILTER)) {
				for (int i = 0; i < ItemSetting.ITEM_COUNT; i++) {
					if (gui.inBounds(ITEM_X + i * ITEM_OFFSET, ITEM_Y, ITEM_SIZE, ITEM_SIZE, mX, mY)) {
						EntityPlayer player = getPlayer();
						ItemStack itemStack = player.inventory.getItemStack();
						if (itemStack.isEmpty()) {
							table.setMenu(new GuiMenuItem(table, selectedTransfer.getItem(i)));
						} else {
							itemStack = itemStack.copy();
							itemStack.setCount(1);
							selectedTransfer.getItem(i).setItem(itemStack);
							table.updateServer(DataType.SIDE_FILTER, getSyncId(selectedTransfer.getItem(i)));
						}
						break;
					}
				}
			}
		}

		for (CheckBox checkBox : checkBoxes) {
			checkBox.onClick(gui, mX, mY);
		}

		for (ArrowScroll arrow : arrows) {
			arrow.onClick(gui, mX, mY);
		}
	}

	@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
	private EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().player;
	}

	@Override
	public void onRelease(GuiTable gui, int mX, int mY, int button) {
		arrows.forEach(ArrowScroll::onRelease);
	}

	public List<Setting> getSettings() {
		return settings;
	}

	@Override
	public String getDesc() {
		return "Manage Logistics";
	}

	// EnumFacing
	private int getInterfaceX(EnumFacing enumFacing) {
		switch (enumFacing) {
			case DOWN: // BOTTOM
				return 1;
			case EAST: // RIGHT
				return 0;
			case NORTH: // BACK
				return 3;
			case SOUTH: // FRONT
				return 1;
			case UP: // TOP
				return 1;
			case WEST: // LEFT
				return 2;
		}
		return -1;
	}

	private int getInterfaceY(EnumFacing enumFacing) {
		switch (enumFacing) {
			case DOWN: // BOTTOM
				return 2;
			case EAST: // RIGHT
				return 1;
			case NORTH: // BACK
				return 1;
			case SOUTH: // FRONT
				return 1;
			case UP: // TOP
				return 0;
			case WEST: // LEFT
				return 1;
		}
		return -1;
	}

	private int getTextureOffsetFromSide(Side side) {
		switch (side.getDirection()) {
			case WEST:
				return 0;
			case SOUTH:
				return 1;
			case UP:
				return 2;
			case DOWN:
				return 3;
			case NORTH:
				return 4;
			case EAST:
				return 5;
		}
		return -1;
	}
}
