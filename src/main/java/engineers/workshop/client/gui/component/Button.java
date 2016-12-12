package engineers.workshop.client.gui.component;

import engineers.workshop.client.gui.GuiBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Button {

	private String text;
	private int x;
	private int y;

	protected Button(String text, int x, int y) {
		this.text = text;
		this.x = x;
		this.y = y;
	}

	private static final int WIDTH = 42;
	private static final int HEIGHT = 16;
	private static final int SRC_X = 42;
	private static final int SRC_Y = 44;
	private static final int TEXT_Y = 7;

	@SideOnly(Side.CLIENT)
	public void draw(GuiBase gui, int mX, int mY) {
		if (isVisible()) {
			gui.prepare();
			boolean hover = gui.inBounds(x, y, WIDTH, HEIGHT, mX, mY);
			gui.drawRect(x, y, SRC_X + (hover ? WIDTH : 0), SRC_Y, WIDTH, HEIGHT);
			gui.drawCenteredString(text, x, y + TEXT_Y, WIDTH, 0.7F, hover ? 0x1F1F1F : 0x1E1E1E);
		}
	}

	@SideOnly(Side.CLIENT)
	public void onClick(GuiBase gui, int mX, int mY) {
		if (isVisible() && gui.inBounds(x, y, WIDTH, HEIGHT, mX, mY)) {
			clicked();
		}
	}

	public abstract void clicked();

	public boolean isVisible() {
		return true;
	}
}
