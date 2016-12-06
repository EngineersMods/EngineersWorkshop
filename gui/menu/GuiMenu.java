package us.engineersworkshop.gui.menu;


import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.engineersworkshop.gui.GuiBase;
import us.engineersworkshop.gui.component.Button;
import us.engineersworkshop.tileentity.TileEntityTable;

public abstract class GuiMenu {

    protected List<Button> buttons;
    protected TileEntityTable table;

    public GuiMenu(TileEntityTable table) {
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
