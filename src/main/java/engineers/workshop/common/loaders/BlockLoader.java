package engineers.workshop.common.loaders;

import engineers.workshop.common.table.BlockTable;
import net.minecraft.block.Block;

public class BlockLoader {

    public static Block blockTable;

    public static void registerBlocks() {
        blockTable = new BlockTable();
    }
}
