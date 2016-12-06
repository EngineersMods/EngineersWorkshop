package engineers.workshop.gui.page;

import engineers.workshop.gui.GuiBase;
import engineers.workshop.gui.GuiTable;
import engineers.workshop.gui.container.slot.SlotBase;
import engineers.workshop.table.TileTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Page {
    private String name;
    protected TileTable table;
    private int id;


    public Page(TileTable table, String name) {
        this.id = table.getPages().size();
        this.table = table;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract int createSlots(int id);

    protected void addSlot(SlotBase slot) {
        table.addSlot(slot);
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiBase gui, int mX, int mY) {
        gui.drawString(name, 8, 6, 0x404040);
    }
    @SideOnly(Side.CLIENT)
    public void onClick(GuiBase gui, int mX, int mY, int button) {}

    public int getId() {
        return id;
    }

    public void onUpdate() {}

    @SideOnly(Side.CLIENT)
    public void onRelease(GuiTable gui, int mX, int mY, int button) {}
}
