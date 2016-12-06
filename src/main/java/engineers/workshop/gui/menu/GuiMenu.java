package engineers.workshop.gui.menu;


import java.util.ArrayList;
import java.util.List;

import engineers.workshop.gui.GuiBase;
import engineers.workshop.gui.component.Button;
import engineers.workshop.table.TileTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class GuiMenu {

    protected List<Button> buttons;
    protected TileTable table;

    public GuiMenu(TileTable table) {
        this.table = table;
        buttons = new ArrayList<Button>();
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
    public void onRelease(GuiBase gui, int mX, int mY) {

    }

    @SideOnly(Side.CLIENT)
    public void onKeyStroke(GuiBase gui, char c, int k) {

    }

    protected abstract void save();

    protected void close() {
        table.setMenu(null);
    }
}
