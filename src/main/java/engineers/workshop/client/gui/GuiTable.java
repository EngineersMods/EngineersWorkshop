package engineers.workshop.client.gui;

import engineers.workshop.client.container.ContainerTable;
import engineers.workshop.client.container.slot.SlotBase;
import engineers.workshop.client.page.Page;
import engineers.workshop.common.network.packets.PacketHandler;
import engineers.workshop.common.network.packets.PacketId;
import engineers.workshop.common.network.data.DataType;
import engineers.workshop.common.table.TileTable;
import engineers.workshop.common.util.helpers.ColorHelper;
import engineers.workshop.common.util.helpers.FormattingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiTable extends GuiBase {

	private static final int HEADER_SRC_X = 0;
	private static final int HEADER_SRC_Y = 0;
	private static final int HEADER_FULL_WIDTH = 42;
	private static final int HEADER_WIDTH = 38;
	private static final int HEADER_HEIGHT = 17;
	private static final int HEADER_X = 3;
	private static final int HEADER_Y = 173;
	private static final int HEADER_TEXT_Y = 7;
	private static final int SLOT_SRC_X = 42;
	private static final int SLOT_SRC_Y = 0;
	private static final int SLOT_SIZE = 18;
	private static final int SLOT_OFFSET = -1;
	private static final int SLOT_BIG_SIZE = 26;
	private static final int SLOT_BIG_OFFSET = SLOT_OFFSET - (SLOT_BIG_SIZE - SLOT_SIZE) / 2;
	private static final int POWER_X = 225;
	private static final int POWER_Y = 173;
	private static final int POWER_WIDTH = 18;
	private static final int POWER_HEIGHT = 50;
	private static final int POWER_INNER_WIDTH = 16;
	private static final int POWER_INNER_HEIGHT = 48;
	private static final int POWER_INNER_SRC_X = 0;
	private static final int POWER_INNER_SRC_Y = 64;
	private static final int POWER_SRC_X = 32;
	private static final int POWER_SRC_Y = 62;
	private static final int POWER_INNER_OFFSET_X = (POWER_WIDTH - POWER_INNER_WIDTH) / 2;
	private static final int POWER_INNER_OFFSET_Y = (POWER_HEIGHT - POWER_INNER_HEIGHT) / 2;
	private TileTable table;
	private List<SlotBase> slots;
	private ContainerTable containerTable;
	private boolean closed = true;

	public GuiTable(TileTable table, EntityPlayer player) {
		super(new ContainerTable(table, player));

		xSize = 256;
		ySize = 256;
		slots = new ArrayList<>();

		for (Object obj : inventorySlots.inventorySlots) {
			SlotBase slot = (SlotBase) obj;
			slots.add(slot);
			slot.updateClient(slot.isVisible());
		}

		this.table = table;
		this.containerTable = (ContainerTable) this.inventorySlots;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mX, int mY) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft, guiTop, 0);
		mX -= guiLeft;
		mY -= guiTop;

		mc.getTextureManager().bindTexture(BACKGROUND);
		GlStateManager.color(1F, 1F, 1F);
		drawTexturedModalRect(0, 0, 0, 0, xSize, ySize);

		drawSlots();
		if (table.getMenu() == null) {
			drawPageHeaders(mX, mY);
			drawPower(mX, mY);
			table.getSelectedPage().draw(this, mX, mY);
		} else {
			table.getMenu().draw(this, mX, mY);
		}
		GlStateManager.popMatrix();
	}

	@Override
	protected void mouseClicked(int mX, int mY, int button) throws IOException {
		super.mouseClicked(mX, mY, button);
		mX -= guiLeft;
		mY -= guiTop;

		if (table.getMenu() == null) {
			clickPageHeader(mX, mY);
			table.getSelectedPage().onClick(this, mX, mY, button);
		} else {
			table.getMenu().onClick(this, mX, mY);
		}
	}

	@Override
	protected void keyTyped(char c, int k) throws IOException {
		if (table.getMenu() == null) {
			super.keyTyped(c, k);
		} else {
			if (k == 1) {
				this.mc.player.closeScreen();
			} else {
				table.getMenu().onKeyStroke(this, c, k);
			}
		}
	}

	private void drawPageHeaders(int mX, int mY) {
		for (int i = 0; i < table.getPages().size(); i++) {
			Page page = table.getPages().get(i);

			boolean selected = page.equals(table.getSelectedPage());
			int srcY = selected ? HEADER_SRC_Y + HEADER_HEIGHT : HEADER_SRC_Y;
			int y = HEADER_Y + HEADER_HEIGHT * i;
			boolean hover = inBounds(HEADER_X, y, HEADER_FULL_WIDTH, HEADER_HEIGHT, mX, mY);
			if (hover) {
				drawMouseOver(page.getDesc());
			}
			int width = hover ? HEADER_FULL_WIDTH : HEADER_WIDTH;
			int offset = HEADER_FULL_WIDTH - width;

			prepare();
			drawRect(HEADER_X, y, HEADER_SRC_X + offset, srcY, width, HEADER_HEIGHT);

			int invertedOffset = (HEADER_FULL_WIDTH - HEADER_WIDTH) - offset;
			drawCenteredString(page.getName(), HEADER_X + invertedOffset, y + HEADER_TEXT_Y, HEADER_WIDTH, 0.7F, 0x2E2E2E);
		}
	}

	private void clickPageHeader(int mX, int mY) {
		for (int i = 0; i < table.getPages().size(); i++) {
			Page page = table.getPages().get(i);
			int y = HEADER_Y + HEADER_HEIGHT * i;
			if (inBounds(HEADER_X, y, HEADER_FULL_WIDTH, HEADER_HEIGHT, mX, mY)) {
				table.setSelectedPage(page);
				table.updateServer(DataType.PAGE);
				break;
			}
		}
	}

	private void drawSlots() {
		prepare();
		for (SlotBase slot : slots) {
			boolean visible = slot.isVisible();
			slot.updateClient(visible);
			if (visible) {
				boolean isBig = slot.isBig();
				int srcY = isBig ? SLOT_SIZE + SLOT_SRC_Y : SLOT_SRC_Y;
				int size = isBig ? SLOT_BIG_SIZE : SLOT_SIZE;
				int offset = isBig ? SLOT_BIG_OFFSET : SLOT_OFFSET;

				drawRect(slot.getX() + offset, slot.getY() + offset, SLOT_SRC_X + slot.getTextureIndex(this) * size, srcY, size, size);
			}
		}
	}

	private void drawPower(int mX, int mY) {
		prepare();

		drawRect(POWER_X + POWER_INNER_OFFSET_X, POWER_Y + POWER_INNER_OFFSET_Y, POWER_INNER_SRC_X + POWER_INNER_WIDTH, POWER_INNER_SRC_Y, POWER_INNER_WIDTH, POWER_INNER_HEIGHT);

		int height = POWER_INNER_HEIGHT * containerTable.power / table.maxFuel;
		int offset = POWER_INNER_HEIGHT - height;
		GlStateManager.color(ColorHelper.getRed(containerTable.power, getTable().maxFuel), ColorHelper.getGreen(containerTable.power, getTable().maxFuel), ColorHelper.getBlue(containerTable.power, getTable().maxFuel));
		drawRect(POWER_X + POWER_INNER_OFFSET_X, POWER_Y + POWER_INNER_OFFSET_Y + offset, POWER_INNER_SRC_X, POWER_INNER_SRC_Y + offset, POWER_INNER_WIDTH, height);
		drawRect(POWER_X, POWER_Y + POWER_INNER_OFFSET_Y + offset - 1, POWER_SRC_X, POWER_SRC_Y - 1, POWER_WIDTH, 1);
		int srcX = POWER_SRC_X;
		boolean hover = inBounds(POWER_X, POWER_Y, POWER_WIDTH, POWER_HEIGHT, mX, mY);
		if (hover)
			srcX += POWER_WIDTH;
		drawRect(POWER_X, POWER_Y, srcX, POWER_SRC_Y, POWER_WIDTH, POWER_HEIGHT);
		GlStateManager.color(1.0f, 1.0f, 1.0f);

		if (hover) {
			String str = ColorHelper.getPowerColor(containerTable.power, getTable().maxFuel) + "Fuel: " + FormattingHelper.formatNumber(containerTable.power) + " / " + FormattingHelper.formatNumber((int) table.maxFuel);
			drawMouseOver(str);
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		if (!closed) {
			PacketHandler.sendToServer(PacketHandler.getPacket(table, PacketId.CLOSE));
			closed = true;
		}
	}

	@Override
	public void setWorldAndResolution(Minecraft minecraft, int width, int height) {
		super.setWorldAndResolution(minecraft, width, height);
		if (closed) {
			closed = false;
			PacketHandler.sendToServer(PacketHandler.getPacket(table, PacketId.RE_OPEN));
		}
	}

	public TileTable getTable() {
		return table;
	}
}
