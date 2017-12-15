package engineers.workshop.client.component;

import engineers.workshop.client.gui.GuiBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CheckBox {

	private static final int SIZE = 8;
	private static final int SRC_X = 26;
	private static final int SRC_Y = 34;
	private static final int TEXT_X = 12;
	private static final int TEXT_Y = 2;

	private int x;
	private int y;
	private String text;

	public CheckBox(String text, int x, int y) {
		this.x = x;
		this.y = y;
		this.text = text;
	}

	@SideOnly(Side.CLIENT)
	public void draw(GuiBase gui, int mX, int mY) {
		if (isVisible()) {
			gui.prepare();
			boolean hover = gui.inBounds(x, y, SIZE, SIZE, mX, mY);
			boolean checked = getValue();
			gui.drawRect(x, y, SRC_X + (checked ? SIZE : 0), SRC_Y + (hover ? SIZE : 0), SIZE, SIZE);
			gui.drawString(text, x + TEXT_X, y + TEXT_Y, 0.7F, 0x1E1E1E);
		}
	}

	@SideOnly(Side.CLIENT)
	public void onClick(GuiBase gui, int mX, int mY) {
		if (isVisible() && gui.inBounds(x, y, SIZE, SIZE, mX, mY)) {
			setValue(!getValue());
			onUpdate();
		}
	}

	public abstract boolean getValue();

	public abstract void setValue(boolean value);

	public void onUpdate() {
	}

	public boolean isVisible() {
		return true;
	}
}
