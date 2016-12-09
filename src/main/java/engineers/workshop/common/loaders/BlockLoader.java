package engineers.workshop.common.loaders;

import engineers.workshop.common.table.BlockTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLoader {

    public static BlockTable blockTable;

    public static void registerBlocks() {
        blockTable = new BlockTable();
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        blockTable.registerModel();
    }
}
