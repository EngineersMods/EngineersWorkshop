package engineers.workshop.client.gui.menu;

import engineers.workshop.client.gui.GuiBase;
import engineers.workshop.client.gui.component.Button;
import engineers.workshop.common.table.TileTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiMenu {

    protected List<Button> buttons;
    protected TileTable table;

    public GuiMenu(TileTable table) {
        this.table = table;
        buttons = new ArrayList<>();
        buttons.add(new Button("Save", 150, 230) {
            @Override
            public void clicked() {
                save();
                close();
            }
        });

        buttons.add(new Button("Cancel", 200, 230) {
            @Override
            public void clicked() {
                close();
            }
        });
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiBase gui, int mX, int mY) {
        for (Button button : buttons) {
            button.draw(gui, mX, mY);
        }
    }

    @SideOnly(Side.CLIENT)
    public void onClick(GuiBase gui, int mX, int mY) {
        for (Button button : buttons) {
            button.onClick(gui, mX, mY);
        }
    }

    @SideOnly(Side.CLIENT)
    public void onRelease(GuiBase gui, int mX, int mY) {}

    @SideOnly(Side.CLIENT)
    public void onKeyStroke(GuiBase gui, char c, int k) {}

    protected abstract void save();

    protected void close() {
        table.setMenu(null);
    }
}
